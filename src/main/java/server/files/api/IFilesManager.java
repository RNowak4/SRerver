package server.files.api;

import io.vavr.collection.List;
import io.vavr.control.Try;
import server.files.Record;
import server.files.ServerFile;

public interface IFilesManager {

    List<ServerFile> getAllFiles();

    Try<Void> createNew(String fileName);

    void delete(String fileId);

    Try<String> createNewRecord(String fileId, String userId, String content);

    Try<Record> modifyRecord(String fileId, String recordId, String userId, String content);

    Try<Void> deleteRecord(String fileId, String recordId, String userId);

    List<Record> getRecordsForFile(String fileName);

    void lockRecord(String filename, String recordId, String userName);

    void unlockRecord(String filename, String recordId, String userName);

    void addOpenedBy(String userId, String file);

    void removeOpenedBy(String userId, String file);
}
