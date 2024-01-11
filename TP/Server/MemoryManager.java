package Server;

import java.util.concurrent.locks.*;

public class MemoryManager {
    private final int maxSystemMemory;
    private int systemMemory;
    private final ReadWriteLock rwLock;
    private final Condition memoryAvailable;

    public MemoryManager(int maxMemory) {
        this.maxSystemMemory = maxMemory;
        this.systemMemory = maxMemory;
        this.rwLock = new ReentrantReadWriteLock();
        this.memoryAvailable = rwLock.writeLock().newCondition();
    }

    public int getSystemMemory() {
        try {
            rwLock.readLock().lock();
            return systemMemory;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void allocateMemory(int allocatedMemory) {
        try {
            rwLock.writeLock().lock();
            if (allocatedMemory > maxSystemMemory) {
                throw new IllegalArgumentException("O pedido ultrapassa a memória máxima do sistema.");
            }
            while (systemMemory < allocatedMemory) {
                memoryAvailable.await();
            }
            systemMemory -= allocatedMemory;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void releaseMemory(int releasedMemory) {
        try {
            rwLock.writeLock().lock();
            systemMemory += releasedMemory;
            memoryAvailable.signalAll();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
