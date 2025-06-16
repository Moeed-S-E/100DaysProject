package main.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AnalysisResult {
    private ObservableList<SummaryItem> summaryItems;
    // Add other fields for UML, Swing components, etc.

    public AnalysisResult() {
        summaryItems = FXCollections.observableArrayList();
    }

    public ObservableList<SummaryItem> getSummaryItems() {
        return summaryItems;
    }
    // Add getters/setters for other fields
}
