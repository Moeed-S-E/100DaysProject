package main.model;

public class StringVisualizer {
    private final String value;

    public StringVisualizer(String value) {
        this.value = value;
    }

    public String visualize() {
        return String.format("""
                String Visualization:
                Value: "%s"
                - Immutable
                - Stored in String Pool
                """, value);
    }
}
