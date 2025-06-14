package banking;

import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private int accountId;
    private String type;
    private double amount;
    private String description;
    private LocalDateTime timestamp;

    public Transaction(int id, int accountId, String type, double amount, String description, LocalDateTime timestamp) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}