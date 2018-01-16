package server.deadlock;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import server.files.api.IFilesManager;
import server.snapshot.Snapshot2;
import server.snapshot.SnapshotBuilder;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class DeadlockController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ServerHolder serverHolder;
    private final IFilesManager filesManager;
    private final SnapshotBuilder snapshotBuilder;

    public DeadlockController(final ServerHolder serverHolder,
                              final IFilesManager filesManager,
                              final SnapshotBuilder snapshotBuilder) {
        this.serverHolder = serverHolder;
        this.filesManager = filesManager;
        this.snapshotBuilder = snapshotBuilder;
    }

//    @Scheduled
    public void checkDeadlocks() {
        final String snapUuid = UUID.randomUUID().toString();
        final DeadlockDetector detector = new DeadlockDetector(snapUuid);
        final Snapshot2 ownSnapShot = snapshotBuilder.makeSnapshot(snapUuid);
        detector.addSnapshot(new SnapshotDescription(ownSnapShot, null, null));
        serverHolder.getServers().stream()
                .map(server -> CompletableFuture.runAsync(() -> getSnapshotForServer(server, detector, snapUuid)))
                .forEach(CompletableFuture::join);

        detector.buildGraph();
        GraphEdge removed = detector.getCycleAndRemove();
        while (removed != null) {
            if (removed.getServerHost() != null) {
                removeWaitingClient(removed);
            } else {
                filesManager.removeFromQueue(removed);
            }
            removed = detector.getCycleAndRemove();
        }
    }

    private void removeWaitingClient(final GraphEdge removed) {
        final String url = String.format("http://%s:%s/files/%s/records/%s/clients?client_id=%s&timestamp=%s",
                removed.getServerHost(),
                removed.getServerPort(),
                removed.getFilename(),
                removed.getRecordId(),
                removed.getWaitingUser(),
                removed.getTimestamp());

        restTemplate.delete(url);
    }

    public void getSnapshotForServer(Server server, DeadlockDetector detector, String id) {
        final String address = String.format("%s:%s", server.getHost(), server.getPort());
        final String url = String.format("http://%s/snapshots/%s", address, id);
        Snapshot2 snap = restTemplate.getForObject(url, Snapshot2.class);
        detector.addSnapshot(new SnapshotDescription(snap, server.getHost(), server.getPort()));
    }

}
