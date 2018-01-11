package server.rest;

import org.springframework.web.bind.annotation.*;
import server.files.api.IFilesManager;
import server.files.api.IRecordsManager;
import server.files.api.RecordDescriptor;
import server.files.ServerFile;

import java.util.List;

@RestController
@RequestMapping(path = "/files")
public class FilesController {
    private IFilesManager filesManager;
    private IRecordsManager recordsManager;

    public FilesController(final IFilesManager filesManager,
                           final IRecordsManager recordsManager) {
        this.filesManager = filesManager;
        this.recordsManager = recordsManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<String> getAllFiles() {
        return filesManager.getAllFiles().toStream()
                .map(ServerFile::getName)
                .toJavaList();
    }

    @RequestMapping(method = RequestMethod.POST)
    public void addNewFile(@RequestParam("name") final String name  ) {
        filesManager.createNew(name).get();
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    public void deleteFile(@PathVariable("id") final String fileId) {
        filesManager.delete(fileId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id}/records")
    public List<RecordDescriptor> getAllRecords(@PathVariable("id") final String fileId) {
        return recordsManager.getRecordsForFile(fileId).toJavaList();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{id}/records")
    public void addNewRecord(@PathVariable("id") final String fileId,
                             final RecordDescriptor recordDescriptor) {
        recordsManager.createNewRecord(fileId, recordDescriptor);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{id}/records/{recordId}")
    public void modifyRecord(@PathVariable("id") final String fileId,
                             @PathVariable("recordId") final String recordId,
                             final RecordDescriptor recordDescriptor) {
        recordsManager.modifyRecord(fileId, recordId, recordDescriptor);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}/records/{recordId}")
    public void deleteRecord(@PathVariable("id") final String fileId,
                             @PathVariable("recordId") final String recordId) {
        recordsManager.deleteRecord(fileId, recordId);
    }
}
