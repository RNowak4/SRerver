package snapshot;

import files.api.FileDescriptor;
import files.api.IFilesManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class SnapshotBuilder {
    private IFilesManager filesManager;

    SnapshotBuilder(final IFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    Snapshot makeSnapshot(final String id) {
        final List<FileDescriptor> files = filesManager.getAllFiles().toJavaList();

        return new Snapshot(id, files);
    }
}
