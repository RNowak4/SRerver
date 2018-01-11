package server.files;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import io.vavr.control.Try;
import server.files.api.WaitingClient;

import java.util.Objects;
import java.util.UUID;

public class ServerFile {
    private static final int maxRecords = 128;

    private String name;
    private List<Record> records = List.empty();
    private Set<String> openedByUserIds = HashSet.empty();

    public ServerFile(String name) {
        this.name = name;
    }

    public Try<Void> addRecord(final Record record) {
        if (records.size() + 1 >= maxRecords) {
            return Try.failure(new RuntimeException("Exceeded records max size."));
        }

        return Try.run(() -> records.append(record));
    }

    public String getName() {
        return name;
    }

    public List<Record> getRecords() {
        return records;
    }

    public Option<Record> getRecord(final String recordId) {
        return records.toStream()
                .filter(record -> Objects.equals(record.getId(), recordId))
                .headOption();
    }

    public Try<Void> deleteRecord(final String recordId, final String userId) {
        return getRecord(recordId)
                .filter(record -> isUserAllowedToEditRecord(record, userId))
                .peek(record -> records = records.remove(record))
                .toTry()
                .map(v -> null);
    }

    public Try<Record> createRecord(char[] content) {
        // TODO builder
        final Record record = new Record(UUID.randomUUID().toString());
        return Try.of(() -> {
            record.setData(content);
            return record;
        });
    }

    private boolean isUserAllowedToEditRecord(final Record record, final String userId) {
        return record.getLockedBy().get()
                .map(WaitingClient::getId)
                .map(id -> Objects.equals(id, userId))
                .getOrElse(false);
    }

    public Try<Void> editRecord(final char[] content, final String userId, final String recordId) {
        return getRecord(recordId)
                .filter(record -> isUserAllowedToEditRecord(record, userId))
                // TODO set data should return Try<Void>
                .peek(record -> record.setData(content))
                .toTry()
                .map(v -> null);
    }

    public void lockRecord(final String userId, final String recordId) {
        getRecord(recordId)
                .forEach(record -> record.lock(Option.of(new WaitingClient(userId))));
    }

    public void unlockRecord(final String userId, final String recordId) {
        getRecord(recordId)
                .forEach(record -> record.unlock(userId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerFile serverFile = (ServerFile) o;
        return Objects.equals(name, serverFile.name) &&
                Objects.equals(records, serverFile.records) &&
                Objects.equals(openedByUserIds, serverFile.openedByUserIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, records, openedByUserIds);
    }

    @Override
    public String toString() {
        return "ServerFile{" +
                "name='" + name + '\'' +
                ", records=" + records +
                ", openedByUserIds=" + openedByUserIds +
                '}';
    }
}
