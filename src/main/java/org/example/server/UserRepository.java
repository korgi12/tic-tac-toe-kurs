package org.example.server;

import org.example.shared.LeaderboardEntry;
import org.example.shared.UserStats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private final DatabaseConfig databaseConfig;

    public UserRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public Optional<UserAccount> findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password FROM users WHERE username = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new UserAccount(
                            resultSet.getLong("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password")
                    ));
                }
                return Optional.empty();
            }
        }
    }

    public void createUser(String username, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.executeUpdate();
        }
    }

    public UserStats getStats(String username) throws SQLException {
        String sql = """
                SELECT username, wins, losses, draws, games_played, rating
                FROM users
                WHERE username = ?
                """;
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Пользователь не найден: " + username);
                }
                return new UserStats(
                        resultSet.getString("username"),
                        resultSet.getInt("wins"),
                        resultSet.getInt("losses"),
                        resultSet.getInt("draws"),
                        resultSet.getInt("games_played"),
                        resultSet.getInt("rating")
                );
            }
        }
    }

    public List<LeaderboardEntry> getLeaderboard(int limit) throws SQLException {
        String sql = """
                SELECT username, rating, wins, losses, draws, games_played
                FROM users
                ORDER BY rating DESC, wins DESC, username ASC
                LIMIT ?
                """;
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    leaderboard.add(new LeaderboardEntry(
                            resultSet.getString("username"),
                            resultSet.getInt("rating"),
                            resultSet.getInt("wins"),
                            resultSet.getInt("losses"),
                            resultSet.getInt("draws"),
                            resultSet.getInt("games_played")
                    ));
                }
            }
        }
        return leaderboard;
    }

    public void applyGameResult(String username, String result) throws SQLException {
        String sql = """
                UPDATE users
                SET wins = wins + ?,
                    losses = losses + ?,
                    draws = draws + ?,
                    games_played = games_played + 1,
                    rating = rating + ?
                WHERE username = ?
                """;

        int winDelta = "victory".equals(result) ? 1 : 0;
        int lossDelta = "defeat".equals(result) ? 1 : 0;
        int drawDelta = "draw".equals(result) ? 1 : 0;
        int ratingDelta = switch (result) {
            case "victory" -> 3;
            case "draw" -> 1;
            default -> 0;
        };

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, winDelta);
            statement.setInt(2, lossDelta);
            statement.setInt(3, drawDelta);
            statement.setInt(4, ratingDelta);
            statement.setString(5, username);
            statement.executeUpdate();
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(
                databaseConfig.url(),
                databaseConfig.user(),
                databaseConfig.password()
        );
    }
}
