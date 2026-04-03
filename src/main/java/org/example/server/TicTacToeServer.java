package org.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TicTacToeServer {
    private static final int DEFAULT_PORT = 5000;

    private final int port;
    private final ExecutorService clientPool = Executors.newCachedThreadPool();
    private final AuthService authService;

    public TicTacToeServer() throws Exception {
        this.port = Integer.getInteger("tictactoe.server.port", DEFAULT_PORT);
        DatabaseConfig databaseConfig = new DatabaseConfig();
        new DatabaseInitializer(databaseConfig).initialize();
        this.authService = new AuthService(new UserRepository(databaseConfig));
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту " + port + ".");
            System.out.println("Подключение к PostgreSQL настроено. Ожидание клиентов...");
            while (true) {
                Socket socket = serverSocket.accept();
                clientPool.submit(new ClientHandler(socket, authService));
            }
        } finally {
            clientPool.shutdown();
        }
    }
}
