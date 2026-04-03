package org.example.client;

import org.example.shared.Message;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TicTacToeClient {
    private static final String HOST = System.getProperty("tictactoe.server.host", "localhost");
    private static final int PORT = Integer.getInteger("tictactoe.server.port", 5000);

    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
    private final AppSession session = new AppSession();

    private ClientConnection connection;
    private LoginFrame loginFrame;
    private GameFrame gameFrame;

    public void start() {
        try {
            connection = new ClientConnection(HOST, PORT);
            loginFrame = new LoginFrame(this);
            loginFrame.setVisible(true);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(
                    null,
                    "Не удалось подключиться к серверу: " + exception.getMessage(),
                    "Ошибка подключения",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void login(String username, String password, LoginFrame frame) {
        submit(frame, () -> {
            Message response = connection.request(Message.loginRequest(username, password));
            if (!"ok".equals(response.status)) {
                showError(frame, response.message);
                return;
            }
            session.setUsername(response.stats.username);
            session.setStats(response.stats);
            SwingUtilities.invokeLater(() -> openGameFrame(response.message));
        });
    }

    public void register(String username, String password, LoginFrame frame) {
        submit(frame, () -> {
            Message response = connection.request(Message.registerRequest(username, password));
            if (!"ok".equals(response.status)) {
                showError(frame, response.message);
                return;
            }
            session.setUsername(response.stats.username);
            session.setStats(response.stats);
            SwingUtilities.invokeLater(() -> openGameFrame(response.message));
        });
    }

    public void startGame(int size, GameFrame frame) {
        submit(frame, () -> {
            Message response = connection.request(Message.startRequest(size));
            handleGameResponse(frame, response);
        });
    }

    public void makeMove(int row, int col, GameFrame frame) {
        submit(frame, () -> {
            Message response = connection.request(Message.moveRequest(row, col));
            handleGameResponse(frame, response);
        });
    }

    public void openLeaderboard(GameFrame frame) {
        submit(frame, () -> {
            Message response = connection.request(Message.leaderboardRequest());
            if (!"ok".equals(response.status)) {
                showError(frame, response.message);
                return;
            }
            SwingUtilities.invokeLater(() -> new LeaderboardDialog(frame, response.leaderboard).setVisible(true));
        });
    }

    public void logout(GameFrame frame) {
        submit(frame, () -> {
            Message response = connection.request(Message.logoutRequest());
            if (!"ok".equals(response.status)) {
                showError(frame, response.message);
                return;
            }
            session.clear();
            SwingUtilities.invokeLater(() -> {
                if (gameFrame != null) {
                    gameFrame.dispose();
                }
                loginFrame = new LoginFrame(this);
                loginFrame.setVisible(true);
            });
        });
    }

    private void openGameFrame(String infoMessage) {
        if (loginFrame != null) {
            loginFrame.dispose();
        }
        gameFrame = new GameFrame(this);
        gameFrame.setUser(session.username());
        gameFrame.updateStats(session.stats());
        gameFrame.setStatus(infoMessage);
        gameFrame.setVisible(true);
    }

    private void handleGameResponse(GameFrame frame, Message response) {
        if ("error".equals(response.status)) {
            showError(frame, response.message);
            return;
        }

        session.setBoard(response.board);
        session.setStats(response.stats);
        SwingUtilities.invokeLater(() -> {
            frame.updateBoard(response.board);
            frame.updateStats(response.stats);
            frame.setStatus(response.message);
            if ("finished".equals(response.status)) {
                frame.showResult(buildResultText(response));
            }
        });
    }

    private String buildResultText(Message response) {
        String outcome = switch (response.result) {
            case "victory" -> "Победа игрока";
            case "defeat" -> "Победа компьютера";
            default -> "Ничья";
        };
        return String.format(
                "%s\nМаксимальная цепочка игрока: %d\nМаксимальная цепочка компьютера: %d",
                outcome,
                response.playerMaxChain,
                response.computerMaxChain
        );
    }

    private void submit(JFrame frame, NetworkTask task) {
        networkExecutor.submit(() -> {
            try {
                task.run();
            } catch (IOException exception) {
                showError(frame, "Проблема сети: " + exception.getMessage());
            } catch (Exception exception) {
                showError(frame, exception.getMessage());
            }
        });
    }

    private void showError(JFrame frame, String text) {
        SwingUtilities.invokeLater(() -> {
            if (frame instanceof LoginFrame login) {
                login.showError(text);
            } else if (frame instanceof GameFrame game) {
                game.showError(text);
            } else {
                JOptionPane.showMessageDialog(frame, text, "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @FunctionalInterface
    private interface NetworkTask {
        void run() throws Exception;
    }
}
