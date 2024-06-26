package ex2;

import java.util.concurrent.locks.ReentrantLock;

public class Bank {

    private static class Account {
        private int balance;
        Account(int balance) { this.balance = balance; }
        int balance() { return balance; }
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    // Bank slots and vector of accounts
    private int slots;
    private Account[] av;
    private ReentrantLock lock;

    public Bank(int n) {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++) av[i]=new Account(0);
        lock=new ReentrantLock();
    }

    // Account balance
    public int balance(int id) {
        if (id < 0 || id >= slots)
            return 0;
        lock.lock();
        try {
            return av[id].balance;
        } finally {
            lock.unlock();
        }
    }

    // Deposit
    public boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        lock.lock();
        try {
            return av[id].deposit(value);
        } finally {
            lock.unlock();
        }
    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        lock.lock();
        try {
            return av[id].withdraw(value);
        } finally {
            lock.unlock();
        }
    }

    // Transfer
    boolean transfer (int from, int to, int value) {
        if (from < 0 || from >= slots || to < 0 || to >= slots)
            return false;
        lock.lock();
        try {
            if (!withdraw(from, value))
                return false;
            deposit(to, value);
            return true;
        } finally {
            lock.unlock();
        }
    }

    // Total accounts balance
    int totalBalance() {
        int sum = 0;
        lock.lock();
        try {
            for (int i = 0; i < slots; i++)
                sum += balance(i);
            return sum;
        } finally {
            lock.unlock();
        }
    }
}
