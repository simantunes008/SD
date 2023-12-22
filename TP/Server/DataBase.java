package Server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class DataBase {
    private final Map<String, User> Users;
    final ReentrantLock lock;

    DataBase() {
        this.Users = new HashMap<String, User>();
        this.lock = new ReentrantLock();
    }

    public boolean isUser(String username) {
        try {
            this.lock.lock();
            return this.Users.containsKey(username);
        } finally {
            this.lock.unlock();
        }
    }

    public boolean authenticateUser(String username, String password) {
        try {
            this.lock.lock();
            User user = this.Users.get(username);
            return user.getPassword().equals(password);
        } finally {
            this.lock.unlock();
        }
    }

    public void setUsers(String username, User user) {
        try {
            this.lock.lock();
            this.Users.put(username, user);
        } finally {
            this.lock.unlock();
        }
    }
}
