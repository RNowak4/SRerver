package server.files;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import io.vavr.control.Try;
import server.files.api.WaitingClient;
import server.utils.HasLogger;

import java.util.Objects;

public class ServerFile implements HasLogger {
    private static final int maxRecords = 128;

    private long recordIdCounter = 0;
    private String name;
    private List<Record> records = List.empty();
    private Set<String> openedByUserIds = HashSet.empty();

    ServerFile(String name) {
        this.name = name;
    }

    Set<String> getOpenedByUserIds() {
        return openedByUserIds;
    }

    public String getName() {
        return name;
    }

    public List<Record> getRecords() {
        return records;
    }

    void setRecords(List<Record> records) {
        this.records = records;
        this.recordIdCounter = records.size();
    }

    Option<Record> getRecord(final String recordId) {
        return records.toStream()
                .filter(record -> Objects.equals(record.getId(), recordId))
                .headOption();
    }

    Try<Record> deleteRecord(final String recordId, final String userId) {
        return getRecord(recordId)
                .filter(record -> isUserAllowedToEditRecord(record, userId))
                .peek(record -> records = records.remove(record))
                .toTry();
    }

    Try<Record> createRecord(char[] content) {
        final Record record = new Record(String.valueOf(recordIdCounter++), firstFreePos());
        records = records.append(record);
        return Try.of(() -> {
            record.setData(content);
            return record;
        });
    }

    private long firstFreePos() {
        final List<Long> sortedPositions = records.toStream()
                .map(Record::getPosition)
                .sorted()
                .collect(List.collector());

        int pos = 0;
        for (Long sortedPosition : sortedPositions) {
            if (sortedPosition == pos) {
                pos++;
            } else {
                return pos;
            }
        }

        return pos;
    }

    private boolean isUserAllowedToEditRecord(final Record record, final String userId) {
        return record.getLockedBy().get()
                .map(WaitingClient::getId)
                .map(id -> Objects.equals(id, userId))
                .getOrElse(false);
    }

    boolean lockRecord(final String userId, final String recordId) {
        getLogger().info("Locking recordId: {} by user: {}", recordId, userId);
        return getRecord(recordId)
                .map(record -> record.lock(Option.of(new WaitingClient(userId))))
                .get();
    }

    void unlockRecord(final String userId, final String recordId) {
        getRecord(recordId)
                .forEach(record -> record.unlock(userId));
        getLogger().info("Unlocked recordId: {} by user: {}", recordId, userId);
    }

    Try<Record> modifyRecord(String recordId, String userId, String content) {
        return getRecord(recordId)
                .filter(record -> isUserAllowedToEditRecord(record, userId))
                .peek(record -> record.setData(content.toCharArray()))
                .toTry();
    }

    void addOpenedBy(String userId) {
        openedByUserIds = openedByUserIds.add(userId);
    }

    void removeOpenedBy(String userId) {
        openedByUserIds = openedByUserIds.remove(userId);
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
