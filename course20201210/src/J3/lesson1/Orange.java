package J3.lesson1;

public class Orange extends Fruit implements ProperFruit{
    private static final float WEIGHT = 1.5f;
    private static final String NAME = "Апельсин";

    @Override
    public float getWeight() {
        return WEIGHT;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String toString() {
        return NAME;
    }
}
