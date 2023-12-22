package Ex3;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int N = 10;
        Thread[] threads = new Thread[N];
        Bank bank = new Bank();

        for (int i = 0; i < N; i++) {
            threads[i] = new Thread(new Deposit(bank));
        }

        for (int i = 0; i < N; i++) {
            threads[i].start();
        }

        for (int i = 0; i< N;i++) {
            threads[i].join();
        }

        System.out.println("Saldo atual: " + bank.balance());
    }
}
