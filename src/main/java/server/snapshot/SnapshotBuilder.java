package server.snapshot;

import server.files.ServerFile;
import server.files.api.*;
import io.vavr.collection.List;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SnapshotBuilder {
    private IFilesManager filesManager;

    SnapshotBuilder(final IFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    public Snapshot2 makeSnapshot(final String id) {
        final Snapshot2 snaphot2 = new Snapshot2();

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
                    recordDescriptor.setLockedBy(lockedBy.getId());
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
