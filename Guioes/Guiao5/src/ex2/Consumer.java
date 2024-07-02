package ex2;

import java.util.HashSet;
import java.util.Set;

public class Consumer implements Runnable {
    private Warehouse warehouse;
    public Consumer(Warehouse warehouse) { this.warehouse = warehouse; }
    public void run() {
        Set<String> items = new HashSet<>();
        items.add("Madeira");
        items.add("Metal");
        items.add("Plástico");
        try {
            warehouse.consume(items);
        } catch (InterruptedException e) {}
    }
}
