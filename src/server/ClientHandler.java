package server;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private Server server;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Authentication phase
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("LOGIN")) {
                    String[] parts = line.split(" ");
                    if (parts.length == 3) {
                        String user = parts[1];
                        String pwd = parts[2];
                        if (server.getUserManager().login(user, pwd)) {
                            username = user;
                            out.println("OK");
                            break;
                        } else {
                            out.println("FAIL");
                        }
                    }
                } else if (line.startsWith("REGISTER")) {
                    String[] parts = line.split(" ");
                    if (parts.length == 3) {
                        String user = parts[1];
                        String pwd = parts[2];
                        if (server.getUserManager().register(user, pwd)) {
                            out.println("OK");
                        } else {
                            out.println("FAIL");
                        }
                    }
                } else {
                    out.println("INVALID_CMD");
                }
            }

            if (username == null) {
                socket.close();
                return;
            }

            // Thông báo client mới vào
            server.broadcast("SERVER: " + username + " đã tham gia phòng chat.", this);
            server.addClient(this);

            // Nhận tin nhắn từ client này
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("LOGOUT")) break;
                // Broadcast tin nhắn
                server.broadcast(username + ": " + message, this);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + username);
        } finally {
            server.removeClient(this);
            if (username != null) {
                server.broadcast("SERVER: " + username + " đã rời phòng chat.", null);
            }
            try { socket.close(); } catch (IOException e) {}
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public String getUsername() {
        return username;
    }
}