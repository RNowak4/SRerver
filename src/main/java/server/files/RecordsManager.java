package server.files;

import io.vavr.collection.List;
import org.springframework.stereotype.Component;
import server.files.api.IRecordsManager;
import server.files.api.RecordDescriptor;

@Component
class RecordsManager implements IRecordsManager {

    // TODO impl

    @Override
    public List<RecordDescriptor> getRecordsForFile(String fileId) {
        return null;
    }

    @Override
    public void createNewRecord(String fileId, RecordDescriptor recordDescriptor) {

    }

    @Override
    public void modifyRecord(String fileId, String recordId, RecordDescriptor recordDescriptor) {

    }

    @Override
    public void deleteRecord(String fileId, String recordId) {

    }

}
