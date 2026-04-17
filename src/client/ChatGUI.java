package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String username;

    public ChatGUI(Socket socket, PrintWriter out, BufferedReader in, String username) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.username = username;
        setTitle("Chat Room - " + username);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });

        // Luồng nhận tin từ server
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    final String msg = line;
                    SwingUtilities.invokeLater(() -> chatArea.append(msg + "\n"));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(ChatGUI.this, "Mất kết nối server"));
                logout();
            }
        }).start();
    }

    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (msg.isEmpty()) return;
        out.println(msg);
        messageField.setText("");
    }

    private void logout() {
        try {
            out.println("LOGOUT");
            socket.close();
        } catch (IOException e) {}
        System.exit(0);
    }
}