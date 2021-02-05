package J1.lesson5.classes;

import J1.lesson5.model.Animal;

public class Bird extends Animal {
    protected int maxFly;
    protected int totalFly;

    public Bird(String name){
        super(name, 5, -1, 0.2);
        maxFly = 5000;
    }

    @Override
    public void run(int distance){
        int delta = -totalRun;
        totalRun +=distance;
        if (totalRun > maxRun)
            totalRun = maxRun;
        delta += totalRun;

        if (delta > 0)
            System.out.println(name + " пропрыгал(а) " + delta + " метров" + (totalRun==maxRun?" и устал(а)":""));
        else
            System.out.println(name + " не может больше прыгать");
    }

    @Override
    public void swim(int distance){
        System.out.println(name + " не умеет плавать!");
    }

    @Override
    public void jump(double height){
        System.out.println(name + " перелетел барьер в " + height + "м");
    }

    public void fly(int distance){
        int delta = -totalFly;
        totalFly +=distance;
        if (totalFly > maxFly)
            totalFly = maxFly;
        delta += totalFly;

        if (delta > 0)
            System.out.println(name + " пролетел(а) " + delta + " метров" + (totalFly==maxFly?" и устал(а)":""));
        else
            System.out.println(name + " не может больше лететь");
    }
}
