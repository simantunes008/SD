package Server;

import java.io.*;
import java.util.concurrent.locks.*;

public class User {
    private String username;
    private String password;
    private final ReadWriteLock rwLock;

    User() {
        this.username = null;
        this.password = null;
        this.rwLock = new ReentrantReadWriteLock();
    }

    User(String username, String password) {
        this.username = username;
        this.password = password;
        this.rwLock = new ReentrantReadWriteLock();
    }

    public void setUsername(String username) {
        try {
            this.rwLock.writeLock().lock();
            this.username = username;
        } finally {
            this.rwLock.writeLock().unlock();
        }
    }

    public void setPassword(String password) {
        try {
            this.rwLock.writeLock().lock();
            this.password = password;
        } finally {
            this.rwLock.writeLock().unlock();
        }
    }

    public String getUsername() {
        try {
            this.rwLock.readLock().lock();
            return this.username;
        } finally {
            this.rwLock.readLock().unlock();
        }
    }

    public String getPassword() {
        try {
            this.rwLock.readLock().lock();
            return this.password;
        } finally {
            this.rwLock.readLock().unlock();
        }
    }

    // @TODO
    public void serialize(DataOutputStream out) throws IOException {

    }

    // @TODO
    public User deserialize(DataInputStream in) throws IOException {

    }
}
