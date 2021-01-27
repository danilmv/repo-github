package J2.lesson1;

public class Human implements Jumping, Running {

    private float maxHeight = 2;
    private float maxLength = 5000;
    private boolean active = true;

    public Human(float maxHeight, float maxLength) {
        setMaxHeight(maxHeight);
        setMaxLength(maxLength);
    }

    public Human() {
    }

    public void setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setMaxLength(float maxLength) {
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
    public String toString() {
        return "Человек";
    }


    @Override
    public boolean isActive() {
        return active;
    }
}
