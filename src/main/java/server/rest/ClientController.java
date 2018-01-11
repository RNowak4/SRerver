package server.rest;

import server.clients.api.IClientManager;
import server.clients.api.Sid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {
    private IClientManager clientManager;

    @RequestMapping(method = RequestMethod.POST, path = "/server/clients")
    public void registerClient(final Sid sid) {
        clientManager.registerClient(sid);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/server/clients/{sid}")
    public void removeClient(@PathVariable("sid") final String sid) {
        clientManager.removeClient(new Sid(sid));
    }
}
