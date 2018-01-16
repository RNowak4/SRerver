package server.deadlock;

import server.snapshot.Snapshot;
import server.snapshot.Snapshot2;

public class SnapshotDescription {
    private Snapshot2 snapshot;
    private String host;
    private String port;

    public SnapshotDescription() {
    }

    public SnapshotDescription(Snapshot2 snapshot, String host, String port) {
        this.snapshot = snapshot;
        this.host = host;
        this.port = port;
    }

    public Snapshot2 getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Snapshot2 snapshot) {
        this.snapshot = snapshot;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
