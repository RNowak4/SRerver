package server.deadlock;

import java.time.LocalDateTime;

public class GraphEdge {
    private String lockingUser;
    private String waitingUser;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String filename;
    private String recordId;
    private String serverHost;
    private String serverPort;

    public GraphEdge(String userId, LocalDateTime timestamp, String lockedBy, String file, String record, SnapshotDescription snapshotDescription) {
        this.waitingUser = userId;
        this.lockingUser = lockedBy;
        this.filename = file;
        this.recordId = record;
        this.serverHost = snapshotDescription.getHost();
        this.serverPort = snapshotDescription.getPort();
        this.timestamp = timestamp;
    }

    String getLockingUser() {
        return lockingUser;
    }

    public String getWaitingUser() {
        return waitingUser;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getRecordId() {
        return recordId;
    }

    String getServerHost() {
        return serverHost;
    }

    String getServerPort() {
        return serverPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphEdge graphEdge = (GraphEdge) o;

        if (lockingUser != null ? !lockingUser.equals(graphEdge.lockingUser) : graphEdge.lockingUser != null)
            return false;
        if (waitingUser != null ? !waitingUser.equals(graphEdge.waitingUser) : graphEdge.waitingUser != null)
            return false;
        if (timestamp != null ? !timestamp.equals(graphEdge.timestamp) : graphEdge.timestamp != null) return false;
        if (filename != null ? !filename.equals(graphEdge.filename) : graphEdge.filename != null) return false;
        if (recordId != null ? !recordId.equals(graphEdge.recordId) : graphEdge.recordId != null) return false;
        if (serverHost != null ? !serverHost.equals(graphEdge.serverHost) : graphEdge.serverHost != null) return false;
        return serverPort != null ? serverPort.equals(graphEdge.serverPort) : graphEdge.serverPort == null;
    }

    @Override
    public int hashCode() {
        int result = lockingUser != null ? lockingUser.hashCode() : 0;
        result = 31 * result + (waitingUser != null ? waitingUser.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        result = 31 * result + (recordId != null ? recordId.hashCode() : 0);
        result = 31 * result + (serverHost != null ? serverHost.hashCode() : 0);
        result = 31 * result + (serverPort != null ? serverPort.hashCode() : 0);
        return result;
    }
}
