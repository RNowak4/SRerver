package server.files.api;

public class RecordDto {
    private String id;
    private String filename;
    private String content;
    private String status;
    private String lockedBy;

    public RecordDto() {
    }

    public RecordDto(String id, String lockedBy, String filename, String content, String status) {
        this.id = id;
        this.lockedBy = lockedBy;
        this.filename = filename;
        this.content = content;
        this.status = status;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
