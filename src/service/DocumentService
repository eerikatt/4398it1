package com.example.service;

import com.example.config.RegistryConfig;
import com.example.dao.DocumentDAO;
import com.example.model.Document;
import com.example.model.SyncResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentService {
    private final DocumentDAO documentDAO;
    private final FileStorageService fileStorageService;

    private static final Set<String> ALLOWED_FILE_TYPES = Set.of("pdf", "docx", "txt");

    public DocumentService() {
        this(RegistryConfig.defaultConfig());
    }

    public DocumentService(RegistryConfig config) {
        this.documentDAO = new DocumentDAO(config);
        this.fileStorageService = new FileStorageService(config);
    }

    // Register a file that already exists in the storage folder
    public Document registerDocumentFromStorage(Path storagePath) {
        try {
            fileStorageService.ensureFoldersExist();

            Path normalizedPath = storagePath.normalize();

            if (!Files.exists(normalizedPath) || !Files.isRegularFile(normalizedPath)) {
                throw new IllegalArgumentException("File does not exist in storage: " + normalizedPath);
            }

            if (!normalizedPath.startsWith(fileStorageService.getStorageFolder())) {
                throw new IllegalArgumentException("File must already be inside the storage folder.");
            }

            String fileName = normalizedPath.getFileName().toString();
            String fileType = getFileExtension(fileName);

            // If unsupported file, move it out of storage and do not insert into database
            if (!ALLOWED_FILE_TYPES.contains(fileType)) {
                String invalidPath = fileStorageService.moveToInvalidFolder(normalizedPath);

                // If an old DB row somehow exists for this same storage path, remove it
                documentDAO.deleteDocumentByFilePath(normalizedPath.toString());

                throw new IllegalArgumentException(
                        "Unsupported file type: " + fileType + ". File moved to: " + invalidPath
                );
            }

            // If file is already registered, return existing row
            Document existing = documentDAO.getDocumentByFilePath(normalizedPath.toString());
            if (existing != null) {
                return existing;
            }

            long fileSize = Files.size(normalizedPath);

            Document document = new Document(
                    fileName,
                    fileType,
                    normalizedPath.toString(),
                    fileSize
            );

            documentDAO.addDocument(document);
            return document;

        } catch (IOException e) {
            throw new RuntimeException("Error registering file from storage.", e);
        }
    }

    // Return all documents
    public List<Document> getAllDocuments() {
        return documentDAO.getAllDocuments();
    }

    // Returns one document id
    public Document getDocumentById(int id) {
        return documentDAO.getDocumentById(id);
    }

    // Deletes a document from both the database and storage folder
    public boolean deleteDocument(int id) {
        Document document = documentDAO.getDocumentById(id);

        if (document == null) {
            return false;
        }

        try {
            fileStorageService.deleteFile(document.getFilePath());
            return documentDAO.deleteDocument(id);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting stored file.", e);
        }
    }

    // Move unsupported files to invalid documents directory and remove their DB rows
    public int removeUnsupportedDocumentsFromDatabase() {
        List<Document> documents = documentDAO.getAllDocuments();
        int removedCount = 0;

        for (Document document : documents) {
            String fileType = document.getFileType();

            if (!ALLOWED_FILE_TYPES.contains(fileType)) {
                Path filePath = Path.of(document.getFilePath()).normalize();

                try {
                    if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                        if (filePath.startsWith(fileStorageService.getStorageFolder())) {
                            fileStorageService.moveToInvalidFolder(filePath);
                        }
                    }
                } catch (IOException e) {
                    // Keep going and remove the DB row
                }

                boolean deleted = documentDAO.deleteDocument(document.getId());
                if (deleted) {
                    removedCount++;
                }
            }
        }

        return removedCount;
    }

    // Syncs storage and database without deleting valid files
    public SyncResult syncStorageAndDatabase() {
        SyncResult result = new SyncResult();

        try {
            fileStorageService.ensureFoldersExist();

            List<Document> initialDbDocuments = documentDAO.getAllDocuments();
            Set<String> dbPaths = new HashSet<>();

            for (Document document : initialDbDocuments) {
                dbPaths.add(document.getFilePath());
            }

            // Process everything currently in storage
            List<Path> storedFiles = fileStorageService.getAllStoredFiles();

            for (Path storedFile : storedFiles) {
                String storedPath = storedFile.normalize().toString();
                String fileName = storedFile.getFileName().toString();
                String fileType = getFileExtension(fileName);

                // If unsupported file in storage. move it out and remove matching DB row if one exists
                if (!ALLOWED_FILE_TYPES.contains(fileType)) {
                    documentDAO.deleteDocumentByFilePath(storedPath);
                    fileStorageService.moveToInvalidFolder(storedFile);
                    continue;
                }

                // If supported file in storage but missing from DB, add it
                if (!dbPaths.contains(storedPath)) {
                    long fileSize = Files.size(storedFile);

                    Document document = new Document(
                            fileName,
                            fileType,
                            storedPath,
                            fileSize
                    );

                    documentDAO.addDocument(document);
                    result.addAddedToDatabase(document);
                }
            }

            // After sync adds/moves, report DB rows still missing from storage
            List<Document> updatedDbDocuments = documentDAO.getAllDocuments();

            for (Document document : updatedDbDocuments) {
                Path dbFilePath = Path.of(document.getFilePath()).normalize();

                if (!Files.exists(dbFilePath)) {
                    result.addMissingFromStorage(document);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error during storage/database sync.", e);
        }

        return result;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1).toLowerCase();
    }
}