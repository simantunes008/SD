package ex2;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();

        int N = 10;

        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++) {
            threads[i] = new Thread(new Deposit(bank));
            threads[i].start();
        }

        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {}
        }

        System.out.println("Saldo atual: " + bank.balance());
    }
}
