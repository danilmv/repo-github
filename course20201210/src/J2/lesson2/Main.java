package J2.lesson2;

public class Main {
    public static final String RED = "\033[0;31m";
    public static final String RESET = "\033[0m";

    public static void main(String[] args) {
//        1. Напишите метод, на вход которого подается двумерный строковый массив размером 4х4,
//        при подаче массива другого размера необходимо бросить исключение MyArraySizeException.
//        2. Далее метод должен пройтись по всем элементам массива, преобразовать в int, и просуммировать.
//        Если в каком-то элементе массива преобразование не удалось (например, в ячейке лежит символ
//        или текст вместо числа), должно быть брошено исключение MyArrayDataException – с детализацией,
//        в какой именно ячейке лежат неверные данные.
//        3. В методе main() вызвать полученный метод, обработать возможные исключения MySizeArrayException
//        и MyArrayDataException и вывести результат расчета.

        String[][][] testArrays = {
                new String[4][5],
                new String[4][4],
                {
                        {"5", "4", "3", "1"},
                        {"5", "4", "3", "i"},
                        {"5", "4", "3", "1"},
                        {"5", "4", "3", "1"},
                },
                {
                        {"5", "4", "3", "1"},
                        {"5", "4", "3", "1"},
                        {"5", "4", "3", "1"},
                        {"5", "4", "3", "1"},
                }
        };

        for (int i = 0; i < testArrays.length; i++) {
            System.out.print("Sum of array #" + (i + 1) + " is ");
            try {
                System.out.println(sumArray(testArrays[i]));

            } catch (MyArraySizeException | MyArrayDataException e) {
//                e.printStackTrace();
//                System.err.println(e.getMessage());
                System.out.println(RED + e.getMessage() + RESET);
            }
        }
    }

    public static int sumArray(String[][] array) throws MyArraySizeException, MyArrayDataException {
        int sum = 0;
        if (array == null || array.length != 4)
            throw new MyArraySizeException("Array must be 4x4");
        for (int i = 0; i < array.length; i++) {
            if (array[i].length != 4)
                throw new MyArraySizeException("Array must be 4x4");

            for (int j = 0; j < array[i].length; j++) {
                try {
                    sum += Integer.parseInt(array[i][j]);
                } catch (NumberFormatException e) {
                    throw new MyArrayDataException("Cell[" + i + "][" + j + "] isn't a number: " + array[i][j]);
                }
            }
        }
        return sum;
    }
}
