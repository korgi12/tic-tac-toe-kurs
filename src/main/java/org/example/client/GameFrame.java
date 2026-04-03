package org.example.client;

import org.example.shared.UserStats;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public class GameFrame extends JFrame {
    private final TicTacToeClient client;
    private final JLabel welcomeLabel = new JLabel();
    private final JLabel statsLabel = new JLabel();
    private final JLabel statusLabel = new JLabel("Выберите размер поля и начните игру.");
    private final JPanel boardPanel = new JPanel();
    private final JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(3, 3, 10, 1));

    public GameFrame(TicTacToeClient client) {
        super("Крестики-нолики — игра");
        this.client = client;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setMinimumSize(new Dimension(760, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    }

    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new BorderLayout(10, 10));

        JPanel info = new JPanel(new GridLayout(0, 1, 4, 4));
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 20f));
        info.add(welcomeLabel);
        info.add(statsLabel);
        top.add(info, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.add(new JLabel("Размер поля:"));
        controls.add(sizeSpinner);

        JButton newGameButton = new JButton("Новая игра");
        newGameButton.addActionListener(event -> client.startGame((Integer) sizeSpinner.getValue(), this));
        controls.add(newGameButton);

        JButton ratingButton = new JButton("Рейтинг");
        ratingButton.addActionListener(event -> client.openLeaderboard(this));
        controls.add(ratingButton);

        JButton logoutButton = new JButton("Выйти из аккаунта");
        logoutButton.addActionListener(event -> client.logout(this));
        controls.add(logoutButton);

        top.add(controls, BorderLayout.EAST);
        return top;
    }

    private JPanel buildCenterPanel() {
        boardPanel.setBorder(BorderFactory.createTitledBorder("Игровое поле"));
        return boardPanel;
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(statusLabel, BorderLayout.CENTER);
        return bottom;
    }

    public void setUser(String username) {
        welcomeLabel.setText("Добро пожаловать, " + username + "!");
    }

    public void updateStats(UserStats stats) {
        if (stats == null) {
            statsLabel.setText("Статистика недоступна.");
            return;
        }
        statsLabel.setText(stats.toDisplayText());
    }

    public void updateBoard(List<String> board) {
        boardPanel.removeAll();
        if (board == null || board.isEmpty()) {
            boardPanel.revalidate();
            boardPanel.repaint();
            return;
        }

        int size = board.size();
        boardPanel.setLayout(new GridLayout(size, size, 6, 6));

        for (int row = 0; row < size; row++) {
            String line = board.get(row);
            for (int col = 0; col < size; col++) {
                char value = line.charAt(col);
                String displayValue = value == '.' ? "" : String.valueOf(value);
                int currentRow = row;
                int currentCol = col;
                JButton cellButton = new JButton(displayValue);
                cellButton.setFont(cellButton.getFont().deriveFont(Font.BOLD, 22f));
                cellButton.setFocusPainted(false);
                cellButton.addActionListener(event -> client.makeMove(currentRow, currentCol, this));
                if (value != '.') {
                    cellButton.setEnabled(false);
                }
                boardPanel.add(cellButton);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    public void showError(String text) {
        JOptionPane.showMessageDialog(this, text, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String text) {
        JOptionPane.showMessageDialog(this, text, "Информация", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showResult(String text) {
        JOptionPane.showMessageDialog(this, text, "Результат игры", JOptionPane.INFORMATION_MESSAGE);
    }
}
