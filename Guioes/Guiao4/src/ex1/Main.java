package ex1;

public class Main {
    public static void main(String[] args) {
        Barrier barrier = new Barrier(10);
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new BarrierThread(barrier));
            threads[i].start();
        }

        for (int i = 0; i < 10; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {}
        }
    }
}