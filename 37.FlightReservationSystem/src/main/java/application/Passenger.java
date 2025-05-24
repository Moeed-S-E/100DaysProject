package application;

public class Passenger {
    private String name;
    private String pnr;
    private String flightNumber;
    private String travelClass;

    public Passenger(String name, String pnr, String flightNumber, String travelClass) {
        this.name = name;
        this.pnr = pnr;
        this.flightNumber = flightNumber;
        this.travelClass = travelClass;
    }

    public String getName() { return name; }
    public String getPnr() { return pnr; }
    public String getFlightNumber() { return flightNumber; }
    public String getTravelClass() { return travelClass; }
}