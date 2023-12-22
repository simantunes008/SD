package Ex2;

public class Deposit implements Runnable {
    private Bank bank;

    public Deposit (Bank bank) {
        this.bank = bank;
    }

    public void run() {
        int I = 1000, V = 100;
        for (int i = 0; i < I; i++) {
            this.bank.deposit(V);
        }
    }
}
