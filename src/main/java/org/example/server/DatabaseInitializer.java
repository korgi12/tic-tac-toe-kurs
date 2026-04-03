package org.example.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private final DatabaseConfig databaseConfig;

    public DatabaseInitializer(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public void initialize() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                databaseConfig.url(),
                databaseConfig.user(),
                databaseConfig.password()
        ); Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id BIGSERIAL PRIMARY KEY,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        wins INTEGER NOT NULL DEFAULT 0,
                        losses INTEGER NOT NULL DEFAULT 0,
                        draws INTEGER NOT NULL DEFAULT 0,
                        games_played INTEGER NOT NULL DEFAULT 0,
                        rating INTEGER NOT NULL DEFAULT 0,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
            statement.executeUpdate("""
                    CREATE INDEX IF NOT EXISTS idx_users_rating
                    ON users (rating DESC, wins DESC, username ASC)
                    """);
        }
    }
}
