package server.rest;

import org.springframework.web.bind.annotation.*;
import server.files.ServerFile;
import server.files.api.IFilesManager;
import server.files.api.RecordDto;

import java.util.List;

@RestController
@RequestMapping(path = "/files")
public class FilesController {
    private IFilesManager filesManager;

    public FilesController(final IFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<String> getAllFiles() {
        return filesManager.getAllFiles().toStream()
                .map(ServerFile::getName)
                .toJavaList();
    }

    @RequestMapping(method = RequestMethod.POST)
    public void addNewFile(@RequestParam("name") final String name) {
        filesManager.createNew(name).get();
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    public void deleteFile(@PathVariable("id") final String fileId) {
        filesManager.delete(fileId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id}/records")
    public List<RecordDto> getAllRecords(@PathVariable("id") final String fileName) {
        return filesManager.getRecordsForFile(fileName).toStream()
                .map(record -> new RecordDto(record.getId(), fileName, record.getData(), "status"))
                .toJavaList();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{id}/records")
    public void addNewRecord(@PathVariable("id") final String fileId,
                             @RequestParam("userId") final String userId,
                             @RequestBody final String content) {
        filesManager.createNewRecord(fileId, userId, content);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{id}/records/{recordId}")
    public void modifyRecord(@PathVariable("id") final String fileId,
                             @PathVariable("recordId") final String recordId,
                             @RequestParam("userId") final String userId,
                             @RequestBody final String content) {
        filesManager.modifyRecord(fileId, recordId, userId, content);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}/records/{recordId}")
    public void deleteRecord(@PathVariable("id") final String fileId,
                             @PathVariable("recordId") final String recordId,
                             @RequestParam("userId") final String userId) {
        filesManager.deleteRecord(fileId, recordId, userId);
    }
}
