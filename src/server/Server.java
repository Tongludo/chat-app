package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients = new ArrayList<>();
    private UserManager userManager = new UserManager();
    private boolean running = true;

    public Server(int port) {
        this.port = port;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public synchronized void broadcast(String message, ClientHandler exclude) {
        for (ClientHandler c : clients) {
            if (exclude != null && c == exclude) continue;
            c.sendMessage(message);
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 12345; // có thể đọc từ args
        Server server = new Server(port);
        server.start();
    }
}