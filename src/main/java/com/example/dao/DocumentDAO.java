package com.example.dao;

import com.example.config.RegistryConfig;
import com.example.db.DatabaseConnection;
import com.example.model.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO {
    private final DatabaseConnection databaseConnection;

    public DocumentDAO() {
        this(RegistryConfig.defaultConfig());
    }

    public DocumentDAO(RegistryConfig config) {
        this.databaseConnection = new DatabaseConnection(config);
    }

    // Inserts a new document row into the database
    public void addDocument(Document document) {
        String sql = "INSERT INTO documents (file_name, file_type, file_path, file_size) VALUES (?, ?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, document.getFileName());
            stmt.setString(2, document.getFileType());
            stmt.setString(3, document.getFilePath());
            stmt.setLong(4, document.getFileSize());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    document.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error adding document.", e);
        }
    }

    // Returns all document rows from the database
    public List<Document> getAllDocuments() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM documents ORDER BY created_at DESC";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Document document = new Document(
                        rs.getInt("id"),
                        rs.getString("file_name"),
                        rs.getString("file_type"),
                        rs.getString("file_path"),
                        rs.getLong("file_size"),
                        rs.getTimestamp("created_at")
                );

                documents.add(document);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving documents.", e);
        }

        return documents;
    }

    // Returns one document by its id, or null if not found
    public Document getDocumentById(int id) {
        String sql = "SELECT * FROM documents WHERE id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Document(
                            rs.getInt("id"),
                            rs.getString("file_name"),
                            rs.getString("file_type"),
                            rs.getString("file_path"),
                            rs.getLong("file_size"),
                            rs.getTimestamp("created_at")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving document by id.", e);
        }

        return null;
    }

    // Returns one document by its file path, or null if not found
    public Document getDocumentByFilePath(String filePath) {
        String sql = "SELECT * FROM documents WHERE file_path = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filePath);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Document(
                            rs.getInt("id"),
                            rs.getString("file_name"),
                            rs.getString("file_type"),
                            rs.getString("file_path"),
                            rs.getLong("file_size"),
                            rs.getTimestamp("created_at")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving document by file path.", e);
        }

        return null;
    }

    // Deletes one document row by id and returns true if a row was deleted
    public boolean deleteDocument(int id) {
        String sql = "DELETE FROM documents WHERE id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting document.", e);
        }
    }

    // Deletes one document row by file path and returns true if a row was deleted
    public boolean deleteDocumentByFilePath(String filePath) {
        String sql = "DELETE FROM documents WHERE file_path = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filePath);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting document by file path.", e);
        }
    }
}