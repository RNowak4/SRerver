package server.files.api;

import io.vavr.collection.List;
import io.vavr.control.Try;
import server.files.ServerFile;

public interface IFilesManager {

    List<ServerFile> getAllFiles();

    Try<Void> createNew(String fileName);

    void delete(String fileId);
}
