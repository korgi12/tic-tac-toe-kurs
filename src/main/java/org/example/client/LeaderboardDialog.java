package org.example.client;

import org.example.shared.LeaderboardEntry;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

public class LeaderboardDialog extends JDialog {
    public LeaderboardDialog(JFrame owner, List<LeaderboardEntry> leaderboard) {
        super(owner, "Рейтинг игроков", true);
        setLayout(new BorderLayout(10, 10));
        setSize(700, 420);
        setLocationRelativeTo(owner);

        String[] columns = {"Место", "Игрок", "Рейтинг", "Победы", "Поражения", "Ничьи", "Игр"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int index = 1;
        for (LeaderboardEntry entry : leaderboard) {
            model.addRow(new Object[]{
                    index++,
                    entry.username,
                    entry.rating,
                    entry.wins,
                    entry.losses,
                    entry.draws,
                    entry.gamesPlayed
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        ((javax.swing.JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    }
}
