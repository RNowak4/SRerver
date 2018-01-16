package server.rest;

import org.springframework.web.bind.annotation.*;
import server.files.api.IFilesManager;

import java.time.LocalDateTime;

@RestController
public class ClientController {
    private IFilesManager filesManager;

    public ClientController(IFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE, path = "/files/{fileName}/records/{recordId}/clients")
    public void removeFromQueue(@PathVariable("fileName") final String fileName,
                                @PathVariable("recordId") final String recordId,
                                @RequestParam("client_id") final String clientId,
                                @RequestParam("timestamp") final LocalDateTime localDateTime) {

        filesManager.removeFromQueue(fileName, recordId, clientId, localDateTime);
    }
}
