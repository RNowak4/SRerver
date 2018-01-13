package server.clients.api.messages;

import server.clients.api.Message;

public class RecordUpdateMessage extends Message {
    private String eventType;
    private Record record;

    public RecordUpdateMessage() {
    }

    public RecordUpdateMessage(String eventType, Record record) {
        this.eventType = eventType;
        this.record = record;
    }

    public RecordUpdateMessage(String eventType, String recordId, String content, String filename) {
        this.eventType = eventType;
        record = new Record(recordId, content, filename);
    }


    public static class Record {
        private String recordId;
        private String content;
        private String filename;

        public Record() {
        }

        private Record(String recordId, String content, String filename) {
            this.recordId = recordId;
            this.content = content;
            this.filename = filename;
        }

        public String getRecordId() {
            return recordId;
        }

        public void setRecordId(String recordId) {
            this.recordId = recordId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }
}
