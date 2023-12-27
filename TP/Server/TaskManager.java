package Server;

import java.util.concurrent.locks.*;

public class TaskManager {
    private int pendingTasks;
    private final ReadWriteLock lock;

    public TaskManager() {
        this.pendingTasks = 0;
        this.lock = new ReentrantReadWriteLock();
    }

    public void incrementPendingTasks() {
        try {
            lock.writeLock().lock();
            pendingTasks++;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void decrementPendingTasks() {
        try {
            lock.writeLock().lock();
            pendingTasks--;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getPendingTasks() {
        try {
            lock.readLock().lock();
            return pendingTasks;
        } finally {
            lock.readLock().unlock();
        }
    }
}
