package server.files.api;

import io.vavr.collection.List;

public interface IRecordsManager {

    List<RecordDescriptor> getRecordsForFile(String fileId);

    void createNewRecord(String fileId, RecordDescriptor recordDescriptor);

    void modifyRecord(String fileId, String recordId, RecordDescriptor recordDescriptor);

    void deleteRecord(String fileId, String recordId);
}
