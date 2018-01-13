package server.clients.api.messages;

public class ContentMessage {
    private String content;

    public ContentMessage() {
    }

    public ContentMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
