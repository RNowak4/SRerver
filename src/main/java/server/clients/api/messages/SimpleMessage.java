package server.clients.api.messages;

import server.clients.api.Message;

public class SimpleMessage extends Message {
    private String message;

    public SimpleMessage() {
    }

    public SimpleMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
