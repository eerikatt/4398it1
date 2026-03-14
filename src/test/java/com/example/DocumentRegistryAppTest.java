/*
    RUN WITH: mvn -Dtest=DocumentRegistryAppTest test
*/

package com.example;

import com.example.gui.DocumentRegistryApp;
import com.example.model.Document;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DocumentRegistryAppTest {

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

    // Test 1: Empty filter matches all documents
    @Test
    @Order(1)
    @DisplayName("Test 1: Empty filter matches all documents")
    void emptyFilterMatchesAll() throws Exception {
        printStart("Test 1", "Empty filter matches all documents.");

        try {
            Document doc = new Document("report.pdf", "pdf", "/storage/report.pdf", 1000L);
            assertTrue(DocumentRegistryApp.documentMatchesFilter(doc, ""));
            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 2: Null filter matches all documents
    @Test
    @Order(2)
    @DisplayName("Test 2: Null filter matches all documents")
    void nullFilterMatchesAll() throws Exception {
        printStart("Test 2", "Null filter matches all documents.");

        try {
            Document doc = new Document("report.pdf", "pdf", "/storage/report.pdf", 1000L);
            assertTrue(DocumentRegistryApp.documentMatchesFilter(doc, null));
            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 3: Filter matches by file name
    @Test
    @Order(3)
    @DisplayName("Test 3: Filter matches by file name")
    void filterMatchesByFileName() throws Exception {
        printStart("Test 3", "Filter matches by file name.");

        try {
            Document doc = new Document("report.pdf", "pdf", "/storage/report.pdf", 1000L);
            assertTrue(DocumentRegistryApp.documentMatchesFilter(doc, "report"));
            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 4: Filter matches by file type
    @Test
    @Order(4)
    @DisplayName("Test 4: Filter matches by file type")
    void filterMatchesByFileType() throws Exception {
        printStart("Test 4", "Filter matches by file type.");

        try {
            Document doc = new Document("notes.txt", "txt", "/storage/notes.txt", 500L);
            assertTrue(DocumentRegistryApp.documentMatchesFilter(doc, "txt"));
            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 5: Filter matches by file path
    @Test
    @Order(5)
    @DisplayName("Test 5: Filter matches by file path")
    void filterMatchesByFilePath() throws Exception {
        printStart("Test 5", "Filter matches by file path.");

        try {
            Document doc = new Document("report.pdf", "pdf", "/storage/archive/report.pdf", 1000L);
            assertTrue(DocumentRegistryApp.documentMatchesFilter(doc, "archive"));
            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 6: Filter is case-insensitive
    @Test
    @Order(6)
    @DisplayName("Test 6: Filter is case-insensitive")
    void filterIsCaseInsensitive() throws Exception {
        printStart("Test 6", "Filter is case-insensitive.");

        try {
            Document doc = new Document("Report.PDF", "PDF", "/Storage/Report.PDF", 1000L);
            assertTrue(DocumentRegistryApp.documentMatchesFilter(doc, "report.pdf"));
            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 7: Filter returns false when no fields match
    @Test
    @Order(7)
    @DisplayName("Test 7: Filter returns false when no fields match")
    void filterNoMatch() throws Exception {
        printStart("Test 7", "Filter returns false when no fields match.");

        try {
            Document doc = new Document("notes.txt", "txt", "/storage/notes.txt", 500L);
            assertFalse(DocumentRegistryApp.documentMatchesFilter(doc, "zzz"));
            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 8: Document with null fields does not throw
    @Test
    @Order(8)
    @DisplayName("Test 8: Document with null fields does not throw")
    void filterHandlesNullDocumentFields() throws Exception {
        printStart("Test 8", "Document with null fields does not throw.");

        try {
            Document doc = new Document();
            doc.setFileName(null);
            doc.setFileType(null);
            doc.setFilePath(null);

            assertFalse(DocumentRegistryApp.documentMatchesFilter(doc, "pdf"));
            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 9: Whitespace-only filter matches all documents
    @Test
    @Order(9)
    @DisplayName("Test 9: Whitespace-only filter matches all documents")
    void whitespaceFilterMatchesAll() throws Exception {
        printStart("Test 9", "Whitespace-only filter matches all documents.");

        try {
            Document doc = new Document("report.pdf", "pdf", "/storage/report.pdf", 1000L);
            assertTrue(DocumentRegistryApp.documentMatchesFilter(doc, "   "));
            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }

    // Test 10: Filter partial match on file name
    @Test
    @Order(10)
    @DisplayName("Test 10: Filter partial match on file name")
    void filterPartialMatchOnFileName() throws Exception {
        printStart("Test 10", "Filter partial match on file name.");

        try {
            Document doc = new Document("quarterly_report_2024.pdf", "pdf", "/storage/quarterly_report_2024.pdf", 2000L);
            assertTrue(DocumentRegistryApp.documentMatchesFilter(doc, "quarterly"));
            printPass();
        } catch (AssertionError | Exception e) {
            printFail(e);
            throw e;
        }
    }
}
