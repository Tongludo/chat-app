package server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USER_FILE = "users.dat";
    private Map<String, String> users; // username -> password

    public UserManager() {
        users = new HashMap<>();
        loadUsers();
    }

    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            users = (Map<String, String>) ois.readObject();
        } catch (FileNotFoundException e) {
            // first run, no file
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (users == null) users = new HashMap<>();
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean register(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, password);
        saveUsers();
        return true;
    }

    public boolean login(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
}