package main.ui;

import javafx.scene.control.*;
import main.model.SummaryItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SummaryTableFactory {
    public static TableView<SummaryItem> createSummaryTable() {
        TableView<SummaryItem> table = new TableView<>();
        TableColumn<SummaryItem, String> classCol = new TableColumn<>("Class Name");
        classCol.setCellValueFactory(cell -> cell.getValue().classNameProperty());
        TableColumn<SummaryItem, String> principleCol = new TableColumn<>("Principle");
        principleCol.setCellValueFactory(cell -> cell.getValue().principleProperty());
        TableColumn<SummaryItem, String> usageCol = new TableColumn<>("Location/Usage");
        usageCol.setCellValueFactory(cell -> cell.getValue().usageProperty());

        table.getColumns().addAll(classCol, principleCol, usageCol);
        return table;
    }
}
