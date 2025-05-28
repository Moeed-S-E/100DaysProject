package application.ui.panels;

import application.constants.UIConstants;
import application.ui.UIComponentFactory;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public abstract class BasePanel {
    
    protected VBox createGlassPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(25));
        panel.setStyle(String.format(
            "-fx-background-color: %s;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 8);",
            UIConstants.GLASS_WHITE));
        return panel;
    }
    
    protected Label createSectionTitle(String text) {
        return UIComponentFactory.createSectionTitle(text);
    }
    
    public abstract VBox createPanel();
}
