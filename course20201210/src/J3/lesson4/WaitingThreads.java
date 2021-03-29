package J3.lesson4;

public class WaitingThreads implements Runnable {
    private static final Object SYNC_MONITOR = new Object();
    private static final int REPEATS = 5;

    private volatile static char lastSymbol;

    private final char symbol;
    private final char previousSymbol;

    public WaitingThreads(char symbol, char previousSymbol) {
        this.symbol = symbol;
        this.previousSymbol = previousSymbol;
    }

    @Override
    public void run() {
        synchronized (SYNC_MONITOR) {
            for (int i = 0; i < REPEATS; i++) {
                if (lastSymbol == previousSymbol) {
                    System.out.print(symbol);
                    lastSymbol = symbol;
                    SYNC_MONITOR.notifyAll();
                } else
                    i--;

                try {
                    SYNC_MONITOR.wait();
                } catch (InterruptedException e) {
                    System.err.println(Thread.currentThread().getName() + ": " + e.getMessage());
                }
            }
            SYNC_MONITOR.notifyAll();
        }
    }

    public static void main(String[] args) {
        lastSymbol = 'C';

        new Thread(new WaitingThreads('B', 'A')).start();
        new Thread(new WaitingThreads('A', 'C')).start();
        new Thread(new WaitingThreads('C', 'B')).start();
    }
}
