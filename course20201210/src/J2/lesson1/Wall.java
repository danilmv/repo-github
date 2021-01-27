package J2.lesson1;

public class Wall {
    private float height;
    public Wall(float height){
        this.height = height;
    }

    public boolean doIt(Jumping participant){
        return participant.jump(height);
    }

    public float getHeight() {
        return height;
    }
}
