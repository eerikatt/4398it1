package com.example.guiapimodule;

import com.example.config.RegistryConfig;
import com.example.model.Document;
import com.example.model.SyncResult;

import java.nio.file.Path;
import java.util.List;

public interface GuiApiModule {
    RegistryConfig getConfig();

    Document registerDocumentFromStorage(Path storagePath);

    List<Document> getAllDocuments();

    Document getDocumentById(int id);

    boolean deleteDocument(int id);

    int removeUnsupportedDocumentsFromDatabase();

    SyncResult syncStorageAndDatabase();
}