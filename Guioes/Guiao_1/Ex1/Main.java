package Ex1;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int N = 10;
        Thread[] threads = new Thread[N]; // Array de 10 threads

        for (int i = 0; i< N; i++) {
            threads[i] = new Thread(new Increment()); // Criar as threads para correrem o runnable increment
            threads[i].setName("Thread " + i); // Definir um número para casa thread
        }

        for (int i = 0; i< N;i++) {
            threads[i].start();
        }

        for (int i = 0; i< N;i++) {
            threads[i].join();
        }

        System.out.println("Finished");
    }
}
