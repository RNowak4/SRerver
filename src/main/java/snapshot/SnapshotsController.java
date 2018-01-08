package snapshot;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnapshotsController {
    private SnapshotBuilder snapshotBuilder;

    public SnapshotsController(final SnapshotBuilder snapshotBuilder) {
        this.snapshotBuilder = snapshotBuilder;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/snapshot/{id}")
    public Snapshot makeSnapshot(@PathVariable("id") final String id) {
        return snapshotBuilder.makeSnapshot(id);
    }
}
