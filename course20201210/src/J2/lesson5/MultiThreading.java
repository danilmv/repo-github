package J2.lesson5;

public class MultiThreading {
    private static final int size = 10000000;
    private static float[] arr = new float[size];

    public static void main(String[] args) {
//        1) Создают одномерный длинный массив, например:
//        static final int size = 10000000;
//        static final int h = size / 2;
//        float[] arr = new float[size];
//        2) Заполняют этот массив единицами;
        for (int i = 0; i < size; i++) arr[i] = 1;
//        3) Засекают время выполнения: long a = System.currentTimeMillis();
        long startTime = System.currentTimeMillis();
//        4) Проходят по всему массиву и для каждой ячейки считают новое значение по формуле:
//          arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        updateArray(0, size);
//        5) Проверяется время окончания метода System.currentTimeMillis();
//        6) В консоль выводится время работы: System.out.println(System.currentTimeMillis() - a);
        System.out.println("First method (single Thread), total time = " + (System.currentTimeMillis() - startTime));

        // Второй метод - многопоточный
        for (int i = 0; i < size; i++) arr[i] = 1;
        startTime = System.currentTimeMillis();

        Thread firstThread = new Thread(() -> updateArray(0, size / 2));
        Thread secondThread = new Thread(() -> updateArray(size / 2, size));
        firstThread.start();
        secondThread.start();

        try {
            firstThread.join();
            secondThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Second method (two Threads), total time = " + (System.currentTimeMillis() - startTime));

//        Отличие первого метода от второго:
//        Первый просто бежит по массиву и вычисляет значения.
//        Второй разбивает массив на два массива, в двух потоках высчитывает новые значения и потом
//        склеивает эти массивы обратно в один.
//
//        Пример деления одного массива на два:
//        System.arraycopy(arr, 0, a1, 0, h);
//        System.arraycopy(arr, h, a2, 0, h);
//
//        Пример обратной склейки:
//        System.arraycopy(a1, 0, arr, 0, h);
//        System.arraycopy(a2, 0, arr, h, h);
//        Примечание:
//        System.arraycopy() копирует данные из одного массива в другой:
//        System.arraycopy(массив-источник, откуда начинаем брать данные из массива-источника, массив-назначение, откуда начинаем записывать данные в массив-назначение, сколько ячеек копируем)
//        По замерам времени:
//        Для первого метода надо считать время только на цикл расчета:
//        for (int i = 0; i < size; i++)
//          arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
//        Для второго метода замеряете время разбивки массива на 2, просчета каждого из двух массивов и склейки.
    }

    private static void updateArray(int from, int to) {
        for (int i = from; i < to; i++)
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5.) * Math.cos(0.2f + i / 5.) * Math.cos(0.4f + i / 2.));
        System.out.println(Thread.currentThread().getName() + ": updateArray is over");
    }
}
