package server.snapshot;

import java.util.ArrayList;
import java.util.List;

public class RecordSnapshot {
    private String lockedBy;
    private List<String> waiting = new ArrayList<>();

    public RecordSnapshot(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public List<String> getWaiting() {
        return waiting;
    }

    public void setWaiting(List<String> waiting) {
        this.waiting = waiting;
    }
}
