package adicional;

public class Supplier implements Runnable {
    private Warehouse warehouse;
    public Supplier(Warehouse warehouse) { this.warehouse = warehouse; }
    public void run() {
        warehouse.supply("Madeira", 8);
        warehouse.supply("Metal", 5);
        warehouse.supply("Plástico", 2);
    }
}
