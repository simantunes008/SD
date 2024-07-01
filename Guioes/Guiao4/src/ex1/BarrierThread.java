package ex1;

public class BarrierThread implements Runnable {
    private Barrier barrier;

    public BarrierThread(Barrier barrier) {
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            barrier.await();
        } catch (InterruptedException e) {}
    }
}
