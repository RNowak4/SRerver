package server.snapshot;

import java.util.HashMap;
import java.util.Map;

public class Snapshot {
    private Map<String, Map<String, RecordSnapshot>> snapshot = new HashMap<>();

    Snapshot() {
    }

    public Map<String, Map<String, RecordSnapshot>> getSnapshot() {
        return snapshot;
    }


}
