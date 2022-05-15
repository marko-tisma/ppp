package com.mtisma.ppp.service;

import com.mtisma.ppp.model.Image;
import com.mtisma.ppp.repository.ImageFileRepository;
import com.mtisma.ppp.repository.ImageRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageFileRepository imageFileRepository;

    public ImageService(ImageRepository imageRepository, ImageFileRepository imageFileRepository) {
        this.imageRepository = imageRepository;
        this.imageFileRepository = imageFileRepository;
    }

    public Optional<FileSystemResource> downloadImage(long id) {
        Optional<Image> image = imageRepository.findById(id);
        if (image.isEmpty()) {
            return Optional.empty();
        }
        return imageFileRepository.getFile(image.get().getLocation());
    }
}
