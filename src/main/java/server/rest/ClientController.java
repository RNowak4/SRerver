package server.rest;

import org.springframework.web.bind.annotation.*;
import server.files.api.IFilesManager;
import server.utils.HasLogger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class ClientController implements HasLogger {
    private IFilesManager filesManager;

    public ClientController(IFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE, path = "/files/{fileName}/records/{recordId}/clients")
    public void removeFromQueue(@PathVariable("fileName") final String fileName,
                                @PathVariable("recordId") final String recordId,
                                @RequestParam("client_id") final String clientId,
                                @RequestParam("timestamp") final String localDateTime) {

        getLogger().info("Removing user: {} from record: {} queue in file: {}", clientId, recordId, fileName);
        filesManager.removeFromQueue(fileName, recordId, clientId, LocalDateTime.parse(localDateTime, DateTimeFormatter.ISO_DATE_TIME));
    }
}
