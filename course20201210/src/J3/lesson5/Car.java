package J3.lesson5;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class Car implements Runnable {
    private static int CARS_COUNT;
    private volatile static AtomicBoolean WIN = new AtomicBoolean(false);
    private final CyclicBarrier startCount;
    private final CyclicBarrier announcementCount;
    private final String COLOR;
    public final static String RESET = "\u001B[0m";

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

    public Car(Race race, int speed, String color, CyclicBarrier announcement, CyclicBarrier startCount) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
        this.startCount = startCount;
        this.announcementCount = announcement;
        this.COLOR = color;
    }

    @Override
    public void run() {
        try {
            System.out.println(COLOR + this.name + " готовится" + RESET);
            Thread.sleep(500 + (int) (Math.random() * 800));
            System.out.println(COLOR + this.name + " готов" + RESET);
            announcementCount.await();
            announcementCount.await();

//            startCount.await();
            for (int i = 0; i < race.getStages().size(); i++) {
                race.getStages().get(i).go(this);
            }
            if (WIN.compareAndSet(false, true)) {
                System.out.println(COLOR + this.name + " -\u001B[34m WIN" + RESET);
            }
            announcementCount.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getColor() {
        return COLOR;
    }
}