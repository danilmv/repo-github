package J3.lesson1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Packing {
    private static final Random random = new Random();
    private final ArrayList<Box<? extends Fruit>> boxes = new ArrayList<>();
    Iterator<Box<? extends Fruit>> iterator;

    private Fruit getFruit() {
        if (random.nextInt(2) == 1)
            return new Orange();
        else
            return new Apple();
    }

    private <T extends Fruit> Box<T> getNextBox(T fruit) {
        Box<T> box = null;
        Box<? extends Fruit> next;
        iterator = boxes.iterator();
        while (iterator.hasNext()) {
            next = iterator.next();
            if (next.getType() == fruit.getClass() && !next.isFull()) {
                return (Box<T>) next;
            }
        }

        box = new Box<T>(fruit.getClass());
        boxes.add(box);
        return box;
    }

    public void startPacking(int numberOfFruits) {
        Fruit fruit;
        Box<? super Fruit> box;
        for (int i = 0; i < numberOfFruits; i++) {
            fruit = getFruit();
            do {
                box = getNextBox(fruit.getClass().cast(fruit));
            } while (!box.add(fruit));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Количество коробок: ");
        sb.append(boxes.size());
        sb.append("\n\n");
        for (Box<?> box : boxes) {
            sb.append(box);
            sb.append("\n");
        }

        return sb.toString();
    }

    public Box<?> getBox(int position) {
        if (position >= boxes.size())
            return null;
        else
            return boxes.get(position);
    }

    public int getSize() {
        return boxes.size();
    }
}
