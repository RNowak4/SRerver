package server.files.api;

import java.time.LocalDateTime;
import java.util.Objects;

public class WaitingClient {
    private String id;
    private LocalDateTime timestamp = LocalDateTime.now();

    public WaitingClient(String id) {
        this.id = id;
    }

    public WaitingClient(String id, LocalDateTime timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaitingClient that = (WaitingClient) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp);
    }

    @Override
    public String toString() {
        return "WaitingClient{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}