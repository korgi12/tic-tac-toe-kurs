package org.example.shared;

public class UserStats {
    public String username;
    public int wins;
    public int losses;
    public int draws;
    public int gamesPlayed;
    public int rating;

    public UserStats() {
    }

    public UserStats(String username, int wins, int losses, int draws, int gamesPlayed, int rating) {
        this.username = username;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.gamesPlayed = gamesPlayed;
        this.rating = rating;
    }

    public String toDisplayText() {
        return String.format(
                "Пользователь: %s | Победы: %d | Поражения: %d | Ничьи: %d | Игр: %d | Рейтинг: %d",
                username,
                wins,
                losses,
                draws,
                gamesPlayed,
                rating
        );
    }
}
