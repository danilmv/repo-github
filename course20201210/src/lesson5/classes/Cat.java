package lesson5.classes;

import lesson5.model.Animal;

public class Cat extends Animal {
    public Cat(String name){
        super(name,200,-1, 2);
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

    @Override
    public void swim(int distance){
        System.out.println(name + " не умеет плавать!");
    }
}
