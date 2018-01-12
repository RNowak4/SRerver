package server.files;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;

@Component
class SystemFileManager {
    private static final File dir = new File("/home/radek/repo");
    private Map<String, File> systemFilesMap = HashMap.empty();

    public Try<Void> newFile(final String fileName) {
        return Try.of(() -> new File(dir, fileName))
                .andThenTry(file -> Files.createFile(file.toPath()))
                .onSuccess(file -> systemFilesMap = systemFilesMap.put(fileName, file))
                .map(v -> null);
    }

    public Try<Void> deleteFile(final String fileName) {
        return systemFilesMap.get(fileName)
                .toTry()
                .andThenTry(file -> Files.delete(file.toPath()))
                .onSuccess(v -> systemFilesMap = systemFilesMap.remove(fileName))
                .map(v -> null);
    }

    Try<Void> addRecord(final ServerFile serverFile, final Record record) {
        return systemFilesMap.get(serverFile.getName())
                .toTry()
                .mapTry(file -> new RandomAccessFile(file, "rwd"))
                .andThenTry(file -> {
                    file.setLength(file.length() + 1024);
                    file.seek(Integer.valueOf(record.getId()) * 1024);
                    file.writeChars(String.valueOf(record.getData()));
                })
                .map(v -> null);
    }

    Try<Void> modifyRecord(final ServerFile serverFile, final Record record, final char[] content) {
        return systemFilesMap.get(serverFile.getName())
                .toTry()
                .mapTry(file -> new RandomAccessFile(file, "rwd"))
                .andThenTry(file -> {
                    file.seek(Integer.valueOf(record.getId()) * 1024);
                    file.writeChars(String.valueOf(record.getData()));
                })
                .map(v -> null);
    }
}
