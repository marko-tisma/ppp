package com.mtisma.ppp.repository;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class ImageFileRepositoryTests {

    private final ImageFileRepository fileRepository = new ImageFileRepository();

    @Test
    public void testSaveFile() throws IOException {
        byte[] data = {0x12, 0x34};
        String name = "test_image";
        Optional<String> location = fileRepository.save(name, data);
        assertTrue(location.isPresent());
        Path path = Path.of(location.get());
        assertTrue(Files.exists(path));
        assertArrayEquals(data, Files.readAllBytes(path));
        assertFalse(fileRepository.save(name, data).isPresent());
        Files.delete(path);
    }

    @Test
    public void testGetFile() throws IOException {
        byte[] data = {0x12, 0x34};
        String name = "test_image";
        Optional<String> location = fileRepository.save(name, data);
        assertTrue(location.isPresent());
        Optional<FileSystemResource> file = fileRepository.getFile(name);
        assertTrue(file.isPresent());
        FileSystemResource res = file.get();
        assertArrayEquals(data, res.getInputStream().readAllBytes());
        Files.delete(Path.of(location.get()));
    }
}
