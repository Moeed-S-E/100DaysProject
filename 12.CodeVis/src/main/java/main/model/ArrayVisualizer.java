package main.model;

import java.util.Arrays;

public class ArrayVisualizer {
    private final int[] array;

    public ArrayVisualizer(int[] array) {
        this.array = array;
    }

    public String visualize() {
        StringBuilder visualization = new StringBuilder("Array Visualization:\n");
        visualization.append(Arrays.toString(array)).append("\n");
        visualization.append("Indexes:   ");
        for (int i = 0; i < array.length; i++) {
            visualization.append(i).append("   ");
        }
        return visualization.toString();
    }
}
