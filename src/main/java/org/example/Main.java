package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            printUsage();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "server" -> new TicTacToeServer().start();
            case "client" -> new TicTacToeClient().start();
            default -> printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar TicTacToe.jar <server|client>");
    }
}
