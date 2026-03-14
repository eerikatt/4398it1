package com.example.gui;

import com.example.guiapimodule.DefaultGuiApiModule;
import com.example.guiapimodule.GuiApiModule;
import com.example.model.Document;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DocumentRegistryApp extends Application {

    // Connects gui to backend interface
    private GuiApiModule apiModule;

    // Main window reference for file chooser dialogs
    private Stage primaryStage;

    // Displayed table
    private TableView<Document> table;

    // List of all documents
    private ObservableList<Document> masterData;

    // Used to narrow search
    private FilteredList<Document> filteredData;

    // Search box
    private TextField searchField;

    // Side panel info
    private Label idValue;
    private Label nameValue;
    private Label typeValue;
    private Label sizeValue;
    private Label pathValue;
    private Label createdValue;

    // Reusable style strings
    private final String rootStyle =
            "-fx-background-color: linear-gradient(to bottom, #f8fafc, #eef2f7);";

    private final String panelStyle =
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-radius: 16;" +
            "-fx-border-color: #dbe3ee;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 18, 0.15, 0, 4);";

    private final String titleStyle =
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #0f172a;";

    private final String sectionTitleStyle =
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #0f172a;";

    private final String labelTitleStyle =
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #64748b;";

    private final String labelValueStyle =
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #0f172a;";

    private final String primaryButtonStyle =
            "-fx-background-color: #2563eb;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 8 14 8 14;" +
            "-fx-cursor: hand;";

    private final String secondaryButtonStyle =
            "-fx-background-color: #e2e8f0;" +
            "-fx-text-fill: #0f172a;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 8 14 8 14;" +
            "-fx-cursor: hand;";

    private final String dangerButtonStyle =
            "-fx-background-color: #dc2626;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 8 14 8 14;" +
            "-fx-cursor: hand;";

    private final String fieldStyle =
            "-fx-background-color: #f8fafc;" +
            "-fx-border-color: #cbd5e1;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 8 10 8 10;" +
            "-fx-font-size: 13px;";

    // Setup window
    @Override
    public void start(Stage stage) {
        // Save stage for upload/download dialogs
        primaryStage = stage;

        // Api module from backend for accessing data
        apiModule = new DefaultGuiApiModule();

        // Layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));
        root.setStyle(rootStyle);

        // Create table
        table = new TableView<>();
        setupTable();
        setupSelectionListener();

        // Get list of all documents for masterData, and filteredData will contain
        // all documents since no filter has been applied yet
        masterData = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(masterData, document -> true);
        table.setItems(filteredData);

        // Set top, center, and right sections
        VBox topSection = new VBox(12, buildHeader(), buildSearchBar());
        root.setTop(topSection);

        StackPane tablePane = new StackPane(table);
        tablePane.setPadding(new Insets(0));
        tablePane.setStyle(panelStyle);

        VBox detailsPane = buildDetailsPanel();
        detailsPane.setStyle(panelStyle);

        root.setCenter(tablePane);
        root.setRight(detailsPane);
        BorderPane.setMargin(tablePane, new Insets(16, 16, 0, 0));
        BorderPane.setMargin(detailsPane, new Insets(16, 0, 0, 0));

        // Window size and title
        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Document Registry");    // Window title

        // Show window
        stage.setScene(scene);
        stage.show();
    }

    // Build header with title and buttons
    private HBox buildHeader() {
        Label title = new Label("Document Registry");
        title.setStyle(titleStyle);

        Label subtitle = new Label("Manage, search, and organize stored documents");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        VBox titleBox = new VBox(4, title, subtitle);

        HBox header = new HBox(12, titleBox);
        header.setAlignment(Pos.CENTER_LEFT);

        return header;

    }

    // Search and sort bar
    private HBox buildSearchBar() {
        // Search label and text box
        Label searchLabel = new Label("Search");
        searchLabel.setStyle(labelTitleStyle);

        searchField = new TextField();
        searchField.setPromptText("Filter by file name, type, or path.");
        searchField.setStyle(fieldStyle);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            applyFilter(newValue);
        });
            
        // Clear button
        Button clearBtn = new Button("Clear");
        clearBtn.setStyle(secondaryButtonStyle);
        clearBtn.setOnAction(e -> searchField.clear());

        // Empty labels for allignment
        Label clearSpacer = new Label(" ");
        clearSpacer.setStyle(labelTitleStyle);

        Label sortSpacer = new Label(" ");
        sortSpacer.setStyle(labelTitleStyle);

        // Seperate vbox for each to maintain level allignment
        VBox searchBox = new VBox(6, searchLabel, searchField);
        VBox clearBox = new VBox(6, clearSpacer, clearBtn);

        // Search field adjusts to size of input
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        // Main row
        HBox searchBar = new HBox(12, searchBox, clearBox);
        searchBar.setAlignment(Pos.TOP_LEFT);

        StackPane barCard = new StackPane(searchBar);
        barCard.setPadding(new Insets(14));
        barCard.setStyle(panelStyle);

        return new HBox(barCard);
    }

    // Side panel
    private VBox buildDetailsPanel() {
        // Title and labels for document details
        Label title = new Label("Document Details");
        title.setStyle(sectionTitleStyle);

        // '-' when no doc is selected
        idValue = new Label("-");
        nameValue = new Label("-");
        typeValue = new Label("-");
        sizeValue = new Label("-");
        pathValue = new Label("-");
        pathValue.setWrapText(true);
        createdValue = new Label("-");

        styleDetailValue(idValue);
        styleDetailValue(nameValue);
        styleDetailValue(typeValue);
        styleDetailValue(sizeValue);
        styleDetailValue(pathValue);
        styleDetailValue(createdValue);

        // Layout (grid style)
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);

        Label idLabel = new Label("ID");
        Label nameLabel = new Label("File Name");
        Label typeLabel = new Label("Type");
        Label sizeLabel = new Label("Size");
        Label pathLabel = new Label("Path");
        Label createdLabel = new Label("Created");

        styleDetailLabel(idLabel);
        styleDetailLabel(nameLabel);
        styleDetailLabel(typeLabel);
        styleDetailLabel(sizeLabel);
        styleDetailLabel(pathLabel);
        styleDetailLabel(createdLabel);

        // Labels
        grid.add(idLabel, 0, 0);
        grid.add(idValue, 1, 0);

        grid.add(nameLabel, 0, 1);
        grid.add(nameValue, 1, 1);

        grid.add(typeLabel, 0, 2);
        grid.add(typeValue, 1, 2);

        grid.add(sizeLabel, 0, 3);
        grid.add(sizeValue, 1, 3);

        grid.add(pathLabel, 0, 4);
        grid.add(pathValue, 1, 4);

        grid.add(createdLabel, 0, 5);
        grid.add(createdValue, 1, 5);

        // Column size (second is adjustable given values)
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(85);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(col1, col2);

        // Layout
        VBox panel = new VBox(16, title, new Separator(), grid);
        panel.setPadding(new Insets(18));
        panel.setPrefWidth(340);
        panel.setMinWidth(300);

        return panel;
    }

    // Main table
    private void setupTable() {
        TableColumn<Document, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(70);

        TableColumn<Document, String> nameCol = new TableColumn<>("File Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        nameCol.setPrefWidth(220);

        TableColumn<Document, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("fileType"));
        typeCol.setPrefWidth(90);

        TableColumn<Document, Long> sizeCol = new TableColumn<>("Size (bytes)");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        sizeCol.setPrefWidth(120);

        TableColumn<Document, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        pathCol.setPrefWidth(450);

        // Add cols
        table.getColumns().addAll(idCol, nameCol, typeCol, sizeCol, pathCol);

        // Adjust size
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Default placeholder (no documents to show in table)
        table.setPlaceholder(new Label("No documents to display."));

        // Table style
        table.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-control-inner-background: white;" +
                "-fx-table-cell-border-color: #e2e8f0;" +
                "-fx-font-size: 13px;"
        );
        table.setFixedCellSize(36);
    }

    // When selected row changes, update side panel
    private void setupSelectionListener() {
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldDoc, newDoc) -> {
            showDocumentDetails(newDoc);
        });
    }

    // Filter documents based on search text
    private void applyFilter(String filterText) {
        // If null set as "", else make it lowercase
        String filter = filterText == null ? "" : filterText.trim().toLowerCase();

        filteredData.setPredicate(document -> {
            if (filter.isEmpty()) {
                return true;
            }

            // If null then set as "", else make it lowercase
            String fileName = document.getFileName() == null ? "" : document.getFileName().toLowerCase();
            String fileType = document.getFileType() == null ? "" : document.getFileType().toLowerCase();
            String filePath = document.getFilePath() == null ? "" : document.getFilePath().toLowerCase();

            // Check if fields contain text
            return fileName.contains(filter)
                    || fileType.contains(filter)
                    || filePath.contains(filter);
        });
    }

    // Get details of document
    private void showDocumentDetails(Document document) {
        if (document == null) {
            clearDetails();
            return;
        }

        idValue.setText(String.valueOf(document.getId()));
        nameValue.setText(document.getFileName());
        typeValue.setText(document.getFileType());
        sizeValue.setText(String.valueOf(document.getFileSize()));
        pathValue.setText(document.getFilePath());
        createdValue.setText(
                document.getCreatedAt() == null ? "-" : document.getCreatedAt().toString()
        );
    }

    // Nulls ('-') when no doc is selected
    private void clearDetails() {
        idValue.setText("-");
        nameValue.setText("-");
        typeValue.setText("-");
        sizeValue.setText("-");
        pathValue.setText("-");
        createdValue.setText("-");
    }

    // Style helper for detail labels
    private void styleDetailLabel(Label label) {
        label.setStyle(labelTitleStyle);
    }

    // Style helper for detail values
    private void styleDetailValue(Label label) {
        label.setStyle(labelValueStyle);
    }

    // Launch
    public static void main(String[] args) {
        launch(args);
    }
}
