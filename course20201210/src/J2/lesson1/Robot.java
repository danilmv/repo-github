package J2.lesson1;

public class Robot implements Running, Jumping{
    private float maxHeight = 0.2f;
    private float maxLength = 500;
    private boolean active = true;

    public Robot(){}

    public Robot(float maxHeight, float maxLength) {
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
        return "Робот";
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
