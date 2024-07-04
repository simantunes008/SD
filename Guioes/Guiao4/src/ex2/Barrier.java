package ex2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {
    private final int N;
    private int currentThreads;
    private ReentrantLock lock;
    private Condition condition;
    private int turn;

    Barrier (int N) {
        this.N = N;
        this.currentThreads = 0;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.turn = 0;
    }

    void await() throws InterruptedException {
        lock.lock();
        try {
            currentThreads++;
            int ticket = turn;
            while (ticket == turn) {
                if (currentThreads % N != 0) {
                    System.out.println("[" + Thread.currentThread().getName() + "] Waiting for Barrier");
                    condition.await();
                } else {
                    turn++;
                    condition.signalAll();
                }
            }
            System.out.println("[" + Thread.currentThread().getName() + "] Barrier has finished");
        } finally {
            lock.unlock();
        }
    }

}