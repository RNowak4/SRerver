package server.clients.api.messages;

import server.clients.api.Message;

public class RecordCreatedMessage extends Message {
    private String message;
    private String recordId;

    public RecordCreatedMessage() {
    }

    public RecordCreatedMessage(String message, String recordId) {
        this.message = message;
        this.recordId = recordId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
}
