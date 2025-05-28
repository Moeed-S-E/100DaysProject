package application.model;

import java.sql.Timestamp;

public class JobFair {
    private int id;
    private String title;
    private Timestamp createdDate;
    private String status;
    
    public JobFair() {}
    
    public JobFair(int id, String title, Timestamp createdDate) {
        this.id = id;
        this.title = title;
        this.createdDate = createdDate;
        this.status = "ACTIVE";
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getFormattedDate() {
        if (createdDate != null) {
            return createdDate.toString().substring(0, 19);
        }
        return "Unknown";
    }
    
    @Override
    public String toString() {
        return title + " (" + getFormattedDate() + ")";
    }
}
