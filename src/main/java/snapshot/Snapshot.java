package snapshot;

import files.api.FileDescriptor;

import java.util.List;

public class Snapshot {
    private String id;
    private List<FileDescriptor> files;

    public Snapshot(final String id, final List<FileDescriptor> files) {
        this.id = id;
        this.files = files;
    }

    public List<FileDescriptor> getFiles() {
        return files;
    }
}
