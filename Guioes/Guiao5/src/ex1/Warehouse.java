package ex1;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();
    private ReentrantLock lock = new ReentrantLock();

    private class Product {
        int quantity = 0;
        Condition c = lock.newCondition();
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) {
        try {
            lock.lock();
            Product p = get(item);
            if (p.quantity == 0) {
                p.quantity += quantity;
                p.c.signalAll();
            } else
                p.quantity += quantity;
            System.out.println(item + " quantity: " + p.quantity);
        } finally {
            lock.unlock();
        }
    }

    public void consume(Set<String> items) throws InterruptedException {
        try {
            lock.lock();
            for (String s : items) {
                Product p = get(s);
                while (p.quantity == 0) {
                    System.out.println("Waiting for item: " + s);
                    p.c.await();
                }
                System.out.println(s + " quantity: " + --p.quantity);
            }
        } finally {
            lock.unlock();
        }
    }

}
