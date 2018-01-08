package files.api;

import io.vavr.collection.List;

public interface IFilesManager {
    List<FileDescriptor> getAllFiles();

    void createNew(FileDescriptor fileDescriptor);

    void delete(String fileId);

    List<RecordDescriptor> getRecordsForFile(String fileId);

    void createNewRecord(String fileId, RecordDescriptor recordDescriptor);

    void modifyRecord(String fileId, String recordId, RecordDescriptor recordDescriptor);

    void deleteRecord(String fileId, String recordId);
}
