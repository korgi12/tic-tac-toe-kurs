package org.example.client;

import org.example.shared.UserStats;

import java.util.List;

public class AppSession {
    private String username;
    private UserStats stats;
    private List<String> board;

    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserStats stats() {
        return stats;
    }

    public void setStats(UserStats stats) {
        this.stats = stats;
    }

    public List<String> board() {
        return board;
    }

    public void setBoard(List<String> board) {
        this.board = board;
    }

    public void clear() {
        username = null;
        stats = null;
        board = null;
    }
}
