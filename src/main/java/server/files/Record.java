package server.files;

import com.corundumstudio.socketio.SocketIOServer;
import io.vavr.control.Option;
import server.files.api.WaitingClient;
import server.utils.HasLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Record implements HasLogger {
    private static final int size = 1024;
    private char[] data = new char[size];
    private long position;
    private String id;
    private AtomicReference<Option<WaitingClient>> lockedBy = new AtomicReference<>(Option.none());
    private Queue<WaitingClient> lockingQueue = new LinkedBlockingQueue<>();

    Record(String id, long position) {
        this.id = id;
        this.position = position;
    }

    public char[] getData() {
        return data;
    }

    void setData(char[] data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public AtomicReference<Option<WaitingClient>> getLockedBy() {
        return lockedBy;
    }

    boolean lock(Option<WaitingClient> user) {
        if (lockedBy.get().isEmpty()) {
            lockedBy.set(user);
            return true;
        } else {
            user.forEach(username -> lockingQueue.add(username));
            return false;
        }
    }

    void unlock(final String userId) {
        if (lockedBy.get().isDefined() && lockedBy.get().get().getUserId().equals(userId)) {
            if (lockingQueue.isEmpty()) {
                lockedBy.set(Option.none());
            } else {
                getLogger().info("Unlocking record: {} from user: {}", id, userId);
                lockedBy.set(Option.of(lockingQueue.poll()));
            }
        }
    }

    public Queue<WaitingClient> getLockingQueue() {
        return lockingQueue;
    }

    void removeFromQueue(final String userName) {
        java.util.List<WaitingClient> toRemove = new ArrayList<>();
        lockingQueue.forEach(waitingClient -> {
            if (waitingClient.getUserId().equals(userName)) {
                toRemove.add(waitingClient);
            }
            lockingQueue.removeAll(toRemove);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Arrays.equals(data, record.data) &&
                Objects.equals(id, record.id) &&
                Objects.equals(lockedBy, record.lockedBy) &&
                Objects.equals(lockingQueue, record.lockingQueue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, id, lockedBy, lockingQueue);
    }

    @Override
    public String toString() {
        return "Record{" +
                "data=" + Arrays.toString(data) +
                ", id='" + id + '\'' +
                ", lockedBy=" + lockedBy +
                ", lockingQueue=" + lockingQueue +
                '}';
    }

    long getPosition() {
        return position;
    }

    void removeFromQueue(final WaitingClient waitingClient) {
        if (!lockingQueue.contains(waitingClient)) {
            getLogger().warn("Cannot remove waiting user: {} because is not in queue.", waitingClient.getUserId());
        }
        lockingQueue.remove(waitingClient);
    }
}
