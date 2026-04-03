package org.example.server;

import org.example.shared.LeaderboardEntry;
import org.example.shared.UserStats;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserStats register(String username, String password) throws SQLException {
        validateCredentials(username, password);
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует.");
        }

        userRepository.createUser(username, password);
        return userRepository.getStats(username);
    }

    public UserStats login(String username, String password) throws SQLException {
        validateCredentials(username, password);
        Optional<UserAccount> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Пользователь не найден.");
        }

        UserAccount user = optionalUser.get();
        if (password.equals(user.passwordHash())) {
            throw new IllegalArgumentException("Неверный пароль.");
        }

        return userRepository.getStats(username);
    }

    public UserStats getStats(String username) throws SQLException {
        return userRepository.getStats(username);
    }

    public void recordGameResult(String username, String result) throws SQLException {
        userRepository.applyGameResult(username, result);
    }

    public List<LeaderboardEntry> getLeaderboard(int limit) throws SQLException {
        return userRepository.getLeaderboard(limit);
    }

    private void validateCredentials(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Логин не может быть пустым.");
        }
        if (username.length() < 3 || username.length() > 30) {
            throw new IllegalArgumentException("Логин должен содержать от 3 до 30 символов.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Пароль не может быть пустым.");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 4 символа.");
        }
    }
}
