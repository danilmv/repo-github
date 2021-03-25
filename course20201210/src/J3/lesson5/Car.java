package J3.lesson5;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class Car implements Runnable {
    private static int CARS_COUNT;
    private volatile static AtomicBoolean WIN = new AtomicBoolean(false);
    private final CyclicBarrier startCount;
    private final CyclicBarrier announcementCount;

    static {
        CARS_COUNT = 0;
    }

    private Race race;
    private int speed;
    private String name;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed, CyclicBarrier announcement, CyclicBarrier startCount) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
        this.startCount = startCount;
        this.announcementCount = announcement;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            System.out.println(this.name + " готов");
            announcementCount.await();

            startCount.await();
            for (int i = 0; i < race.getStages().size(); i++) {
                race.getStages().get(i).go(this);
            }
            if (WIN.compareAndSet(false, true)) {
                System.out.println("\u001B[34m" + this.name + " - WIN\u001B[0m");
            }
            announcementCount.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}