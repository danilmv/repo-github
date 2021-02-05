package J1.lesson5.classes;

import J1.lesson5.model.Animal;

public class Horse extends Animal {
    public Horse(String name){
        super(name, 1500, 100, 3);
    }

    @Override
    public void run(int distance){
        int delta = -totalRun;
        totalRun +=distance;
        if (totalRun > maxRun)
            totalRun = maxRun;
        delta += totalRun;

        if (delta > 0)
            System.out.println(name + " проскакал(а) " + delta + " метров" + (totalRun==maxRun?" и устал(а)":""));
        else
            System.out.println(name + " не может больше скакать");
    }
}
