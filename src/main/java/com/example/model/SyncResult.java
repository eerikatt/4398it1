package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class SyncResult {
    private final List<Document> addedToDatabase;
    private final List<Document> missingFromStorage;

    public SyncResult() {
        this.addedToDatabase = new ArrayList<>();
        this.missingFromStorage = new ArrayList<>();
    }

    public List<Document> getAddedToDatabase() {
        return addedToDatabase;
    }

    public List<Document> getMissingFromStorage() {
        return missingFromStorage;
    }

    public void addAddedToDatabase(Document document) {
        addedToDatabase.add(document);
    }

    public void addMissingFromStorage(Document document) {
        missingFromStorage.add(document);
    }

    @Override
    public String toString() {
        return "SyncResult{" +
                "addedToDatabase=" + addedToDatabase.size() +
                ", missingFromStorage=" + missingFromStorage.size() +
                '}';
    }
}