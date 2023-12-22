package Ex1;

public class Increment implements Runnable {
    public void run() {
        final long I = 100;

        for(long i = 1; i <= I; i++) {
            System.out.println("[" + Thread.currentThread().getName() + "] - " + i);
        }
    }
}
