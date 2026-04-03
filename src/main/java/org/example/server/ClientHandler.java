package org.example.server;

import org.example.shared.Message;
import org.example.shared.SocketJsonIO;
import org.example.shared.UserStats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final AuthService authService;

    private BufferedReader reader;
    private BufferedWriter writer;
    private String currentUsername;
    private GameSession currentSession;

    public ClientHandler(Socket socket, AuthService authService) {
        this.socket = socket;
        this.authService = authService;
    }

    @Override
    public void run() {
        try (socket) {
            reader = SocketJsonIO.createReader(socket.getInputStream());
            writer = SocketJsonIO.createWriter(socket.getOutputStream());

            while (true) {
                Message request = SocketJsonIO.readMessage(reader);
                if (request == null) {
                    return;
                }
                handleRequest(request);
            }
        } catch (IOException exception) {
            System.out.println("Клиент отключился: " + socket.getRemoteSocketAddress());
        }
    }

    private void handleRequest(Message request) throws IOException {
        if (request.type == null) {
            send(Message.response("error", "error", "Некорректный JSON-запрос."));
            return;
        }

        try {
            switch (request.type) {
                case "register" -> handleRegister(request);
                case "login" -> handleLogin(request);
                case "start" -> handleStart(request);
                case "move" -> handleMove(request);
                case "leaderboard" -> handleLeaderboard();
                case "logout" -> handleLogout();
                default -> send(Message.response(request.type, "error", "Неизвестная команда."));
            }
        } catch (IllegalArgumentException exception) {
            send(Message.response(request.type, "error", exception.getMessage()));
        } catch (SQLException exception) {
            send(Message.response(request.type, "error", "Ошибка работы с базой данных: " + exception.getMessage()));
        }
    }

    private void handleRegister(Message request) throws SQLException, IOException {
        UserStats stats = authService.register(request.username, request.password);
        currentUsername = stats.username;
        currentSession = null;
        send(Message.authResponse("register", "ok", "Регистрация выполнена успешно.", stats));
    }

    private void handleLogin(Message request) throws SQLException, IOException {
        UserStats stats = authService.login(request.username, request.password);
        currentUsername = stats.username;
        currentSession = null;
        send(Message.authResponse("login", "ok", "Авторизация выполнена успешно.", stats));
    }

    private void handleStart(Message request) throws IOException, SQLException {
        requireAuthorization();
        if (request.size == null) {
            throw new IllegalArgumentException("Не указан размер поля.");
        }

        currentSession = new GameSession(request.size);
        send(Message.gameResponse(
                "ok",
                "Новая игра началась.",
                currentSession.boardAsLines(),
                null,
                null,
                null,
                authService.getStats(currentUsername)
        ));
    }

    private void handleMove(Message request) throws IOException, SQLException {
        requireAuthorization();
        requireActiveSession();
        if (request.row == null || request.col == null) {
            throw new IllegalArgumentException("Не указаны координаты хода.");
        }

        MoveResult result = currentSession.playerMove(request.row, request.col);
        UserStats stats = authService.getStats(currentUsername);
        if ("finished".equals(result.status())) {
            authService.recordGameResult(currentUsername, result.result());
            stats = authService.getStats(currentUsername);
        }

        send(Message.gameResponse(
                result.status(),
                result.message(),
                result.board(),
                result.playerMaxChain(),
                result.computerMaxChain(),
                result.result(),
                stats
        ));
    }

    private void handleLeaderboard() throws SQLException, IOException {
        requireAuthorization();
        send(Message.leaderboardResponse(authService.getLeaderboard(20)));
    }

    private void handleLogout() throws IOException {
        currentUsername = null;
        currentSession = null;
        send(Message.response("logout", "ok", "Вы вышли из системы."));
    }

    private void requireAuthorization() {
        if (currentUsername == null) {
            throw new IllegalArgumentException("Сначала выполните авторизацию.");
        }
    }

    private void requireActiveSession() {
        if (currentSession == null) {
            throw new IllegalArgumentException("Сначала начните новую игру.");
        }
    }

    private void send(Message message) throws IOException {
        SocketJsonIO.writeMessage(writer, message);
    }
}
