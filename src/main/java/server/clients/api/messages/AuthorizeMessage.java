package server.clients.api.messages;

public class AuthorizeMessage {
    private String userId;

    public AuthorizeMessage() {
    }

    public AuthorizeMessage(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
