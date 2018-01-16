package server.snapshot;

import java.util.HashMap;
import java.util.Map;

public class Snapshot2 {
    private Map<String, Map<String, RecordSnapshot>> snapshot = new HashMap<>();

    public Snapshot2() {
    }

    public Map<String, Map<String, RecordSnapshot>> getSnapshot() {
        return snapshot;
    }


}
