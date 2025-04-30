package main.model;

public class PointerSimulator {
    private final Object reference;

    public PointerSimulator(Object reference) {
        this.reference = reference;
    }

    public String simulate() {
        return String.format("""
                Pointer Simulation:
                Java uses references for objects.
                Reference points to: %s
                """, reference.toString());
    }
}
