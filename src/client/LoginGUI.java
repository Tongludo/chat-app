package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String serverAddress;
    private int serverPort;

    public LoginGUI(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        setTitle("Chat App - Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);
        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        add(loginButton);
        add(registerButton);

        loginButton.addActionListener(e -> doLogin());
        registerButton.addActionListener(e -> doRegister());
    }

    private void connectToServer() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void doLogin() {
        String user = usernameField.getText().trim();
        String pwd = new String(passwordField.getPassword()).trim();
        if (user.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ");
            return;
        }
        try {
            connectToServer();
            out.println("LOGIN " + user + " " + pwd);
            String response = in.readLine();
            if ("OK".equals(response)) {
                // Mở cửa sổ chat
                new ChatGUI(socket, out, in, user).setVisible(true);
                dispose(); // đóng login
            } else {
                JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu");
                socket.close();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến server");
            ex.printStackTrace();
        }
    }

    private void doRegister() {
        String user = usernameField.getText().trim();
        String pwd = new String(passwordField.getPassword()).trim();
        if (user.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ");
            return;
        }
        try {
            connectToServer();
            out.println("REGISTER " + user + " " + pwd);
            String response = in.readLine();
            if ("OK".equals(response)) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Hãy đăng nhập.");
            } else {
                JOptionPane.showMessageDialog(this, "Tên đã tồn tại");
            }
            socket.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối");
        }
    }

    public static void main(String[] args) {
        String serverHost = "localhost";
        int serverPort = 12345;
        SwingUtilities.invokeLater(() -> new LoginGUI(serverHost, serverPort).setVisible(true));
    }
}