package J3.lesson1;

import java.util.ArrayList;

public class Box<T extends Fruit & ProperFruit> {
    private static final float ACCEPTABLE_DIFFERENCE = 0.00001f;
    private final int capacity;
    private final ArrayList<T> fruits = new ArrayList<>();
    private final String name;

    private static int id = 1;

    public Box() {
        this(10 + (int) (Math.random() * 5));
    }

    public Box(int capacity) {
        this.capacity = capacity;

        name = "Коробка№ " + (id++);
    }

    public boolean add(T fruit) {
        if (!fruits.isEmpty() && fruit.getType() != fruits.get(0).getType())
            return false;

        if (fruits.size() >= capacity)
            return false;

        return fruits.add((T) fruit);
    }

    public void remove(T fruit) {
        fruits.remove(fruit);
    }

    public float getWeight() {
        return fruits.size() == 0 ? 0 : fruits.size() * fruits.get(0).getWeight();
    }

    public boolean isFull() {
        return fruits.size() >= capacity;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        Class type = getType();

        return name + ": "
                + "\nнаполнение: " + fruits.size() + "/" + capacity + " "
                + "\nтип содержимого: " + (type == null ? "неопределен" : type.getSimpleName())
                + "\nсодержимое : " + fruits
                + "\nВес коробки = " + getWeight()
                + "\n";
    }

    public Class getType() {
        if (fruits.isEmpty())
            return null;
        else
            return fruits.get(0).getType();
    }

    public T getFruit() {
        if (fruits.isEmpty())
            return null;
        return fruits.get(0);
    }

    public boolean moveFromOtherBox(Box<T> box) {
        T fruit = null;
        if (getType() != null && getType() != box.getType())
            return false;

        do {
            if (fruit != null)
                box.remove(fruit);
            fruit = box.getFruit();
        } while (fruit != null && add(fruit));
        return true;
    }

    public <Z extends Fruit & ProperFruit> boolean compare(Box<Z> otherBox) {
        if (otherBox == null) return false;
        if (otherBox == this) return true;
        return Math.abs(getWeight() - otherBox.getWeight()) < ACCEPTABLE_DIFFERENCE;
    }
}
