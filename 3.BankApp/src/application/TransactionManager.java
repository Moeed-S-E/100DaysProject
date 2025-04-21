package application;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private static TransactionManager instance = null;
    private final List<Transaction> transactions;
    private final String csvFilePath = System.getProperty("user.home") + File.separator + "transactions.csv";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private TransactionManager() {
        transactions = new ArrayList<>();
        File file = new File(csvFilePath);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("Timestamp,Amount,BalanceAfter");
            } catch (IOException e) {
                System.err.println("Failed to initialize CSV: " + e.getMessage());
            }
        }
    }

    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    public void addTransaction(double amount, double balanceAfter) {
        LocalDateTime timestamp = LocalDateTime.now();
        Transaction transaction = new Transaction(timestamp, amount, balanceAfter);
        transactions.add(transaction);
        saveToCsv(transaction);
        System.out.println("Transaction added: " + transaction);
    }

    private void saveToCsv(Transaction transaction) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath, true))) {
            String line = String.format("%s,%.2f,%.2f",
                    transaction.timestamp.format(formatter),
                    transaction.amount,
                    transaction.balanceAfter);
            writer.println(line);
        } catch (IOException e) {
            System.err.println("Failed to save transaction to CSV: " + e.getMessage());
        }
    }

    public File getCsvFile() {
        File file = new File(csvFilePath);
        if (!file.exists()) {
            System.err.println("CSV file does not exist.");
            return null;
        }
        return file;
    }

    public void printTransactions() {
        System.out.println("Transaction History:");
        System.out.println("Timestamp,Amount,BalanceAfter");
        for (Transaction transaction : transactions) {
            System.out.printf("%s,%.2f,%.2f%n",
                    transaction.timestamp.format(formatter),
                    transaction.amount,
                    transaction.balanceAfter);
        }
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded.");
        }
    }

    private static class Transaction {
        private final LocalDateTime timestamp;
        private final double amount;
        private final double balanceAfter;

        public Transaction(LocalDateTime timestamp, double amount, double balanceAfter) {
            this.timestamp = timestamp;
            this.amount = amount;
            this.balanceAfter = balanceAfter;
        }

        @Override
        public String toString() {
            return "Transaction{timestamp=" + timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                   ", amount=" + amount +
                   ", balanceAfter=" + balanceAfter + "}";
        }
    }
}