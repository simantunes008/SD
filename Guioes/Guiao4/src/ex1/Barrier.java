package ex1;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {
    private final int N;
    private int currentThreads;
    private ReentrantLock lock;
    private Condition condition;

    Barrier (int N) {
        this.N = N;
        this.currentThreads = 0;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }

    void await() throws InterruptedException {
        lock.lock();
        try {
            currentThreads++;
            while (currentThreads < N) {
                System.out.println("[" + Thread.currentThread().getName() + "] Waiting for Barrier");
                condition.await();
            }
            System.out.println("[" + Thread.currentThread().getName() + "] Barrier has finished");
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

}