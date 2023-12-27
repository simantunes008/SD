package Server;

import java.util.*;
import java.util.concurrent.locks.*;

public class DataBase {
    private final Map<String, User> users;
    private final ReentrantReadWriteLock rwLock;

    public DataBase() {
        this.users = new HashMap<>();
        this.rwLock = new ReentrantReadWriteLock();
    }

    public boolean isUser(String username) {
        try {
            this.rwLock.readLock().lock();
            return this.users.containsKey(username);
        } finally {
            this.rwLock.readLock().unlock();
        }
    }

    public boolean authenticateUser(String username, String password) {
        try {
            this.rwLock.readLock().lock();
            User user = this.users.get(username);
            return user != null && user.getPassword().equals(password);
        } finally {
            this.rwLock.readLock().unlock();
        }
    }

    public void putUser(String username, User user) {
        try {
            this.rwLock.writeLock().lock();
            this.users.put(username, user);
        } finally {
            this.rwLock.writeLock().unlock();
        }
    }
}
