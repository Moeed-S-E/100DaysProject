module CodeVisualizer {
    requires javafx.controls;
    requires javafx.fxml;
    opens main.app to javafx.fxml;
    opens main.controller to javafx.fxml;
    opens main.model to javafx.fxml;
    exports main.app;
}