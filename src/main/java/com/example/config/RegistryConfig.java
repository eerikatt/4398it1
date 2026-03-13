package com.example.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RegistryConfig {
    private final Path storageFolder;
    private final Path invalidFolder;
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public RegistryConfig(Path storageFolder, Path invalidFolder, String dbUrl, String dbUser, String dbPassword) {
        this.storageFolder = storageFolder.normalize();
        this.invalidFolder = invalidFolder.normalize();
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    // Current default values
    public static RegistryConfig defaultConfig() {
    return new RegistryConfig(
            Paths.get("runtime", "storage"),
            Paths.get("runtime", "invalid-documents"),
            "jdbc:postgresql://localhost:5432/myapp_db",
            "myapp_user",
            "password"
    );
}
    public Path getStorageFolder() {
        return storageFolder;
    }

    public Path getInvalidFolder() {
        return invalidFolder;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }
}