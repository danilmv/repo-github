package J2.lesson3;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Collections {
    static Random random = new Random();

    public static void main(String[] args) {
//        1. Создать массив с набором слов (10-20 слов, среди которых должны встречаться повторяющиеся).
//        Найти и вывести список уникальных слов, из которых состоит массив (дубликаты не считаем).
//        Посчитать, сколько раз встречается каждое слово.
        String[] strWords = {"one", "two", "three", "four", "five", "six", "seven", "eight"};

        String[] strArray = new String[random.nextInt(10) + 11];

        for (int i = 0; i < strArray.length; i++)
            strArray[i] = strWords[random.nextInt(strWords.length)];

        HashMap<String, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < strArray.length; i++) {
            Integer value = hashMap.get(strArray[i]);
            if (value != null)
                hashMap.replace(strArray[i], value, value + 1);
            else
                hashMap.put(strArray[i], 1);
        }

        System.out.println("original array: " + Arrays.toString(strArray));
        System.out.print("this array contains next words: ");
        for (String word : hashMap.keySet()) System.out.print(word + " ");
        System.out.println("");
        System.out.println("Collection: " + hashMap);


//        2. Написать простой класс ТелефонныйСправочник, который хранит в себе список фамилий и телефонных номеров.
//        В этот телефонный справочник с помощью метода add() можно добавлять записи.
//        С помощью метода get() искать номер телефона по фамилии. Следует учесть, что под одной фамилией может
//        быть несколько телефонов (в случае однофамильцев), тогда при запросе такой фамилии должны выводиться все телефоны.

        PhoneBook phoneBook = new PhoneBook();
        String[] listOfNames = {"Ivanov", "Petrov", "Sidorov", "Popova", "Suvorova", "Medvedev", "Lomonosov"};
        for (int i = 0; i < 10; i++) {
            phoneBook.add(listOfNames[random.nextInt(listOfNames.length)], getRandomPhone());
        }

        System.out.println("\nPhone Book: ");

        for (int i = 0; i < listOfNames.length; i++) {
            HashSet<String> numbers = phoneBook.get(listOfNames[i]);
            if (numbers != null)
                System.out.println(listOfNames[i] + " contact information: " + numbers);
            else
                System.out.println(listOfNames[i] + " contact information is absent in our Phone Book");
        }

        //System.out.println(phoneBook);
    }

    static String getRandomPhone() {
        return "+" + (1 + random.nextInt(9)) +
                "(" + random.nextInt(1000) + ")-" + random.nextInt(1000) +
                "-" + random.nextInt(100) + "-" + random.nextInt(100);
    }
}
