package server.snapshot;

import io.vavr.collection.List;
import server.files.api.FileDescriptor;

public class Snapshot {
    private String id;
    private List<FileDescriptor> files = List.empty();

    public Snapshot(String id) {
        this.id = id;
    }

    public Snapshot(final String id, final List<FileDescriptor> files) {
        this.id = id;
        this.files = files;
    }

    public List<FileDescriptor> getFiles() {
        return files;
    }

    public void addFileDescriptor(FileDescriptor fileDescriptor) {
        files = files.append(fileDescriptor);
    }
}
