package J3.lesson1;

import java.util.ArrayList;

public class Box<T extends Fruit> {
    private static final float ACCEPTABLE_DIFFERENCE = 0.00001f;
    private final int capacity;
    private final ArrayList<T> fruits = new ArrayList<>();
    private final String name;

    Class<? extends Fruit> type;

    private static int id = 1;

    public Box(Class<? extends Fruit> type) {
        this(type, 10 + (int) (Math.random() * 5));
    }

    public Box(Class<? extends Fruit> type, int capacity) {
        this.type = type;
        this.capacity = capacity;

        name = "Коробка№ " + (id++);
    }

    public <Z> boolean add(Z fruit) {
        if (fruits.size() >= capacity || fruit.getClass() != type)
            return false;

        return fruits.add((T) fruit);
    }

    public <Z> void remove(Z fruit) {
        fruits.remove((T) fruit);
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
        return name + ": "
                + "\nнаполнение: " + fruits.size() + "/" + capacity + " "
                + "\nтип содержимого: " + type.getSimpleName()
//                + (fruits.isEmpty() ? " " : fruits.get(0).getName())
                + "\nсодержимое : " + fruits
                + "\nВес коробки = " + getWeight()
                + "\n";
    }

    public Class<? extends Fruit> getType() {
        return type;
    }

    public T getFruit() {
        if (fruits.isEmpty())
            return null;
        return fruits.get(0);
    }

    public boolean moveFromOtherBox(Box<? extends Fruit> box) {
        Fruit fruit = null;
        if (type != box.getType())
            return false;

        do {
            if (fruit != null)
                box.remove(fruit);
            fruit = box.getFruit();
        } while (fruit != null && add(fruit));
        return true;
    }

    public <Z extends Fruit> boolean compare(Box<Z> otherBox) {
        return Math.abs(getWeight() - otherBox.getWeight()) < ACCEPTABLE_DIFFERENCE;
    }
}
