package server.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import server.files.api.FileDescriptor;
import server.snapshot.SnapshotBuilder;

import java.util.List;

@RestController
public class SnapshotsController {
    private SnapshotBuilder snapshotBuilder;

    public SnapshotsController(final SnapshotBuilder snapshotBuilder) {
        this.snapshotBuilder = snapshotBuilder;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/server/snapshot/{id}")
    public List<FileDescriptor> makeSnapshot(@PathVariable("id") final String id) {
        return snapshotBuilder.makeSnapshot(id).getFiles().toJavaList();
    }
}