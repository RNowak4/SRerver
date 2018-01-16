package server.snapshot;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.stereotype.Component;
import server.files.ServerFile;
import server.files.api.FileDescriptor;
import server.files.api.IFilesManager;
import server.files.api.RecordDescriptor;
import server.files.api.WaitingClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SnapshotBuilder {
    private IFilesManager filesManager;

    SnapshotBuilder(final IFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    public Snapshot makeSnapshot(final String snapshotId) {
        final Snapshot snaphot = new Snapshot();

        filesManager.getAllFiles().toStream()
                .forEach(file -> {
                    final Map<String, RecordSnapshot> snap = fileSnapshot(file);
                    snaphot.getSnapshot().put(file.getName(), snap);
                });

        return snaphot;
    }

    private Map<String, RecordSnapshot> fileSnapshot(final ServerFile serverFile) {
        final FileDescriptor fileDescriptor = new FileDescriptor(serverFile.getName());

        List<RecordDescriptor> recordDescriptors = serverFile.getRecords().toStream()
                .map(record -> {
                    final WaitingClient lockedBy = record.getLockedBy().get().getOrNull();
                    final RecordDescriptor recordDescriptor = new RecordDescriptor(record.getId());
                    recordDescriptor.setLockedBy(Option.of(lockedBy)
                            .map(WaitingClient::getUserId)
                            .getOrNull());
                    recordDescriptor.setWaiting(new ArrayList<>(record.getLockingQueue())
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
