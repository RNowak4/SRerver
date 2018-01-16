package server.rest;

import org.springframework.web.bind.annotation.*;
import server.snapshot.RecordSnapshot;
import server.snapshot.SnapshotBuilder;

import java.util.Map;

@RestController
public class SnapshotsController {
    private SnapshotBuilder snapshotBuilder;

    public SnapshotsController(final SnapshotBuilder snapshotBuilder) {
        this.snapshotBuilder = snapshotBuilder;
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/snapshots/{id}")
    public Map<String, Map<String, RecordSnapshot>> makeSnapshot(@PathVariable("id") final String id) {
        return snapshotBuilder.makeSnapshot(id).getSnapshot();
    }
}
