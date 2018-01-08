package files;

import files.api.FileDescriptor;
import files.api.IFilesManager;
import files.api.RecordDescriptor;
import io.vavr.collection.List;
import org.springframework.stereotype.Service;

@Service
class FilesManager implements IFilesManager{

    @Override
    public List<FileDescriptor> getAllFiles() {
        return null;
    }

    @Override
    public void createNew(FileDescriptor fileDescriptor) {

    }

    @Override
    public void delete(String fileId) {

    }

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
