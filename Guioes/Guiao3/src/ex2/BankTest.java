package ex2;

import java.util.Random;

public class BankTest {

    private static class Mover implements Runnable {
        Bank b;
        int s; // Number of accounts

        public Mover(Bank b, int s) { this.b=b; this.s=s; }

        public void run() {
            final int moves=100000;
            int from, to;
            Random rand = new Random();

            for (int m=0; m<moves; m++) {
                from=rand.nextInt(s); // Get one
                while ((to=rand.nextInt(s))==from); // Slow way to get distinct
                b.transfer(from,to,100);
            }
        }
    }

    private static class Mover2 implements Runnable {
        Bank b;
        int[] s; // Number of accounts

        public Mover2(Bank b, int[] s) { this.b=b; this.s=s; }

        public void run() {
            final int moves=100000;
            int from, to;
            Random rand = new Random();

            for (int m=0; m<moves; m++) {
                int n = b.totalBalance(s);
                if(n != 10000) System.out.println(n);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int N=10;

        Bank b = new Bank(N);
        int[] ids = new int[N];

        for (int i=0; i<N; i++){
            b.deposit(i,1000);
            ids[i] = i;
        }

        System.out.println(b.totalBalance(ids));

        Thread t1 = new Thread(new Mover(b,N));
        Thread t2 = new Thread(new Mover2(b,ids));

        t1.start(); t2.start(); t1.join(); t2.join();

        System.out.println(b.totalBalance(ids));
    }
}