package server.snapshot;

import server.files.ServerFile;
import server.files.api.*;
import io.vavr.collection.List;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SnapshotBuilder {
    private IFilesManager filesManager;

    SnapshotBuilder(final IFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    public Snapshot makeSnapshot(final String id) {
        final Snapshot snapshot = new Snapshot(id);

        filesManager.getAllFiles().toStream()
                .map(this::fileSnapshot)
                .forEach(snapshot::addFileDescriptor);

        return snapshot;
    }

    private FileDescriptor fileSnapshot(final ServerFile serverFile) {
        final FileDescriptor fileDescriptor = new FileDescriptor(serverFile.getName());

        List<RecordDescriptor> recordDescriptors = serverFile.getRecords().toStream()
                .map(record -> {
                    final WaitingClient lockedBy = record.getLockedBy().get().getOrNull();
                    final RecordDescriptor recordDescriptor = new RecordDescriptor(record.getId());
                    recordDescriptor.setLockedBy(lockedBy.getId());
                    recordDescriptor.setWaiting(record.getLockingQueue().stream()
                            .map(WaitingClient::getId)
                            .collect(Collectors.toList())
                    );

                    return recordDescriptor;
                }).toList();

        fileDescriptor.setRecordDescriptors(recordDescriptors);

        return fileDescriptor;
    }
}
