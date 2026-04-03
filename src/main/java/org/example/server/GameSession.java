package org.example.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameSession {
    public static final char EMPTY = '.';
    public static final char PLAYER = 'X';
    public static final char COMPUTER = 'O';
    private static final int MIN_SIZE = 3;
    private static final int MAX_SIZE = 10;

    private final int size;
    private final char[][] board;

    public GameSession(int size) {
        if (size < MIN_SIZE || size > MAX_SIZE) {
            throw new IllegalArgumentException("Размер поля должен быть от 3 до 10.");
        }
        this.size = size;
        this.board = new char[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                board[row][col] = EMPTY;
            }
        }
    }

    public MoveResult playerMove(int row, int col) {
        if (!isInside(row, col)) {
            return MoveResult.error("Координаты вне границ поля.", boardAsLines());
        }
        if (board[row][col] != EMPTY) {
            return MoveResult.error("Эта клетка уже занята.", boardAsLines());
        }

        board[row][col] = PLAYER;
        if (isBoardFull()) {
            return finishGame();
        }

        computerMove();
        if (isBoardFull()) {
            return finishGame();
        }

        return MoveResult.ok("Ход выполнен.", boardAsLines());
    }

    private MoveResult finishGame() {
        int playerMax = findLongestChain(PLAYER);
        int computerMax = findLongestChain(COMPUTER);
        String result = determineWinner(playerMax, computerMax);
        return MoveResult.finished("Игра завершена.", boardAsLines(), playerMax, computerMax, result);
    }

    private void computerMove() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == EMPTY) {
                    emptyCells.add(new int[]{row, col});
                }
            }
        }
        if (emptyCells.isEmpty()) {
            return;
        }

        int[] selectedCell = emptyCells.get(ThreadLocalRandom.current().nextInt(emptyCells.size()));
        board[selectedCell[0]][selectedCell[1]] = COMPUTER;
    }

    private boolean isInside(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    private boolean isBoardFull() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<String> boardAsLines() {
        List<String> lines = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            lines.add(new String(board[row]));
        }
        return lines;
    }

    private int findLongestChain(char symbol) {
        int longest = 0;
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] != symbol) {
                    continue;
                }

                for (int[] direction : directions) {
                    int currentLength = countChain(row, col, direction[0], direction[1], symbol);
                    longest = Math.max(longest, currentLength);
                }
            }
        }
        return longest;
    }

    private int countChain(int row, int col, int dRow, int dCol, char symbol) {
        int prevRow = row - dRow;
        int prevCol = col - dCol;
        if (isInside(prevRow, prevCol) && board[prevRow][prevCol] == symbol) {
            return 0;
        }

        int length = 0;
        int currentRow = row;
        int currentCol = col;
        while (isInside(currentRow, currentCol) && board[currentRow][currentCol] == symbol) {
            length++;
            currentRow += dRow;
            currentCol += dCol;
        }
        return length;
    }

    private String determineWinner(int playerMax, int computerMax) {
        if (playerMax > computerMax) {
            return "victory";
        }
        if (computerMax > playerMax) {
            return "defeat";
        }
        return "draw";
    }
}
