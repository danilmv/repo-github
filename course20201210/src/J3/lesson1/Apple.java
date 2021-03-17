package J3.lesson1;

public class Apple extends Fruit implements ProperFruit{
    private static final float WEIGHT = 1.0f;
    private static final String NAME = "Яблоко";

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
