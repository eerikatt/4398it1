package com.example.gui;

import com.example.guiapimodule.DefaultGuiApiModule;
import com.example.guiapimodule.GuiApiModule;
import com.example.model.Document;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

        // Set top, center, and right sections
        VBox topSection = new VBox(12, buildHeader());
        root.setTop(topSection);

        StackPane tablePane = new StackPane(table);
        tablePane.setPadding(new Insets(0));
        tablePane.setStyle(panelStyle);

        root.setCenter(tablePane);
        BorderPane.setMargin(tablePane, new Insets(16, 16, 0, 0));

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

    // Launch
    public static void main(String[] args) {
        launch(args);
    }
}
