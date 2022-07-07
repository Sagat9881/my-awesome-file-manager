package ru.apzakharov;

import lombok.Data;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class FileInfo {
    private String fileName;
    private FileType type;
    private long size;
    private LocalDateTime lastModified;


    @SneakyThrows
    public FileInfo(Path path) {
        this.fileName = path.getFileName().toString();
        boolean isDirectory = Files.isDirectory(path);
        this.type = isDirectory ? FileType.DIRECTORY : FileType.FILE;
        this.size = isDirectory ? -1L : Files.size(path);
        this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(3));

    }


    public enum FileType {
        FILE("F"), DIRECTORY("D");

        FileType(String d) {
            type = d;
        }

        private final String type;

        public String getType() {
            return type;
        }
    }
}
