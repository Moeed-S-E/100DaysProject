package application;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {
    private final String csvFilePath;

    // Constructor: Takes the path to the CSV file
    public DataBaseManager(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        // Ensure the file exists, create it if it doesn't
        try {
            File file = new File(csvFilePath);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to save a record (append to CSV)
    public void saveRecord(String record) {
        try (FileWriter fw = new FileWriter(csvFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(record);
            bw.newLine(); // Add a new line after the record
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to read all previous records from CSV
    public List<String> readRecords() {
        List<String> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) { // Skip empty lines
                    records.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}