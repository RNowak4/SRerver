package server.clients.api.messages;

import server.clients.api.Message;

public class RecordChangeMessage extends Message {
    private String eventType;
    private String file;
    private String record;
    private String userId;

    public RecordChangeMessage() {
    }

    public RecordChangeMessage(String eventType, String file, String record, String userId) {
        this.eventType = eventType;
        this.file = file;
        this.record = record;
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
