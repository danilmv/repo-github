package lesson2;

import java.util.Arrays;
import java.util.Random;

public class HoweWork2 {
    public static Random rand = new Random();
    final public static int sizeTask2 = 8;
    final public static int minTask3 = 6;

    public static void main(String[] args) {
//        1 Задать целочисленный массив, состоящий из элементов 0 и 1. Например: [ 1, 1, 0, 0, 1, 0, 1, 1, 0, 0 ].
//        Написать метод, заменяющий в  принятом массиве 0 на 1, 1 на 0;
        int []arrayTask1 = getArray(15, 1);
        System.out.println("Задание 1.\nМассив из 0 и 1:\n" + Arrays.toString(arrayTask1));
        task1(arrayTask1);
        System.out.println("Преобразованный массив:\n" + Arrays.toString(arrayTask1));

//        2 Задать пустой целочисленный массив размером 8.
//        Написать метод, который c помощью цикла заполнит его значениями 1 4 7 10 13 16 19 22;
        int []arrayTask2 = new int[sizeTask2];
        task2(arrayTask2);
        System.out.println("\nЗадание 2.\nЗаполненный массив:\n" + Arrays.toString(arrayTask2));

//        3 Задать массив [ 1, 5, 3, 2, 11, 4, 5, 2, 4, 8, 9, 1 ],
//        написать метод, принимающий на вход массив и умножающий числа меньше 6 на 2;
        int []arrayTask3 = {1, 5, 3, 2, 11, 4, 5, 2, 4, 8, 9, 1};
        System.out.println("\nЗадание 3.\nНачальный массив:\n" + Arrays.toString(arrayTask3));
        task3(arrayTask3);
        System.out.println("Преобразованный массив:\n" + Arrays.toString(arrayTask3));

//        4 Задать одномерный массив. Написать методы поиска в нём минимального и максимального элемента;
        int []arrayTask4 = getArray(20, 99);
        int []minmax = {0,0};
        System.out.println("\nЗадание 4.\nНачальный массив:\n" + Arrays.toString(arrayTask4));
        task4(arrayTask4, minmax);
        System.out.println("Минимальное значение: " + minmax[0] + " Максимальное значение: " + minmax[1]);

//        5 * Создать квадратный целочисленный массив (количество строк и столбцов одинаковое),
//        заполнить его диагональные элементы единицами, используя цикл(ы);
        int [][]arrayTask5 = new int[5][5];
        task5(arrayTask5);
        System.out.println("\nЗадание 5.\nЗаполненные диагонали:");
        for (int i = 0; i < arrayTask5.length; i++)
            System.out.println(Arrays.toString(arrayTask5[i]));

//        6 ** Написать метод, в который передается не пустой одномерный целочисленный массив,
//        метод должен вернуть true если в массиве есть место, в котором сумма левой и правой части массива равны.
//        Примеры:
//        checkBalance([1, 1, 1, || 2, 1]) → true,
//                checkBalance ([2, 1, 1, 2, 1]) → false,
//                checkBalance ([10, || 1, 2, 3, 4]) → true.
//                Абстрактная граница показана символами ||, эти символы в массив не входят.
        int []arrayTask6 = getArray(7, 3, 1);;
        StringBuilder result = new StringBuilder();

        System.out.println("\nЗадание 6.");
        for (int i = 0; i < 5; i++){
            result.setLength(0);

            System.out.println("Массив " + Arrays.toString(arrayTask6) +
                    (task6(arrayTask6, result) ? " " : " не ") + "сбалансирован " + result);
            setArray(arrayTask6, 3, 1);
        }

//        7 *** Написать метод, которому на вход подаётся одномерный массив и число n (может быть положительным,
//        или отрицательным), при этом метод должен циклически сместить все элементы массива на n позиций.
//          [1,2,3,4,5], -2 => [3,4,5,1,2]
//          [1,2,3,4,5], 2 => [4,5,1,2,3]
        int []arrayTask7 = getArray(5,9, 1);
        int move = 2;
        System.out.println("\nЗадание 7.\nИсходный массив: " + Arrays.toString(arrayTask7));
        task7(arrayTask7, move);
        System.out.println("Массив, сдвинутый на " + move + ": " + Arrays.toString(arrayTask7));
        move = -1;
        task7(arrayTask7, move);
        System.out.println("Массив, сдвинутый на " + move + ": " + Arrays.toString(arrayTask7));

//        8 **** Не пользоваться вспомогательным массивом при решении задачи 7.
        System.out.println("\nЗадание 8.\nИсходный массив: " + Arrays.toString(arrayTask7));
        move = 1;
        task8(arrayTask7, move);
        System.out.println("Массив, сдвинутый на " + move + ": " + Arrays.toString(arrayTask7));
        move = -2;
        task8(arrayTask7, move);
        System.out.println("Массив, сдвинутый на " + move + ": " + Arrays.toString(arrayTask7));
    }
    public static void setArray(int[] array, int max, int min){
        for (int i = 0; i < array.length; i++)
            array[i] = min + rand.nextInt(max - min +1);
    }
    public static int[] getArray(int length, int max, int min){
        int []array = new int[length];
        setArray(array, max, min);
        return array;
    }
    public static int[] getArray(int length, int max){
        return getArray(length, max, 0);
    }

    public static void task1(int []array){
        for (int i = 0; i< array.length; i++)
            array[i] = (array[i] == 1)? 0 : 1;
    }
    public static void task2(int []array){
        for (int i = 0; i < array.length; i++)
            array[i] = 1 + 3*i;
    }
    public static boolean task3(int []array){
        boolean changed = false;
        for (int i = 0; i < array.length; i++)
            if (array[i] < minTask3){
                array[i] *= 2;
                changed = true;
            }
        return changed;
    }
    public static void task4(int []array, int []minmax){
        minmax[0] = minmax[1] = array[0];
        for (int i = 1; i < array.length; i++){
            if (minmax[0] > array[i]) minmax[0] = array[i];
            if (minmax[1] < array[i]) minmax[1] = array[i];
        }
    }
    public static void task5(int [][]table){
        for(int i = 0; i<table.length; i++){
            table[i][i] = 1;
            table[i][table.length -1 - i] = 1;
        }
    }
    public static boolean task6(int []array, StringBuilder showRes){
        int sumRight = 0, sumLeft = 0;

        for (int i = 0; i < array.length; i++) sumRight += array[i];

        if (sumRight % 2 != 0)return false;

        for (int i = 0; i < array.length - 1; i++){
            sumLeft += array[i];
            sumRight -= array[i];
            if (sumRight == sumLeft){
                if (showRes != null) {
                    showRes.append(": [");
                    for (int j = 0; j < array.length; j++){
                        showRes.append(array[j] + ((i==j)?" || ":" "));
                    }
                    showRes.append("]");
                }
                return true;
            }
        }
        return false;
    }
    public static boolean task6(int []array){
        return task6(array, null);
    }
    public static void task7(int []array, int move) {
        int copy[] = Arrays.copyOf(array, array.length);

        move %= array.length;
        for (int i = 0; i < array.length; i++) {
            array[i] = copy[(array.length + i - move) % array.length];
        }

    }

    public static void task8 ( int []array, int move){
        move %= array.length;
        int step = (int)Math.signum(move);
        int from, to;
        if (step < 0) {
            from = 0;
            to = array.length-1;
        }else if (step > 0){
            from = array.length-1;
            to = 0;
        }else
            return;

        int step2 = - step;


        for (int i = 0; i != move; i+=step){
            for (int j = from; j!= to; j+=step2)
                swap(array, j, j + step2);
        }
    }
    public static void swap(int []array, int a, int b){
        int buf = array[a];
        array[a] = array[b];
        array[b] = buf;
    }
}