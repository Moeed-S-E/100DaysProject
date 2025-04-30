package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import main.model.ArrayVisualizer;
import main.model.CppMemoryVisualizer;
import main.model.StringVisualizer;

public class VisualizerController {
    @FXML
    private TextArea outputArea;
    @FXML
    private TextArea cppCodeArea;

    public void showDataTypes() {
        outputArea.setText("""
                Primitive Data Types (C++):
                - char: 1 byte
                - bool: 1 byte
                - short: 2 bytes
                - int: 4 bytes
                - unsigned int: 4 bytes
                - float: 4 bytes
                - long: 8 bytes
                - double: 8 bytes
                """);
    }

    public void visualizeArray() {
        int[] array = {10, 20, 30, 40, 50};
        ArrayVisualizer visualizer = new ArrayVisualizer(array);
        outputArea.setText(visualizer.visualize());
    }

    public void visualizeString() {
        StringVisualizer visualizer = new StringVisualizer("Hello");
        outputArea.setText(visualizer.visualize());
    }

    public void simulatePointer() {
        outputArea.setText("""
                Pointer Simulation:
                Java uses reference variables for objects.
                Example: String str = new String("Hello");
                Reference points to object in heap memory.
                """);
    }

    @FXML
    public void visualizeCppMemory() {
        String cppCode = cppCodeArea.getText();
        if (cppCode.isEmpty()) {
            outputArea.setText("Please enter C++ code to visualize.");
            return;
        }
        String visualization = CppMemoryVisualizer.parseCppCode(cppCode);
        outputArea.setText(visualization);
    }
}