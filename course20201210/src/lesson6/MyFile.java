package lesson6;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MyFile{

    public static void concatFiles(String[] readFileList, String writeFile) throws IOException {
        FileOutputStream fWrite = new FileOutputStream(writeFile);

        for(String file:readFileList) {
            FileInputStream fRead = new FileInputStream(file);
            Scanner scan = new Scanner(fRead);

            while (scan.hasNext())
                fWrite.write(scan.nextLine().getBytes(StandardCharsets.UTF_8));
            fWrite.write('\n');

            scan.close();
            fRead.close();
        }

        fWrite.close();
    }

    public static String findWord(String fileName, String word) throws IOException {
        String str = null;
        FileInputStream fRead = new FileInputStream(fileName);
        Scanner scan = new Scanner(fRead);
        while (scan.hasNextLine()) {
            str = scan.nextLine();
//          scan.findInLine(word))
            if (str.contains(word))
                break;
            else
                str = null;
        }
        scan.close();
        fRead.close();
        return str;
    }

    public static String findWordInFolder(String path, String word) {
        StringBuilder strBuild = new StringBuilder();
        findWordInFolder(path, word, strBuild);

        return strBuild.toString();
    }

    private static void findWordInFolder(String path, String word, StringBuilder strB){
        File folder = new File(path);
        File[] listOfFiles;
        String str = null;

        listOfFiles = null;
        if (folder == null)
            return;

        listOfFiles = folder.listFiles();
        if (listOfFiles == null)
            return;

        for (File file:listOfFiles){
            if (file.isFile()){
                try {
                    if (file.getName().endsWith(".txt") && findWord(file.toString(), word)!=null) {
                        strB.append(file.toString() + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                findWordInFolder(file.toString(), word, strB);
            }

        }

        return;
    }
}
