package org.example;

import org.example.client.TicTacToeClient;

import javax.swing.SwingUtilities;

public class ClientMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToeClient().start());
    }
}
