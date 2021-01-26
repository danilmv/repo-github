package J2.lesson1;

public class Track {
    private float length;

    public Track(float length){
        this.length = length;
    }

    public boolean doIt(Running participant){
        return participant.run(length);
    }

    public float getLength() {
        return length;
    }
}
