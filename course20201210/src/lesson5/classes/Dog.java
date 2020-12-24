package lesson5.classes;

import lesson5.model.Animal;

public class Dog extends Animal {
    public Dog(String name){
        super(name, 500, 10, 0.5);
    }
    public Dog(String name, int maxRunDistance){
        super(name, maxRunDistance, 10, 0.5);
    }

    @Override
    public void run(int distance){
        int delta = -totalRun;
        totalRun +=distance;
        if (totalRun > maxRun)
            totalRun = maxRun;
        delta += totalRun;

        if (delta > 0)
            System.out.println(name + " пробежал(а) " + delta + " метров" + (totalRun==maxRun?" и устал(а)":""));
        else
            System.out.println(name + " не может больше бежать");
    }
}
