package main.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SummaryItem {
    private final StringProperty className;
    private final StringProperty principle;
    private final StringProperty usage;

    public SummaryItem(String className, String principle, String usage) {
        this.className = new SimpleStringProperty(className);
        this.principle = new SimpleStringProperty(principle);
        this.usage = new SimpleStringProperty(usage);
    }

    public StringProperty classNameProperty() { return className; }
    public StringProperty principleProperty() { return principle; }
    public StringProperty usageProperty() { return usage; }
}
