package server.rest;

import org.springframework.web.bind.annotation.*;
import server.clients.api.messages.ContentMessage;
import server.clients.api.messages.FileCreateMessage;
import server.clients.api.messages.RecordCreatedMessage;
import server.clients.api.messages.SimpleMessage;
import server.files.ServerFile;
import server.files.api.IFilesManager;
import server.files.api.RecordDto;
import server.files.api.WaitingClient;

import java.util.List;

@RestController
@RequestMapping(path = "/files")
public class FilesController {
    private IFilesManager filesManager;

    public FilesController(final IFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    public List<String> getAllFiles() {
        return filesManager.getAllFiles().toStream()
                .map(ServerFile::getName)
                .toJavaList();
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public SimpleMessage addNewFile(@RequestBody final FileCreateMessage msg) {
        final String name = msg.getFilename();
        filesManager.createNew(name).get();

        return new SimpleMessage("Successfully created file: " + name);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    public void deleteFile(@PathVariable("id") final String fileId) {
        filesManager.delete(fileId);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/{id}/records")
    public List<RecordDto> getAllRecords(@PathVariable("id") final String fileName) {
        return filesManager.getRecordsForFile(fileName).toStream()
                .map(record -> new RecordDto(record.getId(), record.getLockedBy().get().map(WaitingClient::getUserId).getOrNull(), fileName, String.valueOf(record.getData()).replaceAll("\0+", ""), "status"))
                .toJavaList();
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/{id}/records")
    public RecordCreatedMessage addNewRecord(@PathVariable("id") final String fileId,
                                             @RequestHeader("userId") final String userId,
                                             @RequestBody final ContentMessage content) {
        return filesManager.createNewRecord(fileId, userId, content.getContent())
                .map(recordId -> new RecordCreatedMessage("Success", recordId))
                .getOrElseThrow(() -> new RuntimeException("Error while creating new record."));
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT, path = "/{id}/records/{recordId}")
    public SimpleMessage modifyRecord(@PathVariable("id") final String fileId,
                                      @PathVariable("recordId") final String recordId,
                                      @RequestHeader("userId") final String userId,
                                      @RequestBody final ContentMessage content) {
        return filesManager.modifyRecord(fileId, recordId, userId, content.getContent())
                .map(serverFile -> new SimpleMessage("Successfully modified record: " + recordId))
                .getOrElseThrow(() -> new RuntimeException("Error while modifying record: " + recordId));
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}/records/{recordId}")
    public SimpleMessage deleteRecord(@PathVariable("id") final String fileId,
                                      @PathVariable("recordId") final String recordId,
                                      @RequestHeader("userId") final String userId) {
        return filesManager.deleteRecord(fileId, recordId, userId)
                .map(serverFile -> new SimpleMessage("Successfully deleted record: " + recordId))
                .getOrElseThrow(() -> new RuntimeException("Error while trying to delete record: " + recordId));
    }
}
