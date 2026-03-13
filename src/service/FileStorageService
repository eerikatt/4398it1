package com.example.service;

import com.example.config.RegistryConfig;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileStorageService {
    private final RegistryConfig config;

    public FileStorageService() {
        this(RegistryConfig.defaultConfig());
    }

    public FileStorageService(RegistryConfig config) {
        this.config = config;
    }

    public Path getStorageFolder() {
        return config.getStorageFolder();
    }

    public Path getInvalidFolder() {
        return config.getInvalidFolder();
    }

    public void ensureFoldersExist() throws IOException {
        if (!Files.exists(getStorageFolder())) {
            Files.createDirectories(getStorageFolder());
        }

        if (!Files.exists(getInvalidFolder())) {
            Files.createDirectories(getInvalidFolder());
        }
    }

    // Move an unsupported file into the invalid documents directory
    public String moveToInvalidFolder(Path sourcePath) throws IOException {
        ensureFoldersExist();

        String originalFileName = sourcePath.getFileName().toString();
        Path destinationPath = buildUniquePath(getInvalidFolder(), originalFileName);

        Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

        return destinationPath.normalize().toString();
    }

    // Delete a stored file if it exists
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath).normalize();
        Files.deleteIfExists(path);
    }

    // Return all files currently in the storage folder
    public List<Path> getAllStoredFiles() throws IOException {
        ensureFoldersExist();

        List<Path> files = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(getStorageFolder())) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    files.add(path.normalize());
                }
            }
        }

        return files;
    }

    private Path buildUniquePath(Path targetFolder, String originalFileName) {
        Path candidate = targetFolder.resolve(originalFileName).normalize();

        if (!Files.exists(candidate)) {
            return candidate;
        }

        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;
        return targetFolder.resolve(uniqueFileName).normalize();
    }
}