package server.files.api;

public class RecordDto {
    private String id;
    private String fileName;
    private char[] content;
    private String status;

    public RecordDto(String id, String fileName, char[] content, String status) {
        this.id = id;
        this.fileName = fileName;
        this.content = content;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public char[] getContent() {
        return content;
    }

    public void setContent(char[] content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
