package ex3;

import java.util.concurrent.locks.ReentrantLock;

public class Bank {

    private static class Account {
        private int balance;
        private ReentrantLock lock;
        Account(int balance) { this.balance = balance;
        this.lock = new ReentrantLock(); }
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

    public Bank(int n) {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++) av[i]=new Account(0);
    }

    // Account balance
    public int balance(int id) {
        if (id < 0 || id >= slots)
            return 0;
        av[id].lock.lock();
        try {
            return av[id].balance;
        } finally {
            av[id].lock.unlock();
        }
    }

    // Deposit
    public boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        av[id].lock.lock();
        try {
            return av[id].deposit(value);
        } finally {
            av[id].lock.unlock();
        }
    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        av[id].lock.lock();
        try {
            return av[id].withdraw(value);
        } finally {
            av[id].lock.unlock();
        }
    }

    // Transfer
    boolean transfer (int from, int to, int value) {
        if (from < 0 || from >= slots || to < 0 || to >= slots)
            return false;
        int contaMenorId = Math.min(from, to);
        int contaMaiorId = Math.max(from, to);
        av[contaMenorId].lock.lock();
        av[contaMaiorId].lock.lock();
        try {
            if (!withdraw(from, value))
                return false;
            deposit(to, value);
            return true;
        } finally {
            av[contaMenorId].lock.unlock();
            av[contaMaiorId].lock.unlock();
        }
    }

    // Total accounts balance
    int totalBalance() {
        int sum = 0;
        for (int i = 0; i < slots; i++)
            av[i].lock.lock();
        try {
            for (int i = 0; i < slots; i++)
                sum += balance(i);
            return sum;
        } finally {
            for (int i = 0; i < slots; i++)
                av[i].lock.unlock();
        }
    }
}
