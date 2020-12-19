package lesson3;

import java.util.Random;
import java.util.Scanner;

public class XOGame {
    public final static int TABLE_X = 5;
    public final static int TABLE_Y = 5;
    public final static int WIN_ROW = 4;
    public final static char SYMBOL_X = 'X';
    public final static char SYMBOL_0 = '0';
    public final static char SYMBOL_SPACE = '_';
    public static Scanner scan = new Scanner(System.in);
    public static Random rand = new Random();

    public static int maxSize = max(TABLE_X, TABLE_Y);
    public static char[][] table = new char[maxSize][maxSize];

    public static int currentTurn = 0;

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

    public static void initTable(){
        for( int x = 0; x < TABLE_X; x++)
            for (int y = 0; y < TABLE_Y; y++)
                table[x][y] = SYMBOL_SPACE;
    }
    public static void showTable(){
        System.out.print("  ");
        for(int i = 0; i< TABLE_Y; i++)
            System.out.print("" + (i+1) + " ");
        System.out.println();
        for( int x = 0; x<TABLE_X; x++) {
            System.out.print(x+1 + " ");
            for (int y = 0; y < TABLE_Y; y++)
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
        return (x >= 0 && x < TABLE_X && y >= 0 && y < TABLE_Y);
    }

    public static boolean isEmpty(int x, int y){
        return table[x][y] == SYMBOL_SPACE;
    }

    public static boolean checkWin(char symbol){
        return checkWin(symbol, table);
    }
    public static boolean checkWin(char symbol, char[][] checkTable){
        for (int x = 0; x<= TABLE_X - WIN_ROW; x ++)
            for (int y = 0; y <= TABLE_Y - WIN_ROW; y++)
                //проверяем по квадратам... WIN_ROW x WIN_ROW
                if (checkPartWin(symbol, x ,y, checkTable))
                    return true;
        return false;
    }

    public static boolean checkPartWin(char symbol, int offX, int offY, char[][] table){
        boolean result = false;
// проверяем столбцы
        for(int y = 0; y < WIN_ROW && !result; y++)
            for (int x = 0; x < WIN_ROW; x++)
                if (table[offX + x][offY + y] == symbol)
                    result = true;
                else {
                    result = false;
                    break;
                }
//проверяем строки
        for (int x = 0; x < WIN_ROW && !result; x++)
            for (int y = 0; y < WIN_ROW; y++)
                if (table[offX + x][offY + y] == symbol)
                    result = true;
                else {
                    result = false;
                    break;
                }
        if (result)return true;
//диагональ 1
        for (int x = 0; x < WIN_ROW; x++){
            if (table[offX + x][offY + x] == symbol)
                result = true;
            else {
                result = false;
                break;
            }
        }
        if (result)return true;
//диагональ 2
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
        char [][] testTable = new char[maxSize][maxSize];
        char mySymbol = SYMBOL_0;
        int[] index = new int[WIN_ROW];
        int step = 0;
        boolean emptyFound = false;
        long[][] wins = new long[TABLE_X * TABLE_Y][WIN_ROW + 2];
        int[][] losts = new int[TABLE_X * TABLE_Y][WIN_ROW + 1];
        long maxValue = 0;
        int maxItemsNum = -1;
        int[] maxItems = new int[TABLE_X* TABLE_Y];
        boolean checkingWins = true;

        testTable = table.clone();
        index[0] = 0;
        for (int i = 0; i < wins.length; i++)wins[i][WIN_ROW+1] = -1; //если останется -1, значит сюда нельзя сходить
        //подсчет сколько побед и поражений возможно, для каждого возможного хода
        //в урощенном виде: делаем ход, и для этого хода считаем возможные поражения и победы за 2 (WIN_ROW-1) хода
        do {
            if (step == 0){
                checkingWins = true;
                mySymbol = SYMBOL_0;
            }
            //делаем последовательно ходы в пустые ячейки
            emptyFound = false;
            for (; index[step] < TABLE_Y * TABLE_X; index[step]++){
                index[step] = fillNextEmpty(index[step], mySymbol, testTable);
                if (index[step] < TABLE_Y * TABLE_X) {
                    emptyFound = true;
                    break;
                    }
                }
            if(emptyFound)wins[index[0]][WIN_ROW+1] = 0;
            //проверяем нет ли победителя
            if (checkWin(mySymbol, testTable))
                if(checkingWins)
                    wins[index[0]][step]++;
                else
                    losts[index[0]][step]++;
            //если ход сделали, то переходим на следующий уровень(ход)
            if (emptyFound) {
                if (step < WIN_ROW -1)
                    //следующий перебор с начала
                    index[++step] = step>1?index[step-1]:0;
                else
                    //если это уже последний уровень, то просто стираем предыдущий ход
                    clearLastStep(index[step]++, testTable);
            }else {//если все проверили, то возвращаемся на предыдущий уровень
                if(--step == 0 && checkingWins) {
                    //если вернулись к первому уровню, и это был поиск побед, то переходим на поиск поражений
                    checkingWins = !checkingWins;
                    mySymbol = SYMBOL_X;
                    index[++step] = 0;
                }else if(step >=0){
                    //иначе просто очищаем последний ход и проверяем следующую ячейку
                    clearLastStep(index[step]++, testTable);
                }
            }
        }while (step >= 0);//если на первом уровне все проверили, то выходим

        //теперь подсчет результатов
        for (int i = 0; i< wins.length; i++) {
            if(wins[i][WIN_ROW+1] < 0) continue;
            for (int j = 0; j < WIN_ROW; j++) {
                    wins[i][WIN_ROW] += wins[i][j] * Math.pow(50, WIN_ROW - j- 0.5);
                    wins[i][WIN_ROW] -= losts[i][j] * Math.pow(50, WIN_ROW - j);
            }
            if (maxValue < wins[i][WIN_ROW] || maxItemsNum == -1) {
                maxValue = wins[i][WIN_ROW];
                maxItemsNum = 0;
                maxItems[maxItemsNum++] = i;
            }else if(maxValue == wins[i][WIN_ROW]){
                maxItems[maxItemsNum++] = i;
            }
        }
        //если максимумов несколько, то выберем случайный из них
        maxItemsNum = rand.nextInt(maxItemsNum);
        table[maxItems[maxItemsNum]/TABLE_Y][maxItems[maxItemsNum]%TABLE_Y] = SYMBOL_0;

// Random turn
//        int x,y;
//
//        do {
//            x = rand.nextInt(TABLE_WIDTH);
//            y = rand.nextInt(TABLE_HEIGHT);
//        }while (!isEmpty(x,y));
//        table[x][y] = SYMBOL_0;


    }

    public static int max(int x, int y){
        return x>y?x:y;
    }

    public static int fillNextEmpty(int from, char symbol, char[][] table){
        int index = 0;
        for (int x = 0; x<TABLE_X; x++)
            for(int y = 0; y < TABLE_Y; y++, index++) {
                if (index >= from && table[x][y] == SYMBOL_SPACE) {
                    table[x][y] = symbol;
                    return index;
                }
            }

        return index;
    }

    public static void clearLastStep(int at, char[][]table){
        if (at < TABLE_Y * TABLE_X)
            table[at/TABLE_Y][at%TABLE_Y] = SYMBOL_SPACE;
    }
}
