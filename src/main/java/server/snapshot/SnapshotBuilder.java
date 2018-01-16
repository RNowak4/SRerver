package server.snapshot;

import io.vavr.collection.List;
import org.springframework.stereotype.Component;
import server.files.ServerFile;
import server.files.api.FileDescriptor;
import server.files.api.IFilesManager;
import server.files.api.RecordDescriptor;
import server.files.api.WaitingClient;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SnapshotBuilder {
    private IFilesManager filesManager;

    SnapshotBuilder(final IFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    public Snapshot makeSnapshot(final String id) {
        final Snapshot snaphot2 = new Snapshot();

        filesManager.getAllFiles().toStream()
                .forEach(file -> {
                    final Map<String, RecordSnapshot> snap = fileSnapshot(file);
                    snaphot2.getSnapshot().put(file.getName(), snap);
                });

        return snaphot2;
    }

    private Map<String, RecordSnapshot> fileSnapshot(final ServerFile serverFile) {
        final FileDescriptor fileDescriptor = new FileDescriptor(serverFile.getName());

        List<RecordDescriptor> recordDescriptors = serverFile.getRecords().toStream()
                .map(record -> {
                    final WaitingClient lockedBy = record.getLockedBy().get().getOrNull();
                    final RecordDescriptor recordDescriptor = new RecordDescriptor(record.getId());
                    if (lockedBy == null) {
                        recordDescriptor.setLockedBy(null);
                    } else {
                        recordDescriptor.setLockedBy(lockedBy.getId());
                    }
                    recordDescriptor.setWaiting(record.getLockingQueue().stream()
                            .map(WaitingClient::getId)
                            .collect(Collectors.toList())
                    );

                    return recordDescriptor;
                }).toList();

        fileDescriptor.setRecordDescriptors(recordDescriptors);

        final Map<String, RecordSnapshot> recordsMap = new HashMap<>();

        for (RecordDescriptor recordDescriptor : recordDescriptors) {
            RecordSnapshot rs = new RecordSnapshot(recordDescriptor.getLockedBy());
            rs.getWaiting().addAll(recordDescriptor.getWaiting());

            recordsMap.put(recordDescriptor.getId(), rs);
        }

        return recordsMap;
    }
}
