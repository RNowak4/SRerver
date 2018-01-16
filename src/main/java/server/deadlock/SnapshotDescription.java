package server.deadlock;

import server.snapshot.Snapshot;

public class SnapshotDescription {
    private Snapshot snapshot;
    private String host;
    private String port;

    public SnapshotDescription() {
    }

    SnapshotDescription(final Snapshot snapshot, final String host, final String port) {
        this.snapshot = snapshot;
        this.host = host;
        this.port = port;
    }

    Snapshot getSnapshot() {
        return snapshot;
    }

    String getHost() {
        return host;
    }

    String getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SnapshotDescription that = (SnapshotDescription) o;

        if (snapshot != null ? !snapshot.equals(that.snapshot) : that.snapshot != null) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        return port != null ? port.equals(that.port) : that.port == null;
    }

    @Override
    public int hashCode() {
        int result = snapshot != null ? snapshot.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        return result;
    }
}
