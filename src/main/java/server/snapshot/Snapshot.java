package server.snapshot;

import java.util.HashMap;
import java.util.Map;

public class Snapshot {
    private Map<String, Map<String, RecordSnapshot>> snapshot = new HashMap<>();

    public Snapshot() {
    }

    public Snapshot(Map<String, Map<String, RecordSnapshot>> snapshot) {
        this.snapshot = snapshot;
    }

    public Map<String, Map<String, RecordSnapshot>> getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Map<String, Map<String, RecordSnapshot>> snapshot) {
        this.snapshot = snapshot;
    }
}
