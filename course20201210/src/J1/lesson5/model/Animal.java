package J1.lesson5.model;

abstract public class Animal {
    protected String name;
    protected int maxRun;
    protected int maxSwim;
    protected double maxJump;

    protected int totalRun;
    protected int totalSwim;

    private String runAction;

    public Animal(String name, int maxRun, int maxSwim, double maxJump){
        this.name = name;
        this.maxRun = maxRun;
        this.maxSwim = maxSwim;
        this.maxJump = maxJump;
    }

    abstract public void run(int distance);

    public void swim(int distance){
        int delta = -totalSwim;
        totalSwim +=distance;
        if (totalSwim > maxSwim)
            totalSwim = maxSwim;
        delta += totalSwim;

        if (delta > 0)
            System.out.println(name + " проплыл(а) " + delta + " метров" + (totalSwim==maxSwim?" и начал(а) тонуть":""));
        else
            System.out.println(name + " утонул(а)");
    }

    public void jump(double height){
        if (height <= maxJump)
            System.out.println(name + " перепрыгнул(а) барьер в " + height + "м" );
        else
            System.out.println(name + " не может преодолеть барьер в " + height + "м");
    }
}
