package rest;

import files.api.FileDescriptor;
import files.api.IFilesManager;
import files.api.Record;
import files.api.RecordDescriptor;
import io.vavr.collection.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FilesController {
    private IFilesManager filesManager;

    public FilesController(final IFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/files")
    public List<FileDescriptor> getAllFiles() {
        return filesManager.getAllFiles();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/files")
    public void addNewFile(final FileDescriptor fileDescriptor) {
        filesManager.createNew(fileDescriptor);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/files/{id}")
    public void deleteFile(@PathVariable("id") final String fileId) {
        filesManager.delete(fileId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/files/{id}/records")
    public List<RecordDescriptor> getAllRecords(@PathVariable("id") final String fileId) {
        return filesManager.getRecordsForFile(fileId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/files/{id}/records")
    public void addNewRecord(@PathVariable("id") final String fileId,
                             final RecordDescriptor recordDescriptor) {
        filesManager.createNewRecord(fileId, recordDescriptor);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/files/{id}/records/{recordId}")
    public void modifyRecord(@PathVariable("id") final String fileId,
                             @PathVariable("recordId") final String recordId,
                             final RecordDescriptor recordDescriptor) {
        filesManager.modifyRecord(fileId, recordId, recordDescriptor);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/files/{id}/records/{recordId}")
    public void deleteRecord(@PathVariable("id") final String fileId,
                             @PathVariable("recordId") final String recordId) {
        filesManager.deleteRecord(fileId, recordId);
    }
}
