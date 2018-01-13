package server.clients.api.messages;

public class FileCreateMessage {
    private String filename;

    public FileCreateMessage() {
    }

    public FileCreateMessage(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
