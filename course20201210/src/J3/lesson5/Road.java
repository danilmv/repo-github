package J3.lesson5;

public class Road extends Stage {
    public Road(int length) {
        this.length = length;
        this.description = "Дорога " + length + " метров";
    }

    @Override
    public void go(Car c) {
        try {
            System.out.println(c.getColor()+ c.getName() + " начал этап: " + description + Car.RESET);
            Thread.sleep(length * 1000L / c.getSpeed() );
            System.out.println(c.getColor() + c.getName() + " закончил этап: " + description + Car.RESET);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}