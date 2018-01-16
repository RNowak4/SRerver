package server.rest;

import org.springframework.web.bind.annotation.*;
import server.clients.api.IClientManager;
import server.clients.api.Sid;
import server.files.api.IFilesManager;

import java.time.LocalDateTime;

@RestController
public class ClientController {
    private IClientManager clientManager;
    private IFilesManager filesManager;

    @RequestMapping(method = RequestMethod.POST, path = "/server/clients")
    public void registerClient(final Sid sid) {
        clientManager.registerClient(sid);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/server/clients/{sid}")
    public void removeClient(@PathVariable("sid") final String sid) {
        clientManager.removeClient(new Sid(sid));
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/files/{fileName}/records/{recordId}/clients")
    public void removeFromQueue(@PathVariable("fileName") final String fileName,
                                @PathVariable("recordId") final String recordId,
                                @RequestParam("client_id") final String clientId,
                                @RequestParam("timestamp") final LocalDateTime localDateTime) {

        filesManager.removeFromQueue(fileName, recordId, clientId, localDateTime);
    }
}
