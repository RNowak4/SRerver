package server.files;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import server.utils.HasLogger;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Arrays;

@Component
class SystemFileManager implements HasLogger {
    private static final char[] EMPTY_CHAR = new char[1024];

    static {
        for (int i = 0; i < 1024; i++) {
            EMPTY_CHAR[i] = '\0';
        }
    }

    @Value("${folder}")
    private String dirPath;

    private File dir = new File("/home/radek/repo");

    private Map<String, File> systemFilesMap = HashMap.empty();

    Map<String, File> getSystemFilesMap() {
        return systemFilesMap;
    }

    void init() {
        dir = new File(dirPath);
        if (!dir.exists()) {
            getLogger().debug("Repo directory doesn't exist. Creating...");
            if (dir.mkdirs()) {
                getLogger().info("Successfully created repo directory: {}", dir.getAbsolutePath());
            } else {
                getLogger().error("Error while creating repo directory: {}", dir.getAbsolutePath());
            }
        } else {
            getLogger().debug("Successfully detected existing repo directory: {}", dir.getAbsolutePath());
        }

        Option.of(dir.listFiles())
                .forEach(files ->
                        Arrays.stream(files)
                                .forEach(file -> systemFilesMap = systemFilesMap.put(file.getName(), file)));
    }

    List<Record> getRecordsForFile(final String filename) {
        return systemFilesMap.get(filename)
                .toTry()
                .mapTry(file -> new RandomAccessFile(file, "rwd"))
                .flatMap(this::readRecords)
                .getOrElse(List.empty());
    }

    private Try<List<Record>> readRecords(final RandomAccessFile raf) {
        return Try.of(() -> {
            List<Record> records = List.empty();
            for (long i = 0; i < raf.length() / 1024; ++i) {
                final Record record = new Record(String.valueOf(i), i);
                final char[] data = new char[1024];
                raf.seek(i * 1024);
                for (int k = 0; k < 1024; k++) {
                    data[k] = raf.readChar();
                }
                record.setData(data);

                records = records.append(record);
            }
            return records;
        })
                .onFailure(th -> getLogger().error("Error while reading records from file.", th))
                .andFinallyTry(raf::close);
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
                    file.setLength(file.length() + 1024);
                    file.seek(record.getPosition() * 1024);
                    file.writeChars(String.valueOf(EMPTY_CHAR));
                    file.seek(record.getPosition() * 1024);
                    file.writeChars(String.valueOf(record.getData()));
                    file.close();
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
                    file.close();
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
                    file.close();
                })
                .onFailure(th -> getLogger().error("Error while removing record: {} from file: {}", record.getId(), serverFile.getName(), th))
                .map(v -> null);
    }
}
