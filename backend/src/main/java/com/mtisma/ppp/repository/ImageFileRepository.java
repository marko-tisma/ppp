package com.mtisma.ppp.repository;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Repository
public class ImageFileRepository {

    private static final String BASE_DIR = Paths.get("./").toAbsolutePath() + "images/";

    public Optional<String> save(String fileName, byte[] data) {
        Path path = Path.of(BASE_DIR + fileName);
        if (Files.exists(path)) {
            return Optional.empty();
        }
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, data);
            return Optional.of(path.toString());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<FileSystemResource> getFile(String location) {
        Path path = Path.of(location);
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        return Optional.of(new FileSystemResource(path));
    }
}
