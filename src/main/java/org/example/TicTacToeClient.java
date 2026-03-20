package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class TicTacToeClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public void start() throws IOException {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {
            System.out.println("Подключено к серверу " + HOST + ':' + PORT);
            boolean playAgain = true;
            while (playAgain) {
                int size = requestBoardSize(scanner);
                writer.println(JsonCodec.toJson(Message.initRequest(size)));
                Message initialState = readMessage(reader);
                if (initialState == null) {
                    return;
                }
                System.out.println(initialState.getMessage());
                printBoard(initialState.getBoard());
                playAgain = playGameLoop(scanner, reader, writer);
            }
        }
    }

    private boolean playGameLoop(Scanner scanner, BufferedReader reader, PrintWriter writer) throws IOException {
        while (true) {
            int[] move = requestMove(scanner);
            writer.println(JsonCodec.toJson(Message.moveRequest(move[0], move[1])));
            Message response = readMessage(reader);
            if (response == null) {
                return false;
            }

            System.out.println(response.getMessage());
            printBoard(response.getBoard());

            if ("error".equals(response.getStatus())) {
                continue;
            }

            if ("finished".equals(response.getStatus())) {
                printGameResult(response);
                boolean playAgain = askPlayAgain(scanner);
                writer.println(JsonCodec.toJson(Message.playAgainRequest(playAgain)));
                Message restartAck = readMessage(reader);
                if (restartAck != null && restartAck.getMessage() != null) {
                    System.out.println(restartAck.getMessage());
                }
                return playAgain;
            }
        }
    }

    private int requestBoardSize(Scanner scanner) {
        while (true) {
            System.out.print("Введите размер поля (3-10): ");
            String input = scanner.nextLine().trim();
            try {
                int size = Integer.parseInt(input);
                if (size >= 3 && size <= 10) {
                    return size;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Некорректный размер поля. Введите число от 3 до 10.");
        }
    }

    private int[] requestMove(Scanner scanner) {
        while (true) {
            System.out.print("Введите ход (строка столбец), нумерация с 1: ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+");
            if (parts.length != 2) {
                System.out.println("Нужно ввести два числа: строку и столбец.");
                continue;
            }
            try {
                int row = Integer.parseInt(parts[0]) - 1;
                int col = Integer.parseInt(parts[1]) - 1;
                return new int[]{row, col};
            } catch (NumberFormatException exception) {
                System.out.println("Координаты должны быть целыми числами.");
            }
        }
    }

    private boolean askPlayAgain(Scanner scanner) {
        while (true) {
            System.out.print("Начать новую игру? (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if ("y".equals(input) || "yes".equals(input)) {
                return true;
            }
            if ("n".equals(input) || "no".equals(input)) {
                return false;
            }
            System.out.println("Введите y или n.");
        }
    }

    private Message readMessage(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            System.out.println("Сервер закрыл соединение.");
            return null;
        }
        return JsonCodec.fromJson(line);
    }

    private void printBoard(List<String> board) {
        if (board == null || board.isEmpty()) {
            return;
        }
        System.out.print("  ");
        for (int col = 0; col < board.size(); col++) {
            System.out.print((col + 1) + " ");
        }
        System.out.println();
        for (int row = 0; row < board.size(); row++) {
            System.out.print((row + 1) + " ");
            for (char cell : board.get(row).toCharArray()) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    private void printGameResult(Message response) {
        System.out.println("Максимальная цепочка игрока: " + response.getPlayerMaxChain());
        System.out.println("Максимальная цепочка компьютера: " + response.getComputerMaxChain());
        String result = response.getResult();
        if ("victory".equals(result)) {
            System.out.println("Итог: победа игрока.");
        } else if ("defeat".equals(result)) {
            System.out.println("Итог: победа компьютера.");
        } else {
            System.out.println("Итог: ничья.");
        }
    }
}
