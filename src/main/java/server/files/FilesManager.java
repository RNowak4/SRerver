package server.files;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
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

import java.time.LocalDateTime;

@Component
class FilesManager implements HasLogger, IFilesManager {
    private final SystemFileManager systemFileManager;
    private final Bootstrap bootstrap;
    private Map<String, ServerFile> filesMap = HashMap.empty();

    FilesManager(final SystemFileManager systemFileManager, Bootstrap bootstrap) {
        this.systemFileManager = systemFileManager;
        this.bootstrap = bootstrap;
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
                        .onSuccess(record -> {
                            systemFileManager.addRecord(serverFile, record);
                            notifyListeningClients(serverFile, new RecordUpdateMessage("RECORD_CREATED",
                                    record.getId(),
                                    String.valueOf(record.getData()),
                                    userId));
                        })
                        .onFailure(th -> getLogger().error("Error while creating record.", th))
                )
                .map(Record::getId);
    }

    @Override
    public Try<Record> modifyRecord(String fileId, String recordId, String userId, String content) {
        return filesMap.get(fileId)
                .toTry()
                .flatMap(serverFile -> serverFile.modifyRecord(recordId, userId, content)
                        .onSuccess(record -> {
                            systemFileManager.modifyRecord(serverFile, record, content.toCharArray());
                            notifyListeningClients(serverFile, new RecordUpdateMessage("RECORD_UPDATED",
                                    recordId,
                                    content,
                                    userId));
                        })
                        .onFailure(th -> getLogger().error("Error while modifyind record: {}.", recordId)));
    }

    @Override
    public Try<Record> deleteRecord(String fileId, String recordId, String userId) {
        return filesMap.get(fileId)
                .toTry()
                .flatMap(serverFile -> serverFile.deleteRecord(recordId, userId)
                        .onSuccess(record -> {
                                    systemFileManager.removeRecord(serverFile, record);
                                    notifyListeningClients(serverFile,
                                            new RecordRemovedMessage("RECORD_REMOVED",
                                                    recordId,
                                                    fileId));
                                }
                        ));
    }

    private void notifyListeningClients(ServerFile serverFile, Message message) {
        serverFile.getOpenedByUserIds().toStream()
                .forEach(userId -> bootstrap.getServer().getRoomOperations(userId).sendEvent("record_state_change", message));
    }

    @Override
    public List<Record> getRecordsForFile(String fileName) {
        return filesMap.get(fileName)
                .map(ServerFile::getRecords)
                .getOrElse(List.empty());
    }

    @Override
    public void lockRecord(final String filename, final String recordId, final String userName) {
        filesMap.get(filename)
                .peek(serverFile -> serverFile.lockRecord(userName, recordId))
                .forEach(serverFile ->
                        bootstrap.getServer().getRoomOperations(userName).sendEvent("record_state_change",
                                new LockAssignedMessage("LOCK_ASSIGNED", recordId, filename)));
    }

    @Override
    public void unlockRecord(final String filename, final String recordId, final String userName) {
        filesMap.get(filename)
                .peek(serverFile -> serverFile.unlockRecord(userName, recordId))
                .forEach(serverFile ->
                        bootstrap.getServer().getRoomOperations(userName).sendEvent("record_state_change",
                                new LockAssignedMessage("LOCK_PICKED_UP", recordId, filename)));
    }

    @Override
    public void addOpenedBy(String userId, String file) {
        filesMap.get(file)
                .forEach(serverFile -> serverFile.addOpenedBy(userId));
    }

    @Override
    public void removeOpenedBy(String userId, String file) {
        filesMap.get(file)
                .forEach(serverFile -> serverFile.removeOpenedBy(userId));
    }

    @Override
    public void removeFromQueue(GraphEdge removed) {
        filesMap.get(removed.getFilename())
                .flatMap(serverFile -> serverFile.getRecord(removed.getRecordId()))
                .forEach(record -> record.removeFromQueue(new WaitingClient(removed.getWaitingUser(), removed.getTimestamp())));
    }

    @Override
    public void removeFromQueue(String fileName, String recordId, String clientId, LocalDateTime localDateTime) {
        filesMap.get(fileName)
                .flatMap(serverFile -> serverFile.getRecord(recordId))
                .forEach(record -> record.removeFromQueue(new WaitingClient(clientId, localDateTime)));
    }
}