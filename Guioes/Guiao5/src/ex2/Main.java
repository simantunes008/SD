package ex2;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse();

        Thread supplier1 = new Thread(new Supplier(warehouse));
        Thread supplier2 = new Thread(new Supplier(warehouse));
        Thread consumer1 = new Thread(new Consumer(warehouse));
        Thread consumer2 = new Thread(new Consumer(warehouse));
        Thread consumer3 = new Thread(new Consumer(warehouse));

        consumer1.start();
        consumer2.start();
        consumer3.start();
        supplier1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {}
        supplier2.start();

        try {
            consumer1.join();
            consumer2.join();
            consumer3.join();
            supplier1.join();
            supplier2.join();
        } catch (InterruptedException e) {}
    }
}