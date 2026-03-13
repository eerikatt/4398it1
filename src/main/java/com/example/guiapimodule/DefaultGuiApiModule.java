package com.example.guiapimodule;

import com.example.config.RegistryConfig;
import com.example.db.DatabaseInitializer;
import com.example.model.Document;
import com.example.model.SyncResult;
import com.example.service.DocumentService;
import com.example.service.FileStorageService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class DefaultGuiApiModule implements GuiApiModule {
    private final DocumentService documentService;
    private final RegistryConfig config;

    public DefaultGuiApiModule() {
        this(RegistryConfig.defaultConfig());
    }

    public DefaultGuiApiModule(RegistryConfig config) {
        this.config = config;

        FileStorageService fileStorageService = new FileStorageService(config);
        try {
            fileStorageService.ensureFoldersExist();
        } catch (IOException e) {
            throw new RuntimeException("Error creating runtime folders.", e);
        }

        DatabaseInitializer databaseInitializer = new DatabaseInitializer(config);
        databaseInitializer.initialize();

        this.documentService = new DocumentService(config);
    }

    @Override
    public RegistryConfig getConfig() {
        return config;
    }

    @Override
    public Document registerDocumentFromStorage(Path storagePath) {
        return documentService.registerDocumentFromStorage(storagePath);
    }

    @Override
    public List<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @Override
    public Document getDocumentById(int id) {
        return documentService.getDocumentById(id);
    }

    @Override
    public boolean deleteDocument(int id) {
        return documentService.deleteDocument(id);
    }

    @Override
    public int removeUnsupportedDocumentsFromDatabase() {
        return documentService.removeUnsupportedDocumentsFromDatabase();
    }

    @Override
    public SyncResult syncStorageAndDatabase() {
        return documentService.syncStorageAndDatabase();
    }
}