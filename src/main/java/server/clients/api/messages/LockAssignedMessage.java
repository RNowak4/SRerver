package server.clients.api.messages;

import server.clients.api.Message;

public class LockAssignedMessage extends Message {
    private String eventType;
    private String recordId;
    private String filename;

    public LockAssignedMessage() {
    }

    public LockAssignedMessage(String eventType, String recordId, String filename) {
        this.eventType = eventType;
        this.recordId = recordId;
        this.filename = filename;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
