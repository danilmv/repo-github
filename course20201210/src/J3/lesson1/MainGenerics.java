package J3.lesson1;
// Урок 1. Обобщения. ДЗ:
//1. Написать метод, который меняет два элемента массива местами (массив может быть любого ссылочного типа);
//2. Написать метод, который преобразует массив в ArrayList;
//3. Большая задача:
//Есть классы Fruit -> Apple, Orange (больше фруктов не надо);
//Класс Box, в который можно складывать фрукты. Коробки условно сортируются по типу фрукта, поэтому в одну коробку
// нельзя сложить и яблоки, и апельсины;
//Для хранения фруктов внутри коробки можно использовать ArrayList;
//Сделать метод getWeight(), который высчитывает вес коробки, зная количество фруктов и вес одного фрукта
// (вес яблока – 1.0f, апельсина – 1.5f. Не важно, в каких это единицах);
//Внутри класса Коробка сделать метод compare, который позволяет сравнить текущую коробку с той, которую подадут
// в compare в качестве параметра, true – если она равны по весу, false – в противном случае (коробки с яблоками
// мы можем сравнивать с коробками с апельсинами);
//Написать метод, который позволяет пересыпать фрукты из текущей коробки в другую (помним про сортировку фруктов:
// нельзя яблоки высыпать в коробку с апельсинами). Соответственно, в текущей коробке фруктов не остается, а в другую перекидываются объекты, которые были в этой коробке;
//Не забываем про метод добавления фрукта в коробку.
//** Добавить оганиченную вместимость коробок

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainGenerics {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) {
//       1.
        System.out.println("\n" + ANSI_YELLOW + " ### Задание 1 ###\n" + ANSI_RESET);
        String[] words = {"убить", "нельзя", ", ", "помиловать"};
        Arrays.stream(words).forEach(a -> System.out.print(a + " "));
        swap2(words, 2, 1);
        System.out.println("");
        Arrays.stream(words).forEach(a -> System.out.print(a + " "));

//       2.
        System.out.println("\n\n" + ANSI_YELLOW + "### Задание 2 ###\n" + ANSI_RESET);
        List<String> list = convert(words);
        System.out.print(words.getClass().getSimpleName() + " >> " + list.getClass().getSimpleName() + ": ");
        list.forEach(a -> System.out.print(a + " "));

//       3.
        System.out.println("\n\n" + ANSI_YELLOW + "### Задание 3 ###\n" + ANSI_RESET);

        Packing packing = new Packing();
        packing.startPacking(47);
        System.out.println(packing);

//       Сравнение
        System.out.println(ANSI_GREEN + "** Сравнение коробок по весу **\n" + ANSI_RESET);
        Box<?> box1 = packing.getBox(0);
        Box<?> box2 = packing.getBox(2);
        if (box1.compare(box2))
            System.out.println(box1.getName() + " равна по весу " + box2.getName());
        else
            System.out.println(box1.getName() + " не равна по весу " + box2.getName());

//       Перекладывание
        System.out.println("\n" + ANSI_GREEN + "** Перекладывание фруктов **\n" + ANSI_RESET);
        Box<Orange> boxOfOranges = new Box<>(5);
        System.out.println("Берем пустую коробку: " + boxOfOranges);

        Box box = packing.getBox(packing.getSize() - 1);
        System.out.println("Переложим все фрукты из " + box.getName()
                + " в " + boxOfOranges.getName() + ": ");

        boolean result = boxOfOranges.moveFromOtherBox(box);
        if (result) {
            System.out.println(box);
            System.out.println(boxOfOranges);
        } else {
            //это из предыдущей реализации, где тип коробки определялся при создании...
            // теперь он определяется первым фруктом, поэтому этот случай не произойдет
            System.out.println("!!!!" + boxOfOranges.getName() + " предназначена для других фруктов");

            Box<Apple> boxOfApples = new Box<>( 5);
            System.out.println("\nБерем другую коробку: " + boxOfApples + "\nи переложим фрукты в нее");

            result = boxOfApples.moveFromOtherBox(box);
            if (result) {
                System.out.println(box);
                System.out.println(boxOfApples);
            }
        }
    }

    public static void swap(Object[] array, int a, int b) {
        if (array == null || array.length < a + 1 || array.length < b + 1)
            return;

        Object c = array[a];
        array[a] = array[b];
        array[b] = c;
    }

    public static <T> void swap2(T[] array, int a, int b) {
        if (array == null || array.length < a + 1 || array.length < b + 1)
            return;

        T c = array[a];
        array[a] = array[b];
        array[b] = c;
    }

    public static <T> ArrayList<T> convert(T[] array) {
//        return new ArrayList<T>(Arrays.asList(array));
        ArrayList<T> list = new ArrayList<>();
        for (int i = 0; i < array.length; i++)
            list.add(array[i]);

        return list;
    }
}
