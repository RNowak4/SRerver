package files.api;

import java.util.List;
import java.util.Objects;

public class FileDescriptor {
    private String fileId;
    private String fileName;
    private List<RecordDescriptor> recordDescriptors;

    public FileDescriptor(String fileId, String fileName, List<RecordDescriptor> recordDescriptors) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.recordDescriptors = recordDescriptors;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public List<RecordDescriptor> getRecordDescriptors() {
        return recordDescriptors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDescriptor that = (FileDescriptor) o;
        return Objects.equals(fileId, that.fileId) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(recordDescriptors, that.recordDescriptors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, fileName, recordDescriptors);
    }

    @Override
    public String toString() {
        return "FileDescriptor{" +
                "fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", recordDescriptors=" + recordDescriptors +
                '}';
    }
}
