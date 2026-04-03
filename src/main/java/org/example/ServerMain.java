package org.example;

import org.example.server.TicTacToeServer;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        new TicTacToeServer().start();
    }
}
