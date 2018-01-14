package server.deadlock;

import java.time.LocalDateTime;

public class GraphEdge {
    private String lockingUser;
    private String waitingUser;
    private LocalDateTime timestamp;
    private String filename;
    private String recordId;
    private String serverHost;
    private String serverPort;

    public GraphEdge() {
    }

    public GraphEdge(String lockingUser, String waitingUser, LocalDateTime timestamp, String filename, String recordId, String serverHost, String serverPort) {
        this.lockingUser = lockingUser;
        this.waitingUser = waitingUser;
        this.timestamp = timestamp;
        this.filename = filename;
        this.recordId = recordId;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public String getLockingUser() {
        return lockingUser;
    }

    public void setLockingUser(String lockingUser) {
        this.lockingUser = lockingUser;
    }

    public String getWaitingUser() {
        return waitingUser;
    }

    public void setWaitingUser(String waitingUser) {
        this.waitingUser = waitingUser;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }
}
