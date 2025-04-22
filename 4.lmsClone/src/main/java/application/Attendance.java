package application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Attendance {
    // Nested map: Date -> (Student ID -> Attendance Status)
    private Map<LocalDate, Map<String, Boolean>> attendanceMap = new HashMap<>();

    public void markAttendance(String studentId, LocalDate date, boolean present) {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot mark attendance for future dates.");
        }

        attendanceMap.putIfAbsent(date, new HashMap<>());
        attendanceMap.get(date).put(studentId, present);
    }

    public Map<LocalDate, Map<String, Boolean>> getAttendance() {
        return attendanceMap;
    }

    public Map<String, Boolean> getAttendanceForDate(LocalDate date) {
        return attendanceMap.getOrDefault(date, new HashMap<>());
    }

    // Save attendance to CSV
    public void saveToCSV(DataBaseManager dbManager) {
        for (Map.Entry<LocalDate, Map<String, Boolean>> entry : attendanceMap.entrySet()) {
            LocalDate date = entry.getKey();
            Map<String, Boolean> records = entry.getValue();
            for (Map.Entry<String, Boolean> record : records.entrySet()) {
                String csvLine = date.toString() + "," + record.getKey() + "," + record.getValue();
                dbManager.saveRecord(csvLine);
            }
        }
    }

    // Load attendance from CSV
    public void loadFromCSV(DataBaseManager dbManager) {
        attendanceMap.clear();
        ArrayList<String> records = (ArrayList<String>) dbManager.readRecords();
        for (String record : records) {
            String[] data = record.split(",");
            if (data.length == 3) {
                try {
                    LocalDate date = LocalDate.parse(data[0]);
                    String studentId = data[1];
                    boolean present = Boolean.parseBoolean(data[2]);
                    markAttendance(studentId, date, present);
                } catch (Exception e) {
                    System.out.println("Error parsing attendance record: " + record);
                }
            }
        }
    }
}