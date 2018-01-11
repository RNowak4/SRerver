package server.files;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.springframework.stereotype.Component;
import server.files.api.IFilesManager;
import server.utils.HasLogger;

@Component
class FilesManager implements HasLogger, IFilesManager {
    private final SystemFileManager systemFileManager;
    private Map<String, ServerFile> filesMap = HashMap.empty();

    FilesManager(final SystemFileManager systemFileManager) {
        this.systemFileManager = systemFileManager;
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
    public void createNewRecord(String fileId, String userId, String content) {
        filesMap.get(fileId)
                .forEach(serverFile -> serverFile.createRecord(content.toCharArray()));
    }

    @Override
    public void modifyRecord(String fileId, String recordId, String userId, String content) {
        filesMap.get(fileId)
                .forEach(serverFile -> serverFile.modifyRecord(recordId, userId, content));
    }

    @Override
    public void deleteRecord(String fileId, String recordId, String userId) {
        filesMap.get(fileId)
                .map(serverFile -> serverFile.deleteRecord(recordId, userId));
    }

    @Override
    public List<Record> getRecordsForFile(String fileName) {
        return filesMap.get(fileName)
                .map(ServerFile::getRecords)
                .getOrElse(List.empty());
    }

}