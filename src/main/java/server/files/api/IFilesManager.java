package server.files.api;

import io.vavr.collection.List;
import io.vavr.control.Try;
import server.files.Record;
import server.files.ServerFile;

public interface IFilesManager {

    List<ServerFile> getAllFiles();

    Try<Void> createNew(String fileName);

    void delete(String fileId);

    void createNewRecord(String fileId, String userId, String content);

    void modifyRecord(String fileId, String recordId, String userId, String content);

    void deleteRecord(String fileId, String recordId, String userId);

    List<Record> getRecordsForFile(String fileName);
}
