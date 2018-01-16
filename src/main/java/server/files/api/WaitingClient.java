package server.files.api;

import java.time.LocalDateTime;
import java.util.Objects;

public class WaitingClient {
    private String userId;
    private LocalDateTime timestamp = LocalDateTime.now();

    public WaitingClient(String id) {
        this.userId = id;
    }

    public WaitingClient(String id, LocalDateTime timestamp) {
        this.userId = id;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaitingClient that = (WaitingClient) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, timestamp);
    }

    @Override
    public String toString() {
        return "WaitingClient{" +
                "userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}