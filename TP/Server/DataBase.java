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

    public boolean newUser(String username, User user) {
        try {
            this.rwLock.writeLock().lock();
            if (!this.users.containsKey(username)) {
                this.users.put(username, user);
                return true;
            } else {
                return false;
            }
        } finally {
            this.rwLock.writeLock().unlock();
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
}
