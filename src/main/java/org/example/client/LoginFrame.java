package org.example.client;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class LoginFrame extends JFrame {
    private final TicTacToeClient client;

    public LoginFrame(TicTacToeClient client) {
        super("Крестики-нолики — вход");
        this.client = client;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 320);
        setMinimumSize(new Dimension(460, 320));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Крестики-нолики: регистрация и вход", JLabel.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 20f));
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Вход", buildLoginPanel());
        tabs.addTab("Регистрация", buildRegisterPanel());
        add(tabs, BorderLayout.CENTER);

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    }

    private JPanel buildLoginPanel() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Войти");

        loginButton.addActionListener(event -> client.login(
                usernameField.getText().trim(),
                new String(passwordField.getPassword()),
                this
        ));

        return buildFormPanel(usernameField, passwordField, loginButton, "Выполнить вход");
    }

    private JPanel buildRegisterPanel() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton registerButton = new JButton("Зарегистрироваться");

        registerButton.addActionListener(event -> client.register(
                usernameField.getText().trim(),
                new String(passwordField.getPassword()),
                this
        ));

        return buildFormPanel(usernameField, passwordField, registerButton, "Создать аккаунт");
    }

    private JPanel buildFormPanel(JTextField usernameField, JPasswordField passwordField, JButton actionButton, String titleText) {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        JPanel titlePanel = new JPanel();
        JLabel title = new JLabel(titleText);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        titlePanel.add(title);
        container.add(titlePanel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.add(new JLabel("Логин"));
        form.add(usernameField);
        form.add(new JLabel("Пароль"));
        form.add(passwordField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(actionButton);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(form);
        center.add(buttonPanel);
        center.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        container.add(center, BorderLayout.CENTER);
        return container;
    }

    public void showError(String text) {
        JOptionPane.showMessageDialog(this, text, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}
