package lesson3;

import java.util.Random;
import java.util.Scanner;

public class XOGame2 {
    public final static int TABLE_X = 5;
    public final static int TABLE_Y = 5;
    public final static int WIN_ROW = 4;

    public final static char SYMBOL_X = 'X';
    public final static char SYMBOL_0 = '0';
    public final static char SYMBOL_SPACE = '_';

    public static Scanner scan = new Scanner(System.in);
    public static Random rand = new Random();

    public static int currentTurn = 0;

    private static long table_x, table_0, table;

    public static void main(String[] args) {
        boolean keepPlaying = false;

        char symbol;
        int whoBegins;

        do {
            currentTurn = 1;
            initTable();
            showTable();

            whoBegins = rand.nextInt(2);

            do {
                if(currentTurn %2 == whoBegins ) {
                    playerTurn();
                    symbol = SYMBOL_X;
                }
                else {
                    aiTurn();
                    symbol = SYMBOL_0;
                }
                showTable();

                if (checkWin(symbol)){
                    System.out.println(symbol == SYMBOL_X? "Поздравляю, вы победили!":"Компьютер победил!");
                    break;
                }
                if (currentTurn >= TABLE_Y * TABLE_X){
                    System.out.println("Ничья.");
                    break;
                }
                currentTurn++;
            }while (true);

            System.out.println("Еще раз? (y)");
            if ( scan.next().toLowerCase().equals("y"))
                keepPlaying = true;
            else
                keepPlaying = false;

        }while (keepPlaying);
    }

    private static void initTable(){
        table = 0;
        table_x = 0;
        table_0 = 0;
    }

    private static void showTable(){
        System.out.print("  ");
        for(int i = 0; i< TABLE_Y; i++)
            System.out.print("" + (i+1) + " ");
        System.out.println();
        for( int x = 0; x<TABLE_X; x++) {
            System.out.print(x+1 + " ");
            for (int y = 0; y < TABLE_Y; y++)
                System.out.print(getTableValue(x,y)  + "|");
            System.out.println();
        }
        System.out.println();
    }

    private static long get2(int pow){
        return (long) Math.pow(2, pow);
    }

    private static int convertXY(int x, int y){
        return x*TABLE_Y + y;
    }

    private static char getTableValue(int x, int y){
        if ( (table_x & get2( convertXY(x,y)) ) != 0)
            return SYMBOL_X;
        if ((table_0 & get2( convertXY(x,y) )) != 0)
            return SYMBOL_0;
        return SYMBOL_SPACE;
    }

    private static void playerTurn(){
        int x = 0, y =0;
        do {
            System.out.println("Введите координаты (строка столбец)");
            x = scan.nextInt() - 1;
            y = scan.nextInt() - 1;
        }while (!isValid(x,y) || !isEmpty(x,y));

        table |= table_x |= get2(convertXY(x,y));
    }

    private static boolean isValid(int x,int y){
        return (x >= 0 && x < TABLE_X && y >= 0 && y < TABLE_Y);
    }

    private static boolean isEmpty(int x, int y){
        return (table & get2(convertXY(x,y))) == 0;
    }

    private static boolean checkWin(long table){
        return false;
    }

    public static boolean checkPartWin(long table, int offX, int offY) {
        return  false;
    }

    public static void aiTurn(){
        // Random turn
        int x,y;

        do {
            x = rand.nextInt(TABLE_X);
            y = rand.nextInt(TABLE_Y);
        }while (!isEmpty(x,y));
        table |= table_0 |= get2(convertXY(x,y));
    }
}
