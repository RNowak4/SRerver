package server.files;

import io.vavr.control.Option;
import server.files.api.WaitingClient;

import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Record {
    private static final int size = 1024;

    private char[] data = new char[size];
    private long position;
    private String id;
    private AtomicReference<Option<WaitingClient>> lockedBy = new AtomicReference<>(Option.none());
    private Queue<WaitingClient> lockingQueue = new LinkedBlockingQueue<>();

    public Record(String id, long position) {
        this.id = id;
        this.position = position;
    }

    public char[] getData() {
        return data;
    }

    public void setData(char[] data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AtomicReference<Option<WaitingClient>> getLockedBy() {
        return lockedBy;
    }

    public boolean lock(Option<WaitingClient> user) {
        if (lockedBy.get().isEmpty()) {
            lockedBy.set(user);
            return true;
        } else {
            user.forEach(username -> lockingQueue.add(username));
            return false;
        }
    }

    public void unlock(final String userId) {
        if (lockedBy.get().isDefined() && lockedBy.get().get().getId().equals(userId)) {
            if (lockingQueue.isEmpty()) {
                lockedBy.set(Option.none());
            } else {
                lockedBy.set(Option.of(lockingQueue.poll()));
            }
        }
    }

    public Queue<WaitingClient> getLockingQueue() {
        return lockingQueue;
    }

    public void setLockingQueue(Queue<WaitingClient> lockingQueue) {
        this.lockingQueue = lockingQueue;
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

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }
}
