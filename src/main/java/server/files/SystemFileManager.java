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
    private static final char[] EMPTY_CHAR = new char[1024];
    private static final File dir = new File("/home/radek/repo");
    private Map<String, File> systemFilesMap = HashMap.empty();


    Try<Void> newFile(final String fileName) {
        return Try.of(() -> new File(dir, fileName))
                .andThenTry(file -> Files.createFile(file.toPath()))
                .onSuccess(file -> systemFilesMap = systemFilesMap.put(fileName, file))
                .map(v -> null);
    }

    Try<Void> deleteFile(final String fileName) {
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
                    file.seek(record.getPosition() * 1024);
                    file.writeChars(String.valueOf(EMPTY_CHAR));
                    file.seek(record.getPosition() * 1024);
                    file.writeChars(String.valueOf(record.getData()));
                })
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
                .map(v -> null);
    }
}
