package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TicTacToeServer {
    private static final int PORT = 5000;

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT + ". Ожидание клиента...");
            try (Socket socket = serverSocket.accept();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                System.out.println("Клиент подключен: " + socket.getInetAddress());
                handleClient(reader, writer);
            }
        }
    }

    private void handleClient(BufferedReader reader, PrintWriter writer) throws IOException {
        while (true) {
            GameSession session = createSession(reader, writer);
            if (session == null) {
                return;
            }

            boolean restartRequested = playSession(session, reader, writer);
            if (!restartRequested) {
                return;
            }
        }
    }

    private GameSession createSession(BufferedReader reader, PrintWriter writer) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            return null;
        }

        Message request = JsonCodec.fromJson(requestLine);
        if (request == null || !"init".equals(request.getType()) || request.getSize() == null) {
            writer.println(JsonCodec.toJson(Message.response("error", "Ожидалась команда init с размером поля.", null, null, null, null)));
            return null;
        }

        try {
            GameSession session = new GameSession(request.getSize());
            writer.println(JsonCodec.toJson(Message.response("ok", "Игра создана.", session.boardAsLines(), null, null, null)));
            return session;
        } catch (IllegalArgumentException exception) {
            writer.println(JsonCodec.toJson(Message.response("error", exception.getMessage(), null, null, null, null)));
            return null;
        }
    }

    private boolean playSession(GameSession session, BufferedReader reader, PrintWriter writer) throws IOException {
        while (true) {
            String requestLine = reader.readLine();
            if (requestLine == null) {
                return false;
            }

            Message request = JsonCodec.fromJson(requestLine);
            if (request == null || request.getType() == null) {
                writer.println(JsonCodec.toJson(Message.response("error", "Некорректный JSON-запрос.", session.boardAsLines(), null, null, null)));
                continue;
            }

            if (!"move".equals(request.getType()) || request.getRow() == null || request.getCol() == null) {
                writer.println(JsonCodec.toJson(Message.response("error", "Ожидалась команда move с координатами.", session.boardAsLines(), null, null, null)));
                continue;
            }

            MoveResult result = session.playerMove(request.getRow(), request.getCol());
            writer.println(JsonCodec.toJson(Message.response(
                    result.status(),
                    result.message(),
                    result.board(),
                    result.playerMaxChain(),
                    result.computerMaxChain(),
                    result.result()
            )));

            if ("finished".equals(result.status())) {
                return awaitRestartDecision(reader, writer);
            }
        }
    }

    private boolean awaitRestartDecision(BufferedReader reader, PrintWriter writer) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            return false;
        }

        Message request = JsonCodec.fromJson(requestLine);
        if (request != null && "restart".equals(request.getType()) && "yes".equalsIgnoreCase(request.getMessage())) {
            writer.println(JsonCodec.toJson(Message.response("ok", "Начинаем новую игру.", null, null, null, null)));
            return true;
        }

        writer.println(JsonCodec.toJson(Message.response("ok", "Сессия завершена.", null, null, null, null)));
        return false;
    }
}
