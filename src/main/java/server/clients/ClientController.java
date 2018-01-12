package server.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import server.clients.api.Client;
import server.clients.api.Message;

@Controller
public class ClientController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/form")
    @SendTo("/topic/greetings")
    public String validate() {
        return "Hello world !";
    }

    public void sendMessage(final Client client, final Message message) {
        this.template.convertAndSend(client.getLogin(), message);
    }
}
