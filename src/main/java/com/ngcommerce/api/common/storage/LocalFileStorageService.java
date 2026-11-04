package com.ngcommerce.api.common.storage;

import com.ngcommerce.api.common.exception.FileStorageException;
import com.ngcommerce.api.common.exception.InvalidFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private static final Map<String, String> ALLOWED_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final StorageProperties properties;

    private Path getRootLocation() {
        return Paths.get(properties.location())
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public String store(MultipartFile file, String directory) {

        validateFile(file);

        if (!StringUtils.hasText(directory)
                || !directory.matches("^[a-zA-Z0-9_-]+$")) {

            throw new InvalidFileException(
                    "Invalid storage directory"
            );
        }

        String contentType = file.getContentType();
        String extension = ALLOWED_TYPES.get(contentType);

        String filename = UUID.randomUUID() + extension;

        Path root = getRootLocation();

        Path targetDirectory = root
                .resolve(directory)
                .normalize();

        if (!targetDirectory.startsWith(root)) {
            throw new InvalidFileException(
                    "Invalid storage path"
            );
        }

        try {
            Files.createDirectories(targetDirectory);

            Path targetFile = targetDirectory.resolve(filename);

            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetFile);
            }

            return directory + "/" + filename;

        } catch (IOException ex) {

            throw new FileStorageException(
                    "Could not store file",
                    ex
            );
        }
    }

    @Override
    public void delete(String filePath) {

        if (!StringUtils.hasText(filePath)) {
            return;
        }

        Path root = getRootLocation();

        Path target = root
                .resolve(filePath)
                .normalize();

        if (!target.startsWith(root) || target.equals(root)) {
            throw new InvalidFileException(
                    "Invalid file path"
            );
        }

        try {
            Files.deleteIfExists(target);

        } catch (IOException ex) {

            throw new FileStorageException(
                    "Could not delete file",
                    ex
            );
        }
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileException(
                    "File is required"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                    "File size must not exceed 10 MB"
            );
        }

        if (!ALLOWED_TYPES.containsKey(file.getContentType())) {
            throw new InvalidFileException(
                    "Only JPEG, PNG and WEBP images are allowed"
            );
        }
    }
}
