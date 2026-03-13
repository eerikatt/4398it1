package com.example.db;

import com.example.config.RegistryConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final RegistryConfig config;

    public DatabaseConnection() {
        this(RegistryConfig.defaultConfig());
    }

    public DatabaseConnection(RegistryConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                config.getDbUrl(),
                config.getDbUser(),
                config.getDbPassword()
        );
    }
}