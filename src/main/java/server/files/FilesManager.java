package server.files;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.springframework.stereotype.Component;
import server.files.api.IFilesManager;
import server.utils.HasLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
class FilesManager implements HasLogger, IFilesManager {
    private final File dir = new File("/home/radek/repo");
    private Map<String, Tuple2<ServerFile, File>> filesMap = HashMap.empty();

    @Override
    public List<ServerFile> getAllFiles() {
        return filesMap.values().map(value -> value._1).collect(List.collector());
    }

    @Override
    public Try<Void> createNew(final String fileName) {
        return Try.of(() -> new File(dir, fileName))
                .andThenTry(File::createNewFile)
                .map(res -> Tuple.of(new ServerFile(fileName), res))
                .onSuccess(fileTuple -> filesMap = filesMap.put(fileName, fileTuple))
                .onSuccess(v -> getLogger().info("Successfully created file: {}.", fileName))
                .onFailure(th -> getLogger().error("Error while tryng to create file: {}.", fileName))
                .map(v -> null);
    }

    @Override
    public void delete(String fileId) {
        // TODO refactor
        Try.run(() -> filesMap.get(fileId)
                .forEach(idFileMap -> {
                    try {
                        Files.delete(idFileMap._2.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }))
                .onSuccess(v -> filesMap = filesMap.remove(fileId));
    }
}