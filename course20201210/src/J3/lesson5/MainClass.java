package J3.lesson5;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MainClass {
    public static final int CARS_COUNT = 4;
    public final static String[] COLORS = {"\u001B[31m", "\u001B[32m", "\u001B[33m", "\u001B[35m"};

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        CyclicBarrier announcementCount = new CyclicBarrier(CARS_COUNT + 1);
        CyclicBarrier startCount = new CyclicBarrier(CARS_COUNT + 1);
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10), COLORS[i % COLORS.length], announcementCount, startCount);
        }
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }
        announcementCount. await();
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
//        startCount.await();
        announcementCount.await();
        announcementCount.await();
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }
}