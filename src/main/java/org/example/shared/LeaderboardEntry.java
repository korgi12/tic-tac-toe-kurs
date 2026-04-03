package org.example.shared;

public class LeaderboardEntry {
    public String username;
    public int rating;
    public int wins;
    public int losses;
    public int draws;
    public int gamesPlayed;

    public LeaderboardEntry() {
    }

    public LeaderboardEntry(String username, int rating, int wins, int losses, int draws, int gamesPlayed) {
        this.username = username;
        this.rating = rating;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.gamesPlayed = gamesPlayed;
    }
}
