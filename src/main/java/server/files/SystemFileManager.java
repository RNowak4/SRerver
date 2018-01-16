package server.files;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import server.utils.HasLogger;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;

@Component
class SystemFileManager implements HasLogger {
    private static final char[] EMPTY_CHAR = new char[1024];

    @Value("${folder}")
    private String dirPath;

    private File dir = new File("/home/radek/repo");
    private Map<String, File> systemFilesMap = HashMap.empty();

    @PostConstruct
    void init() {
        dir = new File(dirPath);
    }

    Try<Void> newFile(final String fileName) {
        return Try.of(() -> new File(dir, fileName))
                .andThenTry(file -> Files.createFile(file.toPath()))
                .onSuccess(file -> systemFilesMap = systemFilesMap.put(fileName, file))
                .onSuccess(v -> getLogger().info("Successfully created system file: {}", fileName))
                .onFailure(th -> getLogger().error("Error while trying to create system file: {}", fileName, th))
                .map(v -> null);
    }

    Try<Void> deleteFile(final String fileName) {
        return systemFilesMap.get(fileName)
                .toTry()
                .andThenTry(file -> Files.delete(file.toPath()))
                .onSuccess(v -> systemFilesMap = systemFilesMap.remove(fileName))
                .onSuccess(v -> getLogger().info("Successfully deleted system file: {}", fileName))
                .onFailure(th -> getLogger().error("Error while trying to delete system file: {}", fileName, th))
                .map(v -> null);
    }

    Try<Void> addRecord(final ServerFile serverFile, final Record record) {
        return systemFilesMap.get(serverFile.getName())
                .toTry()
                .mapTry(file -> new RandomAccessFile(file, "rwd"))
                .andThenTry(file -> {
                    file.seek(record.getPosition() * 1024);
                    file.writeChars(String.valueOf(EMPTY_CHAR));
                    file.seek(record.getPosition() * 1024);
                    file.writeChars(String.valueOf(record.getData()));
                })
                .onFailure(th -> getLogger().error("Error while adding new record to file: {}", serverFile.getName(), th))
                .map(v -> null);
    }

    Try<Void> modifyRecord(final ServerFile serverFile, final Record record, final char[] content) {
        return systemFilesMap.get(serverFile.getName())
                .toTry()
                .mapTry(file -> new RandomAccessFile(file, "rwd"))
                .andThenTry(file -> {
                    file.seek(record.getPosition() * 1024);
                    file.writeChars(String.valueOf(EMPTY_CHAR));
                    file.seek(record.getPosition() * 1024);
                    file.writeChars(String.valueOf(content));
                })
                .onFailure(th -> getLogger().error("Error while modifying record: {} in file: {}", record.getId(), serverFile.getName(), th))
                .map(v -> null);
    }

    Try<Void> removeRecord(final ServerFile serverFile, final Record record) {
        return systemFilesMap.get(serverFile.getName())
                .toTry()
                .mapTry(file -> new RandomAccessFile(file, "rwd"))
                .andThenTry(file -> {
                    file.seek(record.getPosition() * 1024);
                    file.writeChars(String.valueOf(EMPTY_CHAR));
                })
                .onFailure(th -> getLogger().error("Error while removing record: {} from file: {}", record.getId(), serverFile.getName(), th))
                .map(v -> null);
    }
}
