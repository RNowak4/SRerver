package server.deadlock;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import server.files.api.IFilesManager;
import server.snapshot.Snapshot;
import server.snapshot.SnapshotBuilder;
import server.utils.HasLogger;

import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class DeadlockController implements HasLogger {
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

    @Scheduled(fixedRate = 10000)
    public void checkDeadlocks() {
        final String snapUuid = UUID.randomUUID().toString();
        getLogger().info("Starting checking for dead locks with snapshot id: {}", snapUuid);
        final DeadlockDetector detector = new DeadlockDetector(snapUuid);
        final Snapshot ownSnapShot = snapshotBuilder.makeSnapshot(snapUuid);
        detector.addSnapshot(new SnapshotDescription(ownSnapShot, null, null));
        serverHolder.getServers().stream()
                .map(server -> CompletableFuture.runAsync(() -> getSnapshotForServer(server, detector, snapUuid))
                        .thenAccept(v -> getLogger().info("Successfully get snapshot from server: {}:{}", server.getHost(), server.getPort())))
                .forEach(CompletableFuture::join);

        detector.buildGraph();

        Option<GraphEdge> removed = detector.getCycleAndRemove();
        while (removed.isDefined()) {
            if (removed.get().getServerHost() != null) {
                removeWaitingClient(removed.get());
            } else {
                filesManager.removeFromQueue(removed.get());
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
                removed.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));

        getLogger().info("Removing waiting client: {} on file: {} record: {}",
                removed.getWaitingUser(), removed.getFilename(), removed.getRecordId());
        restTemplate.delete(url);
    }

    private void getSnapshotForServer(final Server server, final DeadlockDetector detector, final String id) {
        getLogger().info("Getting snapshot for server: {}:{}", server.getHost(), server.getPort());

        final String address = String.format("%s:%s", server.getHost(), server.getPort());
        final String url = String.format("http://%s/snapshots/%s", address, id);
        Try.of(() -> restTemplate.getForObject(url, Snapshot.class))
                .onFailure(th -> getLogger().warn("Error while getting snapshot from server: {}:{}", server.getHost(), server.getPort(), th))
                .forEach(snapshot -> detector.addSnapshot(new SnapshotDescription(snapshot, server.getHost(), server.getPort())));
    }

}
