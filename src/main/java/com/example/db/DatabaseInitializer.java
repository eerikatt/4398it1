package com.example.db;

import com.example.config.RegistryConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private final DatabaseConnection databaseConnection;

    public DatabaseInitializer() {
        this(RegistryConfig.defaultConfig());
    }

    public DatabaseInitializer(RegistryConfig config) {
        this.databaseConnection = new DatabaseConnection(config);
    }

    public void initialize() {
        String sql = """
                CREATE TABLE IF NOT EXISTS documents (
                    id SERIAL PRIMARY KEY,
                    file_name VARCHAR(255) NOT NULL,
                    file_type VARCHAR(20) NOT NULL,
                    file_path TEXT NOT NULL UNIQUE,
                    file_size BIGINT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database schema.", e);
        }
    }
}