package server.deadlock;

import io.vavr.control.Option;
import server.files.api.WaitingClient;
import server.snapshot.RecordSnapshot;
import server.utils.HasLogger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeadlockDetector implements HasLogger {
    private WaitingGraph graph;
    private List<SnapshotDescription> snapshots = new ArrayList<>();
    private String snapUuid;

    public DeadlockDetector(final WaitingGraph graph, final List<SnapshotDescription> snapshots) {
        this.graph = graph;
        this.snapshots = snapshots;
    }

    DeadlockDetector(String snapUuid) {
        this.snapUuid = snapUuid;
    }

    void addSnapshot(final SnapshotDescription snapshot) {
        snapshots.add(snapshot);
    }

    Option<GraphEdge> getCycleAndRemove() {
        final List<GraphEdge> cyclePath = graph.findCycle();

        if (!cyclePath.isEmpty()) {
            getLogger().info("Found cycle.");
            final GraphEdge youngest = WaitingGraph.getYoungest(cyclePath);
            graph.removeEdge(youngest);
            return Option.of(youngest);
        }

        getLogger().info("Cycle not found.");
        return Option.none();
    }

    WaitingGraph buildGraph() {
        getLogger().info("Started building waiting graph");
        graph = new WaitingGraph();
        for (final SnapshotDescription snapshotDescription : snapshots) {
            final Map<String, Map<String, RecordSnapshot>> data = snapshotDescription.getSnapshot().getSnapshot();
            final List<String> files = new ArrayList<>(data.keySet());

            for (final String file : files) {
                final Map<String, RecordSnapshot> records = data.get(file);

                for (final String record : records.keySet()) {
                    final RecordSnapshot recordSnap = records.get(record);
                    final String lockedBy = recordSnap.getLockedBy();
                    if (lockedBy != null) {
                        for (WaitingClient waiting : recordSnap.getWaiting()) {
                            final GraphEdge e = new GraphEdge(waiting.getUserId(), LocalDateTime.parse(waiting.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME), lockedBy, file, record, snapshotDescription);
                            graph.addEdge(e);
                        }
                    }
                }
            }
        }

        getLogger().info("Successfulyl built waiting graph");
        return graph;
    }
}