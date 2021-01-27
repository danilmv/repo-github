package J2.lesson1;

public class Cat implements Running, Jumping{
    private float maxHeight = 3;
    private float maxLength = 1000;
    private boolean active = true;

    public Cat(){};

    public Cat(float maxHeight, float maxLength) {
        this.maxHeight = maxHeight;
        this.maxLength = maxLength;
    }

    @Override
    public boolean jump(float height) {
        return active = height <= maxHeight;

    }

    @Override
    public boolean run(float length) {
        return active = length <= maxLength;
    }

    @Override
    public String toString(){
        return "Кот";
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
