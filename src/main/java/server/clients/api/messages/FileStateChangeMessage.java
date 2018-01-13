package server.clients.api.messages;

public class FileStateChangeMessage {
    private String eventType;
    private String userId;
    private String file;

    public FileStateChangeMessage() {
    }

    public FileStateChangeMessage(String eventType, String userId, String file) {
        this.eventType = eventType;
        this.userId = userId;
        this.file = file;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
