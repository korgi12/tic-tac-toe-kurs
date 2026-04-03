package org.example.shared;

import java.util.List;

public class Message {
    public String type;
    public String status;
    public String message;
    public String username;
    public String password;
    public Integer size;
    public Integer row;
    public Integer col;
    public List<String> board;
    public Integer playerMaxChain;
    public Integer computerMaxChain;
    public String result;
    public UserStats stats;
    public List<LeaderboardEntry> leaderboard;

    public Message() {
    }

    public static Message loginRequest(String username, String password) {
        Message message = new Message();
        message.type = "login";
        message.username = username;
        message.password = password;
        return message;
    }

    public static Message registerRequest(String username, String password) {
        Message message = new Message();
        message.type = "register";
        message.username = username;
        message.password = password;
        return message;
    }

    public static Message startRequest(int size) {
        Message message = new Message();
        message.type = "start";
        message.size = size;
        return message;
    }

    public static Message moveRequest(int row, int col) {
        Message message = new Message();
        message.type = "move";
        message.row = row;
        message.col = col;
        return message;
    }

    public static Message leaderboardRequest() {
        Message message = new Message();
        message.type = "leaderboard";
        return message;
    }

    public static Message logoutRequest() {
        Message message = new Message();
        message.type = "logout";
        return message;
    }

    public static Message response(String type, String status, String text) {
        Message message = new Message();
        message.type = type;
        message.status = status;
        message.message = text;
        return message;
    }

    public static Message authResponse(String type, String status, String text, UserStats stats) {
        Message message = response(type, status, text);
        message.stats = stats;
        return message;
    }

    public static Message gameResponse(
            String status,
            String text,
            List<String> board,
            Integer playerMaxChain,
            Integer computerMaxChain,
            String result,
            UserStats stats
    ) {
        Message message = response("game", status, text);
        message.board = board;
        message.playerMaxChain = playerMaxChain;
        message.computerMaxChain = computerMaxChain;
        message.result = result;
        message.stats = stats;
        return message;
    }

    public static Message leaderboardResponse(List<LeaderboardEntry> leaderboard) {
        Message message = response("leaderboard", "ok", "Рейтинг загружен.");
        message.leaderboard = leaderboard;
        return message;
    }
}
