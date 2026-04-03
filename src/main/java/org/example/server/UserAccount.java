package org.example.server;

public class UserAccount {
    private final long id;
    private final String username;
    private final String passwordHash;

    public UserAccount(long id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public long id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String passwordHash() {
        return passwordHash;
    }
}
