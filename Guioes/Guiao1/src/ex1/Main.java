package ex1;

public class Main {
    public static void main(String[] args) {
        int N = 10;

        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++) {
            threads[i] = new Thread(new Increment());
            threads[i].start();
        }

        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {}
        }

        System.out.println("fim");
    }
}