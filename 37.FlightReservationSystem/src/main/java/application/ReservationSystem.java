package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

public class ReservationSystem {
    private List<Flight> flights = new ArrayList<>();
    private List<Passenger> passengers = new ArrayList<>();
    
    
    private static final double ECONOMY_PRICE = 10000.0;
    private static final double BUSINESS_PRICE = 15000.0;
    private static final double FIRST_PRICE = 20000.0;

    public ReservationSystem() {
        
        flights.add(new Flight("PKIK1", "Islamabad", "Karachi", "09:00 AM", 50));
        flights.add(new Flight("PKQL1", "Quetta", "Lahore", "12:00 PM", 30));
        flights.add(new Flight("PKLP1", "Lahore", "Peshawar", "3:00 PM", 10));
    }

    
    
    public List<Flight> getFlights() { return flights; }
    public List<Passenger> getPassengers() { return passengers; }

    public boolean bookTicket(String name, String flightNumber, String travelClass) {
        Flight flight = findFlight(flightNumber);
        if (flight == null || flight.getAvailableSeats() <= 0) {
            JOptionPane.showMessageDialog(null, "No seats available or flight not found.", "Booking Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String pnr = generatePNR();
        passengers.add(new Passenger(name, pnr, flightNumber, travelClass));
        flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        double price = getPrice(travelClass);
        JOptionPane.showMessageDialog(null, "Booking successful! PNR: " + pnr + "\nPrice: PKR " + price, "Success", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    
    
    
    public void cancelReservation(String pnr, String reason) {
        Iterator<Passenger> iterator = passengers.iterator();
        while (iterator.hasNext()) {
            Passenger p = iterator.next();
            if (p.getPnr().equals(pnr)) {
                Flight flight = findFlight(p.getFlightNumber());
                if (flight != null) {
                    flight.setAvailableSeats(flight.getAvailableSeats() + 1);
                }
                double refund = calculateRefund(p.getTravelClass(), reason);
                iterator.remove();
                JOptionPane.showMessageDialog(null, "Reservation canceled.\nRefund: PKR " + refund, "Cancellation", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "PNR not found.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    
    

    public void updateDelay(String flightNumber, boolean delayed, String reason) {
        Flight flight = findFlight(flightNumber);
        if (flight != null) {
            flight.setDelayed(delayed);
            flight.setDelayReason(reason);
            JOptionPane.showMessageDialog(null, "Flight delay status updated.", "Update", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Flight not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    
    
    public boolean checkReservation(String name, String pnr) {
        for (Passenger p : passengers) {
            if (p.getName().equalsIgnoreCase(name) && p.getPnr().equals(pnr)) {
                return true;
            }
        }
        return false;
    }

    
    
    
    private Flight findFlight(String flightNumber) {
        for (Flight f : flights) {
            if (f.getFlightNumber().equals(flightNumber)) {
                return f;
            }
        }
        return null;
    }

    
    
    private String generatePNR() {
        return "PNR" + (int)(Math.random() * 100000);
    }
    
    

    private double calculateRefund(String travelClass, String reason) {
        double price = getPrice(travelClass);
        if (reason.equalsIgnoreCase("Medical") || reason.equalsIgnoreCase("Emergency")) {
            return price * 0.8; 
        } else {
            return price * 0.5; 
        }
    }

    
    public double getPrice(String travelClass) {
        switch (travelClass) {
            case "Economy":
                return ECONOMY_PRICE;
            case "Business":
                return BUSINESS_PRICE;
            case "First":
                return FIRST_PRICE;
            default:
                return 0.0;
        }
    }
}