package server.files;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Try;
import org.springframework.stereotype.Component;
import server.clients.api.Message;
import server.clients.api.messages.LockAssignedMessage;
import server.clients.api.messages.RecordRemovedMessage;
import server.clients.api.messages.RecordUpdateMessage;
import server.config.Bootstrap;
import server.deadlock.GraphEdge;
import server.files.api.IFilesManager;
import server.files.api.WaitingClient;
import server.utils.HasLogger;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Component
class FilesManager implements HasLogger, IFilesManager {
    private final static String RECORD_STATE_CHANGE = "record_state_change";
    private final SystemFileManager systemFileManager;
    private final Bootstrap bootstrap;
    private Map<String, ServerFile> filesMap = HashMap.empty();

    FilesManager(final SystemFileManager systemFileManager, Bootstrap bootstrap) {
        this.systemFileManager = systemFileManager;
        this.bootstrap = bootstrap;
    }

    @PostConstruct
    void init() {
        systemFileManager.init();

        final Set<String> files = systemFileManager.getSystemFilesMap().keySet();

        files.toStream()
                .map(filename -> {
                    final List<Record> records = systemFileManager.getRecordsForFile(filename);
                    final ServerFile serverFile = new ServerFile(filename);
                    serverFile.setRecords(records);
                    return serverFile;
                })
                .forEach(serverFile -> filesMap = filesMap.put(serverFile.getName(), serverFile));
    }

    @Override
    public List<ServerFile> getAllFiles() {
        return filesMap.values().toList();
    }

    @Override
    public Try<Void> createNew(final String fileName) {
        return systemFileManager.newFile(fileName)
                .map(res -> new ServerFile(fileName))
                .onSuccess(fileTuple -> filesMap = filesMap.put(fileName, fileTuple))
                .onSuccess(v -> getLogger().info("Successfully created file: {}.", fileName))
                .onFailure(th -> getLogger().error("Error while trying to create file: {}.", fileName))
                .map(v -> null);
    }

    @Override
    public void delete(String fileId) {
        systemFileManager.deleteFile(fileId)
                .onSuccess(v -> filesMap = filesMap.remove(fileId));
    }

    @Override
    public Try<String> createNewRecord(String fileId, String userId, String content) {
        return filesMap.get(fileId)
                .toTry()
                .flatMap(serverFile -> serverFile.createRecord(content.toCharArray())
                        .onSuccess(record -> systemFileManager.addRecord(serverFile, record)
                                .onSuccess(v -> notifyListeningClients(serverFile, new RecordUpdateMessage("RECORD_CREATED",
                                        record.getId(),
                                        String.valueOf(record.getData()),
                                        userId))))
                        .onFailure(th -> getLogger().error("Error while creating record.", th))
                )
                .map(Record::getId);
    }

    @Override
    public Try<Record> modifyRecord(final String fileId, final String recordId, final String userId, final String content) {
        return filesMap.get(fileId)
                .toTry()
                .flatMap(serverFile -> serverFile.modifyRecord(recordId, userId, content)
                        .onSuccess(record -> systemFileManager.modifyRecord(serverFile, record, content.toCharArray())
                                .onSuccess(v -> notifyListeningClients(serverFile, new RecordUpdateMessage("RECORD_UPDATED",
                                        recordId,
                                        content,
                                        userId))))
                        .onFailure(th -> getLogger().error("Error while modifyind record: {}.", recordId)));
    }

    @Override
    public Try<Record> deleteRecord(String fileId, String recordId, String userId) {
        return filesMap.get(fileId)
                .toTry()
                .flatMap(serverFile -> serverFile.deleteRecord(recordId, userId)
                        .onSuccess(record ->
                                systemFileManager.removeRecord(serverFile, record)
                                        .onSuccess(v -> notifyListeningClients(serverFile,
                                                new RecordRemovedMessage("RECORD_REMOVED",
                                                        recordId,
                                                        fileId)))

                        ))
                .onSuccess(v -> getLogger().info("Successfully deleted record: {} from file: {}", recordId, fileId))
                .onFailure(th -> getLogger().error("Error while trying to delete record: {} from file: {}", recordId, fileId, th));
    }

    private void notifyListeningClients(final ServerFile serverFile, final Message message) {
        getLogger().debug("Notifying clients of file: {} with message: {}", serverFile.getName(), message);
        serverFile.getOpenedByUserIds().toStream()
                .forEach(userId -> bootstrap.getServer().getRoomOperations(userId).sendEvent(RECORD_STATE_CHANGE, message));
    }

    @Override
    public List<Record> getRecordsForFile(final String fileName) {
        return filesMap.get(fileName)
                .map(ServerFile::getRecords)
                .getOrElse(List.empty());
    }

    @Override
    public void lockRecord(final String filename, final String recordId, final String userName) {
        filesMap.get(filename)
                .peek(serverFile -> serverFile.lockRecord(userName, recordId))
                .toTry()
                .peek(serverFile ->
                        bootstrap.getServer().getRoomOperations(userName).sendEvent(RECORD_STATE_CHANGE,
                                new LockAssignedMessage("LOCK_ASSIGNED", recordId, filename)))
                .onSuccess(v -> getLogger().info("Successfully set lock to record: {} in file: {} by user: {}", recordId, filename, userName));
    }

    @Override
    public void unlockRecord(final String filename, final String recordId, final String userName) {
        filesMap.get(filename)
                .peek(serverFile -> serverFile.unlockRecord(userName, recordId))
                .toTry()
                .peek(serverFile ->
                        bootstrap.getServer().getRoomOperations(userName).sendEvent(RECORD_STATE_CHANGE,
                                new LockAssignedMessage("LOCK_PICKED_UP", recordId, filename)))
                .onSuccess(v -> getLogger().info("Successfully unlocked record: {} from file: {} by user: {}", recordId, filename, userName));
    }

    @Override
    public void addOpenedBy(final String userId, final String file) {
        getLogger().debug("Added opened by: {} file: {}", userId, file);
        filesMap.get(file)
                .forEach(serverFile -> serverFile.addOpenedBy(userId));
    }

    @Override
    public void removeOpenedBy(final String userId, final String file) {
        getLogger().debug("Removed opened by: {} file: {}", userId, file);
        filesMap.get(file)
                .forEach(serverFile -> serverFile.removeOpenedBy(userId));
    }

    @Override
    public void removeFromQueue(final GraphEdge removed) {
        removeFromQueue(removed.getFilename(), removed.getRecordId(), removed.getWaitingUser(), removed.getTimestamp());
    }

    @Override
    public void removeFromQueue(final String fileName, final String recordId,
                                final String clientId, final LocalDateTime localDateTime) {
        getLogger().info("Removing user: {} from queue at file: {} record: {}.", clientId, fileName, recordId);

        filesMap.get(fileName)
                .flatMap(serverFile -> serverFile.getRecord(recordId))
                .forEach(record -> {
                    record.removeFromQueue(new WaitingClient(clientId, localDateTime));
                    bootstrap.getServer().getRoomOperations(clientId)
                            .sendEvent(RECORD_STATE_CHANGE, new LockAssignedMessage("LOCK_REJECTED", recordId, fileName));
                });
    }
}