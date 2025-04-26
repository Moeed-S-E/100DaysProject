package application;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private ObservableList<Event> events = FXCollections.observableArrayList();
    private TableView<Event> eventTable;
    private Button addButton, editButton, deleteButton;


    private static final String EVENTS_FILE = "events.csv";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Calendar Application|Moeed Sajid");
        primaryStage.setWidth(400);
        primaryStage.setHeight(600);


        loadEventsFromFile();

        // Initialize UI components
        initComponents();

        // Layout setup
        BorderPane root = new BorderPane();
        VBox controlPanel = createControlPanel();
        root.setCenter(new ScrollPane(eventTable));
        root.setBottom(controlPanel);

        // Scene setup
        Scene scene = new Scene(root);

        // Load CSS file (if it exists)
        String cssPath = getClass().getResource("styles.css") != null
                ? getClass().getResource("styles.css").toExternalForm()
                : null;

        if (cssPath != null) {
            scene.getStylesheets().add(cssPath); // Add CSS if file exists
        } else {
            System.err.println("Warning: styles.css not found. No styles applied.");
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Save events to CSV file when the program exits
        saveEventsToFile();
    }

    private void initComponents() {
        // Table view for events
        eventTable = new TableView<>();
        eventTable.setItems(events);

        TableColumn<Event, String> nameColumn = new TableColumn<>("Event Name");
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());

        TableColumn<Event, String> dateColumn = new TableColumn<>("Event Date");
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());

        eventTable.getColumns().addAll(nameColumn, dateColumn);

        // Buttons
        addButton = new Button("Add Event");
        editButton = new Button("Edit Event");
        deleteButton = new Button("Delete Event");

        // Add button action
        addButton.setOnAction(e -> {
            Event newEvent = createNewEvent();
            if (newEvent != null) {
                events.add(newEvent);
            }
        });

        // Edit button action
        editButton.setOnAction(e -> {
            Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                editEvent(selectedEvent);
            } else {
                showAlert("No Event Selected", "Please select an event to edit.");
            }
        });

        // Delete button action
        deleteButton.setOnAction(e -> {
            Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                events.remove(selectedEvent);
            } else {
                showAlert("No Event Selected", "Please select an event to delete.");
            }
        });
    }

    private VBox createControlPanel() {
        HBox buttons = new HBox(10, addButton, editButton, deleteButton);
        buttons.setPadding(new Insets(10));

        VBox controlPanel = new VBox(10, buttons);
        controlPanel.setPadding(new Insets(10));
        return controlPanel;
    }

    private Event createNewEvent() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Event");
        nameDialog.setHeaderText("Enter event details:");
        nameDialog.setContentText("Event Name:");

        String name = nameDialog.showAndWait().orElse(null);
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        Dialog<LocalDate> dateDialog = new Dialog<>();
        dateDialog.setTitle("Select Event Date");
        dateDialog.setHeaderText("Choose a date:");
        dateDialog.getDialogPane().setContent(datePicker);

        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dateDialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Set result converter to ensure only valid dates are returned
        dateDialog.setResultConverter(button -> {
            if (button == confirmButtonType) {
                return datePicker.getValue(); // Return the selected date
            }
            return null; // Return null if Cancel is clicked
        });

        LocalDate date = dateDialog.showAndWait().orElse(null);
        if (date == null) {
            return null;
        }

        return new Event(name, date);
    }

    private void editEvent(Event event) {
        TextInputDialog nameDialog = new TextInputDialog(event.getName());
        nameDialog.setTitle("Edit Event");
        nameDialog.setHeaderText("Edit event details:");
        nameDialog.setContentText("Event Name:");

        String newName = nameDialog.showAndWait().orElse(null);
        if (newName == null || newName.trim().isEmpty()) {
            return;
        }

        DatePicker datePicker = new DatePicker(event.getDate());
        Dialog<LocalDate> dateDialog = new Dialog<>();
        dateDialog.setTitle("Edit Event Date");
        dateDialog.setHeaderText("Choose a new date:");
        dateDialog.getDialogPane().setContent(datePicker);

        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dateDialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Set result converter to ensure only valid dates are returned
        dateDialog.setResultConverter(button -> {
            if (button == confirmButtonType) {
                return datePicker.getValue(); // Return the selected date
            }
            return null; // Return null if Cancel is clicked
        });

        LocalDate newDate = dateDialog.showAndWait().orElse(null);
        if (newDate != null) {
            event.setName(newName);
            event.setDate(newDate);
            eventTable.refresh();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Save events to a CSV file
    private void saveEventsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EVENTS_FILE))) {
            for (Event event : events) {
                writer.write(event.getName() + "," + event.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                writer.newLine();
            }
            System.out.println("Events saved to " + EVENTS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving events to file: " + e.getMessage());
        }
    }

    // Load events from a CSV file
    private void loadEventsFromFile() {
        File file = new File(EVENTS_FILE);
        if (!file.exists()) {
            System.out.println("No events file found. Starting with an empty list.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<Event> loadedEvents = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    LocalDate date = LocalDate.parse(parts[1].trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                    loadedEvents.add(new Event(name, date));
                }
            }
            events.addAll(loadedEvents);
            System.out.println("Events loaded from " + EVENTS_FILE);
        } catch (IOException e) {
            System.err.println("Error loading events from file: " + e.getMessage());
        }
    }
}

