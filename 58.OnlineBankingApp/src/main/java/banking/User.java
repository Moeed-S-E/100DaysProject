package banking;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private int accountId;

    public User(int id, String username, String passwordHash, int accountId) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.accountId = accountId;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public int getAccountId() { return accountId; }
}
