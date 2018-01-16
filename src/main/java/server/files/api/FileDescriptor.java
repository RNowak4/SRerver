package server.files.api;

import io.vavr.collection.List;

public class FileDescriptor {
    private String fileName;
    private List<RecordDescriptor> recordDescriptors;

    public FileDescriptor(String fileName) {
        this.fileName = fileName;
    }

    public FileDescriptor(String fileName, List<RecordDescriptor> recordDescriptors) {
        this.fileName = fileName;
        this.recordDescriptors = recordDescriptors;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setRecordDescriptors(List<RecordDescriptor> recordDescriptors) {
        this.recordDescriptors = recordDescriptors;
    }

    public String getFileName() {
        return fileName;
    }

    public List<RecordDescriptor> getRecordDescriptors() {
        return recordDescriptors;
    }

}
