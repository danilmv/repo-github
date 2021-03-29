package J3.lesson5;

import java.util.concurrent.Semaphore;

public class Tunnel extends Stage {
    private Semaphore semaphore = new Semaphore(MainClass.CARS_COUNT/2);

    public Tunnel() {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
    }

    @Override
    public void go(Car c) {
        try {
            try {
                System.out.println(c.getColor() + c.getName() + " готовится к этапу(ждет): " + description + Car.RESET);
                semaphore.acquire();
                System.out.println(c.getColor() + c.getName() + " начал этап: " + description + Car.RESET);
                Thread.sleep(length * 1000L / c.getSpeed() );
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(c.getColor() + c.getName() + " закончил этап: " + description + Car.RESET);
                semaphore.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
