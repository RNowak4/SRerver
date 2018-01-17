package server.files.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class WaitingClient {
    private String userId;
    private String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

    public WaitingClient() {
    }

    public WaitingClient(String id) {
        this.userId = id;
    }

    public WaitingClient(String id, LocalDateTime timestamp) {
        this.userId = id;
        this.timestamp = timestamp.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public String getUserId() {
        return userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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