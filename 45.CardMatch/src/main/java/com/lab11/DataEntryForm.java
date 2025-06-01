package com.lab11;

import java.awt.*;
import javax.swing.*;

public class DataEntryForm extends JFrame {
    private JLabel nameLabel, cnicLabel, genderLabel, addressLabel, cgpaLabel;
    private JTextField nameField, cnicField, addressField, cgpaField;
    private JRadioButton male, female;

    public DataEntryForm() {
        super("Data Entry Form Lab 11 Moeed");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));

        // Top margin and Title
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel titleLabel = new JLabel("Moeed");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel);

        // Name Panel
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        nameLabel = new JLabel("Name:");
        nameLabel.setPreferredSize(new Dimension(60, 25));
        nameField = new JTextField(18);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        // CNIC Panel
        JPanel cnicPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        cnicLabel = new JLabel("CNIC:");
        cnicLabel.setPreferredSize(new Dimension(60, 25));
        cnicField = new JTextField(18);
        cnicPanel.add(cnicLabel);
        cnicPanel.add(cnicField);

        // Gender Panel
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        genderLabel = new JLabel("Gender:");
        genderLabel.setPreferredSize(new Dimension(60, 25));
        male = new JRadioButton("Male");
        female = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);
        genderPanel.add(genderLabel);
        genderPanel.add(male);
        genderPanel.add(female);

        // Address Panel
        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        addressLabel = new JLabel("Address:");
        addressLabel.setPreferredSize(new Dimension(60, 25));
        addressField = new JTextField(18);
        addressPanel.add(addressLabel);
        addressPanel.add(addressField);

        // CGPA Panel
        JPanel cgpaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        cgpaLabel = new JLabel("CGPA:");
        cgpaLabel.setPreferredSize(new Dimension(60, 25));
        cgpaField = new JTextField(18);
        cgpaPanel.add(cgpaLabel);
        cgpaPanel.add(cgpaField);


        mainPanel.add(namePanel);
        mainPanel.add(cnicPanel);
        mainPanel.add(genderPanel);
        mainPanel.add(addressPanel);
        mainPanel.add(cgpaPanel);


        add(mainPanel);

        setSize(400, 350);
        setLocationRelativeTo(null); 
        setVisible(true);
    }

    public static void main(String[] args) {
        new DataEntryForm();
    }
}
