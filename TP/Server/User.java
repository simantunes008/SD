package Server;

import java.util.concurrent.locks.ReentrantLock;

public class User {
    private String username;
    private String password;
    final ReentrantLock lock;

    User() {
        this.username = null;
        this.password = null;
        this.lock = new ReentrantLock();
    }

    User(String username, String password) {
        this.username = username;
        this.password = password;
        this.lock = new ReentrantLock();
    }

    public void setUsername(String username) {
        try {
            this.lock.lock();
            this.username = username;
        } finally {
            this.lock.unlock();
        }
    }

    public void setPassword(String password) {
        try {
            this.lock.lock();
            this.password = password;
        } finally {
            this.lock.unlock();
        }
    }

    public String getUsername() {
        try {
            this.lock.lock();
            return this.username;
        } finally {
            this.lock.unlock();
        }
    }

    public String getPassword() {
        try {
            this.lock.lock();
            return this.password;
        } finally {
            this.lock.unlock();
        }
    }
}
