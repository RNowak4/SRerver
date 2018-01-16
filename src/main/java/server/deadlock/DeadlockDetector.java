package server.deadlock;

import server.snapshot.RecordSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeadlockDetector {
    private WaitingGraph graph;
    private List<SnapshotDescription> snapshots = new ArrayList<>();

    public DeadlockDetector(final WaitingGraph graph, final List<SnapshotDescription> snapshots) {
        this.graph = graph;
        this.snapshots = snapshots;
    }

    public void addSnapshot(final SnapshotDescription snapshot) {
        snapshots.add(snapshot);
    }

    public GraphEdge getCycleAndRemove() {
        final List<GraphEdge> cyclePath = graph.findCycle();

        if (!cyclePath.isEmpty()) {
            final GraphEdge younges = WaitingGraph.getYoungest(cyclePath);
            return younges;
        }

        return null;
    }

    public WaitingGraph buildGraph() {
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
                        for (String waiting : recordSnap.getWaiting()) {
                            final GraphEdge e = new GraphEdge(waiting, lockedBy, file, record, snapshotDescription);
                            graph.addEdge(e);
                        }
                    }
                }
            }
        }

        return graph;
    }
}