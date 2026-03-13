/*
    RUN WITH: mvn -Dtest=DocumentTest test
*/

package com.example;

import com.example.model.Document;
import org.junit.jupiter.api.*;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DocumentTest {

    // Print results
    private void printStart(String testName, String description) {
        System.out.println();
        System.out.println("RUNNING: " + testName);
        System.out.println(description);
    }

    private void printPass() {
        System.out.println("*** [PASS] ***");
    }

    private void printFail(Throwable e) {
        System.out.println("*** [FAIL] ***");
        System.out.println("Reason: " + e.getMessage());
    }

    // Test 1: 4 passed constructor values properly stored
    @Test
    @Order(1)
    @DisplayName("Test 1: 4 passed constructor values properly stored")
    void fourValSonstructor() throws Exception {
        printStart(
                "Test 1",
                "4 passed constructor values properly stored."
        );

        try {
            Document document = new Document(
                    "test1.txt",
                    "txt",
                    "runtime/storage/notes.txt",
                    1234L
            );

            assertEquals("test1.txt", document.getFileName());
            assertEquals("txt", document.getFileType());
            assertEquals("runtime/storage/notes.txt", document.getFilePath());
            assertEquals(1234L, document.getFileSize());

            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 2: constructor should store all values correctly
    @Test
    @Order(2)
    @DisplayName("Test 2: constructor should store all values correctly")
    void fullConstructorStoresAllValuesCorrectly() throws Exception {
        printStart(
                "Test 2",
                "constructor should store all values correctly."
        );

        try {
            Timestamp createdAt = new Timestamp(System.currentTimeMillis());

            Document document = new Document(
                    5,
                    "test.pdf",
                    "pdf",
                    "runtime/storage/test.pdf",
                    1234L,
                    createdAt
            );

            assertEquals(5, document.getId());
            assertEquals("test.pdf", document.getFileName());
            assertEquals("pdf", document.getFileType());
            assertEquals("runtime/storage/test.pdf", document.getFilePath());
            assertEquals(1234L, document.getFileSize());
            assertEquals(createdAt, document.getCreatedAt());

            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 3: Assignments should update values correctly
    @Test
    @Order(3)
    @DisplayName("Test 3: Assignments should update values correctly")
    void settersUpdateValuesCorrectly() throws Exception {
        printStart(
                "Test 3",
                "Assignments should update values correctly."
        );

        try {
            Timestamp createdAt = new Timestamp(System.currentTimeMillis());

            // Use default constructor and then assign values
            Document document = new Document();

            document.setId(10);
            document.setFileName("test.docx");
            document.setFileType("docx");
            document.setFilePath("runtime/storage/test.docx");
            document.setFileSize(1234L);
            document.setCreatedAt(createdAt);

            assertEquals(10, document.getId());
            assertEquals("test.docx", document.getFileName());
            assertEquals("docx", document.getFileType());
            assertEquals("runtime/storage/test.docx", document.getFilePath());
            assertEquals(1234L, document.getFileSize());
            assertEquals(createdAt, document.getCreatedAt());

            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }
}