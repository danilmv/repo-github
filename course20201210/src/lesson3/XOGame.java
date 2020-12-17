package lesson3;

import java.util.Random;
import java.util.Scanner;

public class XOGame {
    public final static int TABLE_WIDTH = 5;
    public final static int TABLE_HEIGHT = 5;
    public final static int WIN_ROW = 4;
    public final static char SYMBOL_X = 'X';
    public final static char SYMBOL_0 = '0';
    public final static char SYMBOL_SPACE = '_';
    public static Scanner scan = new Scanner(System.in);
    public static Random rand = new Random();

    public static char[][] table = new char[max(TABLE_WIDTH, TABLE_HEIGHT)][max(TABLE_WIDTH, TABLE_HEIGHT)];

    public static void main(String[] args) {
        boolean keepPlaying = false;
        int turn = 0;
        char symbol;

        do {
            turn = 1;
            initTable();
            showTable();

            do {
                if(turn%2 == 1) {
                    playerTurn();
                    symbol = SYMBOL_X;
                }
                else {
                    aiTurn();
                    symbol = SYMBOL_0;
                }
                showTable();

                if (checkWin(symbol)){
                    System.out.println(turn%2==1? "Поздравляю, вы победили!":"Компьютер победил!");
                    break;
                }
                if (turn >= TABLE_HEIGHT * TABLE_WIDTH){
                    System.out.printf("Ничья.");
                    break;
                }
                turn++;
            }while (true);

        }while (keepPlaying);
    }

    public static void initTable(){
        for( int x = 0; x<table.length; x++)
            for (int y = 0; y<table[x].length; y++)
                table[x][y] = SYMBOL_SPACE;
    }
    public static void showTable(){
        for( int x = 0; x<TABLE_WIDTH; x++) {
            for (int y = 0; y < TABLE_HEIGHT; y++)
                System.out.print(table[x][y] + "|");
            System.out.println();
        }
        System.out.println();
    }
    public static void playerTurn(){
        int x = 0, y =0;
        do {
            System.out.println("Введите координаты (строка столбец)");
            x = scan.nextInt() - 1;
            y = scan.nextInt() - 1;
        }while (!isValid(x,y) || !isEmpty(x,y));

        table[x][y] = SYMBOL_X;
    }

    public static boolean isValid(int x, int y){
        return (x >= 0 && x < TABLE_WIDTH && y >= 0 && y < TABLE_HEIGHT);
    }

    public static boolean isEmpty(int x, int y){
        return table[x][y] == SYMBOL_SPACE;
    }

    public static boolean checkWin(char symbol){
        for (int x = 0; x<= TABLE_WIDTH - WIN_ROW; x ++)
            for (int y = 0; y <= TABLE_HEIGHT - WIN_ROW; y++)
                if (checkPartWin(symbol, x ,y))
                    return true;
        return false;
    }

    public static boolean checkPartWin(char symbol, int offX, int offY){
        boolean result = false;

        for(int y = 0; y < WIN_ROW && !result; y++)
            for (int x = 0; x < WIN_ROW; x++)
                if (table[offX + x][offY + y] == symbol)
                    result = true;
                else {
                    result = false;
                    break;
                }

        for (int x = 0; x < WIN_ROW && !result; x++)
            for (int y = 0; y < WIN_ROW; y++)
                if (table[offX + x][offY + y] == symbol)
                    result = true;
                else {
                    result = false;
                    break;
                }
        if (result)return true;

        for (int x = 0; x < WIN_ROW; x++){
            if (table[offX + x][offY + x] == symbol)
                result = true;
            else {
                result = false;
                break;
            }
        }
        if (result)return true;

        for (int x = 0; x < WIN_ROW; x++){
            if (table[offX + x][offY + WIN_ROW - 1 - x] == symbol)
                result = true;
            else {
                result = false;
                break;
            }
        }

        return result;
    }

    public static void aiTurn(){
        int x,y;

        do {
            x = rand.nextInt(TABLE_WIDTH);
            y = rand.nextInt(TABLE_HEIGHT);
        }while (!isEmpty(x,y));
        table[x][y] = SYMBOL_0;
    }

    public static int max(int x, int y){
        return x>y?x:y;
    }
}
