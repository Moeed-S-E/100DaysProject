package main.model;

public class DataType {
    private final String name;
    private final int size;

    public DataType(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }
}
