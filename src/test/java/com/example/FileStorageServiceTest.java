/*
    RUN WITH: mvn -Dtest=FileStorageServiceTest test
*/

package com.example;

import com.example.config.RegistryConfig;
import com.example.service.FileStorageService;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileStorageServiceTest {

    // Temporary/testing elements
    private Path testRoot;
    private RegistryConfig testConfig;
    private FileStorageService fileStorageService;

    // Before each test
    @BeforeEach
    void setUp() throws Exception {
        // Temp directory
        testRoot = Files.createTempDirectory("document-registry-test");

        // Make config with test values
        testConfig = new RegistryConfig(
                testRoot.resolve("storage"),
                testRoot.resolve("invalid-documents"),
                "jdbc:test",
                "test_user",
                "test_password"
        );

        // Create using test config
        fileStorageService = new FileStorageService(testConfig);
    }

    // After each test
    @AfterEach
    void tearDown() throws Exception {
        // Delete test folder and contentst
        if (testRoot != null && Files.exists(testRoot)) {
            Files.walk(testRoot)
                    // Delete files first
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception e) {
                            // Ignore cleanup errors
                        }
                    });
        }
    }

    // Print test results
    private void printStart(String testName, String description) {
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());

        System.out.println();
        System.out.println("TIME: " + now);
        System.out.println("RUNNING: " + testName);
        System.out.println(description);
    }

    private void printPass() {
        System.out.println("*** [PASS] ***");
    }

    private void printFail(Exception e) {
        System.out.println("*** [FAIL] ***");
        System.out.println("Reason: " + e.getMessage());
    }

    // Test 1: Make sure ensureFoldersExist() creates storage and invalid-documents
    @Test
    @Order(1)
    @DisplayName("Test 1 - ensureFoldersExist() creates storage and invalid-documents")
    void ensureFoldersExistCreatesStorageAndInvalidFolders() throws Exception {
        printStart(
                "Test 1",
                "ensureFoldersExist() creates storage and invalid-documents"
        );

        try {
            fileStorageService.ensureFoldersExist();

            // storage folder now exists
            assertTrue(Files.exists(testConfig.getStorageFolder()));
            assertTrue(Files.isDirectory(testConfig.getStorageFolder()));

            // invalid-documents folder exists
            assertTrue(Files.exists(testConfig.getInvalidFolder()));
            assertTrue(Files.isDirectory(testConfig.getInvalidFolder()));

            printPass();
        } catch (Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 2: getAllStoredFiles() returns files only
    @Test
    @Order(2)
    @DisplayName("Test 2 - getAllStoredFiles() returns files only")
    void getAllStoredFilesReturnsOnlyFilesInStorageFolder() throws Exception {
        printStart(
                "Test 2",
                "getAllStoredFiles() returns files only."
        );

        try {
            fileStorageService.ensureFoldersExist();

            // Create two files
            Path file1 = testConfig.getStorageFolder().resolve("testText.txt");
            Path file2 = testConfig.getStorageFolder().resolve("testPdf.pdf");

            // Create one folder
            Path subFolder = testConfig.getStorageFolder().resolve("subfolder");

            Files.writeString(file1, "test 1");
            Files.writeString(file2, "test 2");
            Files.createDirectories(subFolder);

            // Call getAllStoredFiles()
            List<Path> files = fileStorageService.getAllStoredFiles();

            // Check returns
            assertEquals(2, files.size());
            assertTrue(files.contains(file1.normalize()));
            assertTrue(files.contains(file2.normalize()));

            // Check for folderr
            assertFalse(files.contains(subFolder.normalize()));

            printPass();
        } catch (Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 3: moveToInvalidFolder() moves file to invalid-documents
    @Test
    @Order(3)
    @DisplayName("Test 3 - moveToInvalidFolder() moves a file to invalid-documents")
    void moveToInvalidFolderMovesFileOutOfStorage() throws Exception {
        printStart(
                "Test 3",
                "oveToInvalidFolder() moves file to invalid-documents."
        );

        try {
            fileStorageService.ensureFoldersExist();

            // Test file
            Path originalFile = testConfig.getStorageFolder().resolve("badfile.exe");
            Files.writeString(originalFile, "invalid file");

            // Move file
            String movedPathString = fileStorageService.moveToInvalidFolder(originalFile);
            Path movedPath = Path.of(movedPathString);

            // Check existence
            assertFalse(Files.exists(originalFile));

            // Moved file should exist
            assertTrue(Files.exists(movedPath));

            // Check location
            assertTrue(movedPath.startsWith(testConfig.getInvalidFolder()));

            printPass();
        } catch (Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 4: deleteFile()
    @Test
    @Order(4)
    @DisplayName("Test 4 - deleteFile() (removes an existing file)")
    void deleteFileRemovesExistingFile() throws Exception {
        printStart(
                "Test 4",
                "deleteFile() removes existing file."
        );

        try {
            fileStorageService.ensureFoldersExist();

            // Create file in storage
            Path file = testConfig.getStorageFolder().resolve("temp.txt");
            Files.writeString(file, "delete me");

            // Check if file exists
            assertTrue(Files.exists(file));

            // deleteFile()
            fileStorageService.deleteFile(file.toString());

            // Check if deleted
            assertFalse(Files.exists(file));

            printPass();
        } catch (Exception e) {
            printFail(e);
            throw e;
        }
    }
}