package lesson6;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//    1.Создать 2 текстовых файла, примерно по 50-100 символов в каждом (особого значения не имеет);
//    2. Написать программу, «склеивающую» эти файлы, то есть вначале идет текст из первого файла, потом текст из второго.
        String []fileList = {"file1.txt", "file2.txt"};
        try {
            MyFile.concatFiles(fileList, "file3.txt");
            System.out.println("Данные файлов " + Arrays.toString(fileList)+ " успешно объединены в указанном файле");
        } catch (IOException e) {
            System.out.println("Возникла ошибка: " + e.getMessage());
        }

//    3. * Написать программу, которая проверяет присутствует ли указанное пользователем слово в файле.
        String result = null;
        String message;
        Scanner scan = new Scanner(System.in);
        System.out.print("Введите слово: ");
        String word = scan.nextLine();

        try {
            message = (result = MyFile.findWord("file1.txt", word)) != null ?
                    "Слово '"+ word +"' найдено в строке:\n" + result :
                    "Слово '"+ word +"' не нейдено";
        } catch (IOException e) {
            message = "Возникла ошибка: "+ e.getMessage();
        }
        System.out.println(message);

//    4. ** Написать метод, проверяющий, есть ли указанное слово в папке
        System.out.println();
        String str = MyFile.findWordInFolder(".", word);
        if (str == null || str.isEmpty())
            System.out.println("Слово '" + word +"' в папке не найдено");
        else
            System.out.println( "Слово '" + word +"' найдено в следующих файлах:\n" + str );;
    }
}
