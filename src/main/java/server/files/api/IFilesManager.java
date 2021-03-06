package server.files.api;

import io.vavr.collection.List;
import io.vavr.control.Try;
import server.deadlock.GraphEdge;
import server.files.Record;
import server.files.ServerFile;

import java.time.LocalDateTime;

public interface IFilesManager {

    List<ServerFile> getAllFiles();

    Try<Void> createNew(String fileName);

    void delete(String fileId);

    Try<String> createNewRecord(String fileId, String userId, String content);

    Try<Record> modifyRecord(String fileId, String recordId, String userId, String content);

    Try<Record> deleteRecord(String fileId, String recordId, String userId);

    List<Record> getRecordsForFile(String fileName);

    void lockRecord(String filename, String recordId, String userName);

    void unlockRecord(String filename, String recordId, String userName);

    void addOpenedBy(String userId, String file);

    void removeOpenedBy(String userId, String file);

    void removeFromQueue(GraphEdge removed);

    void removeFromQueue(String fileName, String recordId, String clientId, LocalDateTime localDateTime);

    void removeUserFromSystem(String username);
}
