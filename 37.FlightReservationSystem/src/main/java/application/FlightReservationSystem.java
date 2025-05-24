package application;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class FlightReservationSystem extends JFrame {
    private ReservationSystem system = new ReservationSystem();
    private JTable flightTable;
    private JTable passTable;

    public FlightReservationSystem() {
    	
    	
        setTitle("Flight Reservation System");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 16));

        
        
        
        
        
        JPanel flightPanel = new JPanel(new BorderLayout(10, 10));
        flightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        flightPanel.setBackground(new Color(255, 255, 255));
        flightTable = new JTable(buildFlightTableModel(system.getFlights()));
        flightTable.setRowHeight(25);
        flightTable.setFont(new Font("Arial", Font.PLAIN, 14));
        flightTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        flightPanel.add(new JScrollPane(flightTable), BorderLayout.CENTER);
        tabbedPane.addTab("Flight Schedule", flightPanel);

        
        
        
        
        
        
        
        
        
        JPanel bookPanel = new JPanel(new GridBagLayout());
        bookPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bookPanel.setBackground(new Color(255, 255, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField flightNumField = new JTextField(20);
        JComboBox<String> classBox = new JComboBox<>(new String[]{"Economy", "Business", "First"});
        JButton bookBtn = new JButton("Book Ticket");
        styleTextField(nameField);
        styleTextField(flightNumField);
        classBox.setFont(new Font("Arial", Font.PLAIN, 14));
        styleButton(bookBtn, new Color(0, 128, 255));

        bookBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String flight = flightNumField.getText().trim();
            String travelClass = (String) classBox.getSelectedItem();
            if (!name.isEmpty() && !flight.isEmpty()) {
                boolean success = system.bookTicket(name, flight, travelClass);
                if (success) {
                    flightTable.setModel(buildFlightTableModel(system.getFlights()));
                    passTable.setModel(buildPassengerTableModel(system.getPassengers()));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        bookPanel.add(new JLabel("Passenger Name:"), gbc);
        gbc.gridx = 1;
        bookPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        bookPanel.add(new JLabel("Flight Number:"), gbc);
        gbc.gridx = 1;
        bookPanel.add(flightNumField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        bookPanel.add(new JLabel("Travel Class:"), gbc);
        gbc.gridx = 1;
        bookPanel.add(classBox, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        bookPanel.add(bookBtn, gbc);
        tabbedPane.addTab("Book Ticket", bookPanel);

        
        
        
        
        
        
        JPanel cancelPanel = new JPanel(new GridBagLayout());
        cancelPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cancelPanel.setBackground(new Color(255, 255, 255));
        JTextField pnrField = new JTextField(20);
        JTextField reasonField = new JTextField(20);
        JButton cancelBtn = new JButton("Cancel Reservation");
        styleTextField(pnrField);
        styleTextField(reasonField);
        styleButton(cancelBtn, new Color(255, 69, 58));

        cancelBtn.addActionListener(e -> {
            String pnr = pnrField.getText().trim();
            String reason = reasonField.getText().trim();
            if (!pnr.isEmpty()) {
                system.cancelReservation(pnr, reason);
                flightTable.setModel(buildFlightTableModel(system.getFlights()));
                passTable.setModel(buildPassengerTableModel(system.getPassengers())); 
            } else {
                JOptionPane.showMessageDialog(this, "Enter valid PNR.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        cancelPanel.add(new JLabel("PNR Number:"), gbc);
        gbc.gridx = 1;
        cancelPanel.add(pnrField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        cancelPanel.add(new JLabel("Reason (Optional):"), gbc);
        gbc.gridx = 1;
        cancelPanel.add(reasonField, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        cancelPanel.add(cancelBtn, gbc);
        tabbedPane.addTab("Cancel Booking", cancelPanel);

        
        
        
        
        
        
        
        
        
        
        
        JPanel passPanel = new JPanel(new BorderLayout(10, 10));
        passPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        passPanel.setBackground(new Color(255, 255, 255));
        passTable = new JTable(buildPassengerTableModel(system.getPassengers()));
        passTable.setRowHeight(25);
        passTable.setFont(new Font("Arial", Font.PLAIN, 14));
        passTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        passPanel.add(new JScrollPane(passTable), BorderLayout.CENTER);
        tabbedPane.addTab("Passengers", passPanel);


        
        
        
        
        
        
        JPanel delayPanel = new JPanel(new GridBagLayout());
        delayPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        delayPanel.setBackground(new Color(255, 255, 255));
        JTextField delayFlightField = new JTextField(20);
        JTextField delayReasonField = new JTextField(20);
        JCheckBox delayCheck = new JCheckBox("Is Delayed?");
        JButton delayBtn = new JButton("Update Delay");
        styleTextField(delayFlightField);
        styleTextField(delayReasonField);
        delayCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        styleButton(delayBtn, new Color(0, 128, 255));

        delayBtn.addActionListener(e -> {
            String flight = delayFlightField.getText().trim();
            boolean isDelayed = delayCheck.isSelected();
            String reason = delayReasonField.getText().trim();
            system.updateDelay(flight, isDelayed, reason);
            flightTable.setModel(buildFlightTableModel(system.getFlights()));
        });

        gbc.gridx = 0; gbc.gridy = 0;
        delayPanel.add(new JLabel("Flight Number:"), gbc);
        gbc.gridx = 1;
        delayPanel.add(delayFlightField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        delayPanel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1;
        delayPanel.add(delayReasonField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        delayPanel.add(delayCheck, gbc);
        gbc.gridx = 1;
        delayPanel.add(delayBtn, gbc);
        tabbedPane.addTab("Delay Info", delayPanel);

        add(tabbedPane);
        setVisible(true);
    }

    
    
    
    
    
    
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    
    
    
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
    }

    
    
    
    private DefaultTableModel buildFlightTableModel(List<Flight> flights) {
        String[] columnNames = {"Flight No.", "From", "To", "Departure", "Seats", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Flight f : flights) {
            Object[] row = {
                f.getFlightNumber(),
                f.getDeparture(),
                f.getDestination(),
                f.getDepartureTime(),
                f.getAvailableSeats(),
                f.isDelayed() ? "Delayed: " + f.getDelayReason() : "On Time"
            };
            model.addRow(row);
        }
        return model;
    }

    
    
    
    private DefaultTableModel buildPassengerTableModel(List<Passenger> passengers) {
        String[] columnNames = {"Name", "PNR", "Flight", "Class"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Passenger p : passengers) {
            Object[] row = {
                p.getName(),
                p.getPnr(),
                p.getFlightNumber(),
                p.getTravelClass()
            };
            model.addRow(row);
        }
        return model;
    }

    
    public static void main(String[] args) {
        new FlightReservationSystem();
    }
}