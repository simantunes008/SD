package ex2;

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
            p.quantity += quantity;
            p.c.signalAll();
            System.out.println(item + " quantity: " + p.quantity);
        } finally {
            lock.unlock();
        }
    }

    public void consume(Set<String> items) throws InterruptedException {
        boolean flag = true;
        try {
            lock.lock();
            while (flag) {
                int size = items.size();
                int counter = 0;
                for (String s : items) {
                    Product p = get(s);
                    if (p.quantity == 0) {
                        System.out.println("Waiting for item: " + s);
                        p.c.await();
                        break;
                    }
                    counter++;
                }
                if (counter == size)
                    flag = false;
            }
            for (String s : items) {
                System.out.println(s + " quantity: " + --get(s).quantity);
            }
        } finally {
            lock.unlock();
        }
    }

}
