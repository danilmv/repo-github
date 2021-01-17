package lesson7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GameMap extends JPanel {
    public static final int GAME_MODE_HVH = 0;
    public static final int GAME_MODE_HVA = 1;
    public static final int GAME_MODE_HVAA = 2; //team
    public static final int GAME_MODE_HVAVA = 3;

    public static final int GAME_SIZE_MIN = 3;
    public static final int GAME_SIZE_MAX = 8;

    private int mode = GAME_MODE_HVA;
    private int sizeX = 3;
    private int sizeY = 3;
    private int win = 3;

    private final int GAMESTATUS_INIT = 0;
    private final int GAMESTATUS_STARTED = 1;
    private final int GAMESTATUS_OVER_DRAW = 2;
    private final int GAMESTATUS_OVER = 3;

    private int gameStatus = GAMESTATUS_INIT;

    private final Color COLOR_BACKGROUND = Color.WHITE;
    private final Color COLOR_LINES = Color.DARK_GRAY;
    private final Color COLOR_GAMEOVER_BACK = Color.GRAY;
    private final Color COLOR_GAMEOVER_FONT = Color.ORANGE;
    private final Color COLOR_PLAYER_1 = new Color(150, 0, 000);
    private final Color COLOR_PLAYER_2 = new Color(00, 00, 200);
    private final Color COLOR_PLAYER_3 = new Color(00, 150, 000);

    private final Font FONT_GAMEOVER = new Font("Arial", Font.BOLD, 20);

    private int cellWidth;
    private int cellHeight;

    private final static int SYMBOL_PLAYER_1 = 1;
    private final static int SYMBOL_PLAYER_2 = 2;
    private final static int SYMBOL_PLAYER_3 = 3;
    private final static int SYMBOL_SPACE = 0;
    private Random rand = new Random();

    private int maxSize;
    private int[][] table;

    private static int currentPlayer;
    private static int currentTurn;
    private Player[] player;


    GameMap() {
        setBackground(COLOR_BACKGROUND);
        setLayout(new BorderLayout());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                update(e);
            }
        });
    }

    public void startGame(int mode, int sizeX, int sizeY, int win) {
        this.mode = mode;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.win = win;

        gameStatus = GAMESTATUS_STARTED;

        cellWidth = getWidth() / sizeX;
        cellHeight = getHeight() / sizeY;


        maxSize = Math.max(sizeX, sizeY);
        table = new int[maxSize][maxSize];
        initTable();

        switch (this.mode) {
            case GAME_MODE_HVA:
                player = new Player[2];
                player[0] = new Player("Красный", false, COLOR_PLAYER_1, SYMBOL_PLAYER_1);
                player[1] = new Player("Синий", true, COLOR_PLAYER_2, SYMBOL_PLAYER_2);
                break;
            case GAME_MODE_HVH:
                player = new Player[2];
                player[0] = new Player("Красный", false, COLOR_PLAYER_1, SYMBOL_PLAYER_1);
                player[1] = new Player("Синий", false, COLOR_PLAYER_2, SYMBOL_PLAYER_2);
                break;
            case GAME_MODE_HVAA:
            case GAME_MODE_HVAVA:
                player = new Player[3];
                player[0] = new Player("Красный", false, COLOR_PLAYER_1, SYMBOL_PLAYER_1);
                player[1] = new Player("Синий", true, COLOR_PLAYER_2, SYMBOL_PLAYER_2);
                player[2] = new Player("Зеленый", true, COLOR_PLAYER_3, SYMBOL_PLAYER_3);
                break;
            default:
                throw new RuntimeException("Неизвестный режим игры: " + mode);
        }

        currentTurn = 0;
        currentPlayer = rand.nextInt(player.length);

        repaint();

        nextTurn();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        render(g);
    }

    private void update(MouseEvent e) {
        if (gameStatus != GAMESTATUS_STARTED)
            return;
        if (player[currentPlayer].isAI)
            return;

        int x = e.getX() / cellWidth;
        if (x > sizeX - 1) x = sizeX - 1;
        int y = e.getY() / cellHeight;
        if (y > sizeY - 1) y = sizeY - 1;

        System.out.println(x + " " + y);

        if (isEmpty(x, y) && isValid(x, y))
            makeMove(x, y);
    }

    private void render(Graphics g) {
        if (gameStatus == GAMESTATUS_INIT)
            return;

        g.setColor(COLOR_LINES);
        for (int x = 0; x <= getWidth(); x += cellWidth) {
            g.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y <= getHeight(); y += cellHeight) {
            g.drawLine(0, y, getWidth(), y);
        }

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int i = 0; i < player.length; i++)
                    if (table[x][y] == player[i].getSymbol()) {
                        g.setColor(player[i].getColor());
                        g.fillOval(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                        break;
                    }
            }
        }

        if (gameStatus == GAMESTATUS_OVER || gameStatus == GAMESTATUS_OVER_DRAW) {
            g.setColor(COLOR_GAMEOVER_BACK);
            g.fillRect(0, getHeight() / 2 - 15, getWidth(), 30);
            g.setColor(COLOR_GAMEOVER_FONT);
            g.setFont(FONT_GAMEOVER);
            if (gameStatus == GAMESTATUS_OVER_DRAW)
                g.drawString("Ничья", getWidth() / 2 - 30, getHeight() / 2 + 10);
            else {
                g.drawString("Конец игры, " + player[currentPlayer].getName() + " победил", getWidth() / 2 - 130, getHeight() / 2 + 10);
            }
        }
    }

    private void nextTurn() {
        if (checkWin(player[currentPlayer].getSymbol())) {
            gameStatus = GAMESTATUS_OVER;
            return;
        }
        if (currentTurn >= sizeY * sizeX) {
            gameStatus = GAMESTATUS_OVER_DRAW;
            return;
        }

        currentPlayer = (currentPlayer + 1) % player.length;
        if (player[currentPlayer].isAI())
            switch (mode) {
                case GAME_MODE_HVA:
                    aiTurnVsHuman();
                    break;
                case GAME_MODE_HVAA:
                    aiTurnVsHuman();
                    break;
                case GAME_MODE_HVAVA:
                    aiTurnSelf();
                    break;
            }
    }

    private void makeMove(int x, int y) {
        table[x][y] = player[currentPlayer].getSymbol();
        currentTurn++;

        repaint();

        nextTurn();
    }

    public void initTable() {
        for (int x = 0; x < table.length; x++)
            for (int y = 0; y < table[x].length; y++)
                table[x][y] = SYMBOL_SPACE;
    }

    public boolean isValid(int x, int y) {
        return (x >= 0 && x < sizeX && y >= 0 && y < sizeY);
    }

    public boolean isEmpty(int x, int y) {
        return table[x][y] == SYMBOL_SPACE;
    }

    public boolean checkWin(int symbol) {
        return checkWin(symbol, table);
    }

    public boolean checkWin(int symbol, int[][] checkTable) { //нужно сделать проверку только для последнего хода, а не всего поля
        for (int x = 0; x <= sizeX - win; x++)
            for (int y = 0; y <= sizeY - win; y++)
                //проверяем по квадратам... WIN_ROW x WIN_ROW
                if (checkPartWin(symbol, x, y, checkTable))
                    return true;
        return false;
    }

    public boolean checkPartWin(int symbol, int offX, int offY, int[][] table) {
        boolean result = false;
// проверяем столбцы
        for (int y = 0; y < win && !result; y++)
            for (int x = 0; x < win; x++)
                if (table[offX + x][offY + y] == symbol)
                    result = true;
                else {
                    result = false;
                    break;
                }
//проверяем строки
        for (int x = 0; x < win && !result; x++)
            for (int y = 0; y < win; y++)
                if (table[offX + x][offY + y] == symbol)
                    result = true;
                else {
                    result = false;
                    break;
                }
        if (result) return true;
//диагональ 1
        for (int x = 0; x < win; x++) {
            if (table[offX + x][offY + x] == symbol)
                result = true;
            else {
                result = false;
                break;
            }
        }
        if (result) return true;
//диагональ 2
        for (int x = 0; x < win; x++) {
            if (table[offX + x][offY + win - 1 - x] == symbol)
                result = true;
            else {
                result = false;
                break;
            }
        }

        return result;
    }

    public void aiTurn() {
        int[][] testTable;
        int mySymbol = player[currentPlayer].getSymbol();
        int[] index = new int[win];
        int step = 0;
        boolean emptyFound = false;
        long[][] wins = new long[sizeX * sizeY][win + 2];
        int[][] losts = new int[sizeX * sizeY][win + 1];
        long maxValue = 0;
        int maxItemsNum = -1;
        int[] maxItems = new int[sizeX * sizeY];
        boolean checkingWins = true;

        testTable = table.clone();
        index[0] = 0;
        for (int i = 0; i < wins.length; i++) wins[i][win + 1] = -1; //если останется -1, значит сюда нельзя сходить
        //подсчет сколько побед и поражений возможно, для каждого возможного хода
        //в упрощенном виде: делаем ход, и для этого хода считаем возможные поражения и победы за 2 (WIN_ROW-1) хода
        do {
            if (step == 0) {
                checkingWins = true;
                mySymbol = player[currentPlayer].getSymbol();
            }
            //делаем последовательно ходы в пустые ячейки
            emptyFound = false;
            for (; index[step] < sizeY * sizeX; index[step]++) {
                index[step] = fillNextEmpty(index[step], mySymbol, testTable);
                if (index[step] < sizeY * sizeX) {
                    emptyFound = true;
                    break;
                }
            }
            if (emptyFound) wins[index[0]][win + 1] = 0;
            //проверяем нет ли победителя
            if (checkWin(mySymbol, testTable))
                if (checkingWins)
                    wins[index[0]][step]++;
                else
                    losts[index[0]][step]++;
            //если ход сделали, то переходим на следующий уровень(ход)
            if (emptyFound) {
                if (step < win - 1)
                    //следующий перебор с начала
                    index[++step] = step > 1 ? index[step - 1] : 0;
                else
                    //если это уже последний уровень, то просто стираем предыдущий ход
                    clearLastStep(index[step]++, testTable);
            } else {//если все проверили, то возвращаемся на предыдущий уровень
                if (--step == 0 && checkingWins) {
                    //если вернулись к первому уровню, и это был поиск побед, то переходим на поиск поражений
                    checkingWins = !checkingWins;
                    mySymbol = SYMBOL_PLAYER_1;
                    index[++step] = 0;
                } else if (step >= 0) {
                    //иначе просто очищаем последний ход и проверяем следующую ячейку
                    clearLastStep(index[step]++, testTable);
                }
            }
        } while (step >= 0);//если на первом уровне все проверили, то выходим

        //теперь подсчет результатов
        for (int i = 0; i < wins.length; i++) {
            if (wins[i][win + 1] < 0) continue;
            for (int j = 0; j < win; j++) {
                wins[i][win] += wins[i][j] * Math.pow(50, win - j - 0.5);
                wins[i][win] -= losts[i][j] * Math.pow(50, win - j);
            }
            if (maxValue < wins[i][win] || maxItemsNum == -1) {
                maxValue = wins[i][win];
                maxItemsNum = 0;
                maxItems[maxItemsNum++] = i;
            } else if (maxValue == wins[i][win]) {
                maxItems[maxItemsNum++] = i;
            }
        }
        //если максимумов несколько, то выберем случайный из них
        maxItemsNum = rand.nextInt(maxItemsNum);
        //table[maxItems[maxItemsNum] / sizeY][maxItems[maxItemsNum] % sizeY] = SYMBOL_PLAYER_2;
        makeMove(maxItems[maxItemsNum] / sizeY, maxItems[maxItemsNum] % sizeY);

// Random turn
//        int x,y;
//
//        do {
//            x = rand.nextInt(TABLE_WIDTH);
//            y = rand.nextInt(TABLE_HEIGHT);
//        }while (!isEmpty(x,y));
//        table[x][y] = SYMBOL_0;
    }

    public void aiTurnSelf() { //пытается остановить всех
        int[][] testTable;
        int checkPlayer = currentPlayer;
        int checkSymbol = player[checkPlayer].getSymbol();
        boolean checkedAll = false;
        int[] index = new int[win];
        int step = 0;
        boolean emptyFound = false;
        long[][] wins = new long[sizeX * sizeY][win + 2];
        int[][] losts = new int[sizeX * sizeY][win + 1];
        long maxValue = 0;
        int maxItemsNum = -1;
        int[] maxItems = new int[sizeX * sizeY];
        boolean checkingWins = true;

        testTable = table.clone();
        index[0] = 0;
        for (int i = 0; i < wins.length; i++) wins[i][win + 1] = -1; //если останется -1, значит сюда нельзя сходить
        //подсчет сколько побед и поражений возможно, для каждого возможного хода
        //в упрощенном виде: делаем ход, и для этого хода считаем возможные поражения и победы за 2 (WIN_ROW-1) хода
        do {
            //делаем последовательно ходы в пустые ячейки
            emptyFound = false;
            for (; index[step] < sizeY * sizeX; index[step]++) {
                index[step] = fillNextEmpty(index[step], checkSymbol, testTable);
                if (index[step] < sizeY * sizeX) {
                    emptyFound = true;
                    break;
                }
            }
            if (emptyFound) {
                wins[index[0]][win + 1] = 0;
                //проверяем нет ли победителя
                if (checkWin(checkSymbol, testTable)) {
                    if (checkPlayer == currentPlayer)
                        wins[index[0]][step]++;
                    else
                        losts[index[0]][step]++;

                    clearLastStep(index[step]++, testTable);
                }
                //если ход сделали и нет победы, то переходим на следующий уровень(ход)
                else {
                    if (step < win - 1)
                        //следующий перебор с начала
                        index[++step] = step > 1 ? index[step - 1] + 1 : 0;
                    else
                        //если это уже последний уровень, то просто стираем предыдущий ход
                        clearLastStep(index[step]++, testTable);
                }
            } else {//если все проверили, то возвращаемся на предыдущий уровень
                if (--step < 0 && !checkedAll) {
                    clearLastStep(index[++step]++, testTable);
                    //если вернулись к первому уровню, то переходим на поиск поражений - проверка других игроков
                    checkPlayer = (checkPlayer + 1) % player.length;
                    if (checkPlayer == currentPlayer) { //проверили всех
                        checkedAll = true;
                    } else {
                        checkSymbol = player[checkPlayer].getSymbol();
                        index[step] = 0;
                    }
                } else if (step >= 0) {
                    //иначе просто очищаем последний ход и проверяем следующую ячейку
                    clearLastStep(index[step]++, testTable);
                }
            }
        } while (step >= 0);//если на первом уровне все проверили, то выходим

        int x = 0;
        //теперь подсчет результатов
        for (int i = 0; i < wins.length; i++) {
            if (wins[i][win + 1] < 0) continue;
            for (int j = 0; j < win; j++) {
                wins[i][win] += wins[i][j] * Math.pow(50, win - j) * 2;
//                wins[i][win] += wins[i][j] * Math.pow(50, win - j - 0.5);
//                wins[i][win] -= losts[i][j] * Math.pow(50, win - j);
                wins[i][win] += losts[i][j] * Math.pow(50, win - j);
            }
            if (maxValue < wins[i][win] || maxItemsNum == -1) {
                maxValue = wins[i][win];
                maxItemsNum = 0;
                maxItems[maxItemsNum++] = i;
            } else if (maxValue == wins[i][win]) {
                maxItems[maxItemsNum++] = i;
            }
        }
        //если максимумов несколько, то выберем случайный из них
        maxItemsNum = rand.nextInt(maxItemsNum);
        //table[maxItems[maxItemsNum] / sizeY][maxItems[maxItemsNum] % sizeY] = SYMBOL_PLAYER_2;
        makeMove(maxItems[maxItemsNum] / sizeY, maxItems[maxItemsNum] % sizeY);
    }

    public void aiTurnVsHuman() {
        int[][] testTable;
        int checkPlayer = currentPlayer;
        int checkSymbol = player[checkPlayer].getSymbol();
        boolean checkedAll = false;
        int[] index = new int[win];
        int step = 0;
        boolean emptyFound = false;
        long[][] wins = new long[sizeX * sizeY][win + 2];
        int[][] losts = new int[sizeX * sizeY][win + 1];
        long maxValue = 0;
        int maxItemsNum = -1;
        int[] maxItems = new int[sizeX * sizeY];
        boolean checkingWins = true;

        testTable = table.clone();
        index[0] = 0;
        for (int i = 0; i < wins.length; i++) wins[i][win + 1] = -1; //если останется -1, значит сюда нельзя сходить
        //подсчет сколько побед и поражений возможно, для каждого возможного хода
        //в упрощенном виде: делаем ход, и для этого хода считаем возможные поражения и победы за 2 (WIN_ROW-1) хода
        do {
            //делаем последовательно ходы в пустые ячейки
            emptyFound = false;
            for (; index[step] < sizeY * sizeX; index[step]++) {
                index[step] = fillNextEmpty(index[step], checkSymbol, testTable);
                if (index[step] < sizeY * sizeX) {
                    emptyFound = true;
                    break;
                }
            }
            if (emptyFound) {
                wins[index[0]][win + 1] = 0;
                //проверяем нет ли победителя
                if (checkWin(checkSymbol, testTable)) {
                    if (checkPlayer == currentPlayer)
                        wins[index[0]][step]++;
                    else
                        losts[index[0]][step]++;

                    clearLastStep(index[step]++, testTable);
                }
                //если ход сделали и нет победы, то переходим на следующий уровень(ход)
                else {
                    if (step < win - 1)
                        //следующий перебор с начала
                        index[++step] = step > 1 ? index[step - 1] + 1 : 0;
                    else
                        //если это уже последний уровень, то просто стираем предыдущий ход
                        clearLastStep(index[step]++, testTable);
                }
            } else {//если все проверили, то возвращаемся на предыдущий уровень
                if (--step < 0 && !checkedAll) {
                    clearLastStep(index[++step]++, testTable);
                    //если вернулись к первому уровню, то переходим на поиск поражений - проверка других игроков
                    do {
                        checkPlayer = (checkPlayer + 1) % player.length;
                        if (checkPlayer == currentPlayer) { //проверили всех
                            checkedAll = true;
                            break;
                        } else {
                            checkSymbol = player[checkPlayer].getSymbol();
                            index[step] = 0;
                        }
                    } while (player[checkPlayer].isAI()); //AI не проверяем
                } else if (step >= 0) {
                    //иначе просто очищаем последний ход и проверяем следующую ячейку
                    clearLastStep(index[step]++, testTable);
                }
            }
        } while (step >= 0);//если на первом уровне все проверили, то выходим

        int x = 0;
        //теперь подсчет результатов
        for (int i = 0; i < wins.length; i++) {
            if (wins[i][win + 1] < 0) continue;
            for (int j = 0; j < win; j++) {
                wins[i][win] += wins[i][j] * Math.pow(50, win - j) * 2;
//                wins[i][win] += wins[i][j] * Math.pow(50, win - j - 0.5);
//                wins[i][win] -= losts[i][j] * Math.pow(50, win - j);
                wins[i][win] += losts[i][j] * Math.pow(50, win - j);
            }
            if (maxValue < wins[i][win] || maxItemsNum == -1) {
                maxValue = wins[i][win];
                maxItemsNum = 0;
                maxItems[maxItemsNum++] = i;
            } else if (maxValue == wins[i][win]) {
                maxItems[maxItemsNum++] = i;
            }
        }
        //если максимумов несколько, то выберем случайный из них
        maxItemsNum = rand.nextInt(maxItemsNum);
        //table[maxItems[maxItemsNum] / sizeY][maxItems[maxItemsNum] % sizeY] = SYMBOL_PLAYER_2;
        makeMove(maxItems[maxItemsNum] / sizeY, maxItems[maxItemsNum] % sizeY);
    }

    public int fillNextEmpty(int from, int symbol, int[][] table) {
        int index = 0;
        for (int x = 0; x < sizeX; x++)
            for (int y = 0; y < sizeY; y++, index++) {
                if (index >= from && table[x][y] == SYMBOL_SPACE) {
                    table[x][y] = symbol;
                    return index;
                }
            }

        return index;
    }

    public void clearLastStep(int at, int[][] table) {
        if (at < sizeY * sizeX)
            table[at / sizeY][at % sizeY] = SYMBOL_SPACE;
    }

    class Player {
        private String name;
        private boolean isAI;
        private Color color;
        private int symbol;

        public Player(String name, boolean isAI, Color color, int symbol) {
            this.name = name;
            this.isAI = isAI;
            this.color = color;
            this.symbol = symbol;
        }

        public String getName() {
            return name;
        }

        public boolean isAI() {
            return isAI;
        }

        public Color getColor() {
            return color;
        }

        public int getSymbol() {
            return symbol;
        }
    }
}
