package application;

public class Flight {
    private String flightNumber;
    private String departure;
    private String destination;
    private String departureTime;
    private int availableSeats;
    private boolean delayed;
    private String delayReason;

    public Flight(String flightNumber, String departure, String destination, String departureTime, int availableSeats) {
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.delayed = false;
        this.delayReason = "";
    }

    public String getFlightNumber() { return flightNumber; }
    public String getDeparture() { return departure; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public int getAvailableSeats() { return availableSeats; }
    public boolean isDelayed() { return delayed; }
    public String getDelayReason() { return delayReason; }

    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public void setDelayed(boolean delayed) { this.delayed = delayed; }
    public void setDelayReason(String delayReason) { this.delayReason = delayReason; }
}