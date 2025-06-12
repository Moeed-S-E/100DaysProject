package app.controller;

import app.data.DataStore;
import app.models.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class InventoryController {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> nameCol;
    @FXML private TableColumn<Product, Integer> stockCol;
    @FXML private TableColumn<Product, Double> priceCol;
    @FXML private Button addButton;

    private final ObservableList<Product> productList = FXCollections.observableArrayList(DataStore.products);

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        productTable.setItems(productList);

        addButton.setOnAction(e -> showAddProductDialog());
    }

    private void showAddProductDialog() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Enter product details");

        // Use absolute path for modern.css
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/modern.css").toExternalForm());

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");
        TextField stockField = new TextField();
        stockField.setPromptText("Stock");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Stock:"), 0, 1);
        grid.add(stockField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText();
                    int stock = Integer.parseInt(stockField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    return new Product(name, stock, price);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Error adding product");
                    alert.setContentText("Please enter valid values for all fields.");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(product -> {
            DataStore.products.add(product);
            productList.setAll(DataStore.products);
        });
    }
}