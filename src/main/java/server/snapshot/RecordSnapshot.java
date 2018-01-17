package server.snapshot;

import server.files.api.WaitingClient;

import java.util.ArrayList;
import java.util.List;

public class RecordSnapshot {
    private String lockedBy;
    private List<WaitingClient> waiting = new ArrayList<>();

    public RecordSnapshot() {
    }

    public RecordSnapshot(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public List<WaitingClient> getWaiting() {
        return waiting;
    }

    public void setWaiting(List<WaitingClient> waiting) {
        this.waiting = waiting;
    }
}
