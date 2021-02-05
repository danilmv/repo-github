package J1.lesson7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Random;

public class GameMap extends JPanel {
    public static final int GAME_MODE_HVH = 0;
    public static final int GAME_MODE_HVA = 1;
    public static final int GAME_MODE_HVAA = 2; //team
    public static final int GAME_MODE_HVAVA = 3;
    public static final int GAME_MODE_AVA = 4;
    public static final int GAME_MODE_AVAVA = 5;

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
    private final Color COLOR_SHOW_TURN = Color.WHITE;
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
    private int[][] turn;

    private static int currentPlayer;
    private static int currentTurn;
    private Player[] player;

    private boolean showTurn;

    private long time;
    private boolean onlyAI;
    private int lastMove = -1;


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

    public void startGame(int mode, int sizeX, int sizeY, int win, boolean showTurn) {
        this.mode = mode;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.win = win;
        this.showTurn = showTurn;

        gameStatus = GAMESTATUS_STARTED;

        cellWidth = getWidth() / sizeX;
        cellHeight = getHeight() / sizeY;


        maxSize = Math.max(sizeX, sizeY);
        table = new int[sizeX][sizeY];
        turn = new int[sizeX][sizeY];

        initTable();

        switch (this.mode) {
            case GAME_MODE_HVA:
                player = new Player[2];
                player[0] = new Player("Красный", false, COLOR_PLAYER_1, SYMBOL_PLAYER_1);
                player[1] = new Player("Синий", true, COLOR_PLAYER_2, SYMBOL_PLAYER_2, new AiTurnSelf());
                onlyAI = false;
                break;
            case GAME_MODE_HVH:
                player = new Player[2];
                player[0] = new Player("Красный", false, COLOR_PLAYER_1, SYMBOL_PLAYER_1);
                player[1] = new Player("Синий", false, COLOR_PLAYER_2, SYMBOL_PLAYER_2);
                onlyAI = false;
                break;
            case GAME_MODE_HVAA:
                player = new Player[3];
                player[0] = new Player("Красный", false, COLOR_PLAYER_1, SYMBOL_PLAYER_1);
                player[1] = new Player("Синий", true, COLOR_PLAYER_2, SYMBOL_PLAYER_2, new AiTurnVsHuman());
                player[2] = new Player("Зеленый", true, COLOR_PLAYER_3, SYMBOL_PLAYER_3, new AiTurnVsHuman());
                onlyAI = false;
                break;
            case GAME_MODE_HVAVA:
                player = new Player[3];
                player[0] = new Player("Красный", false, COLOR_PLAYER_1, SYMBOL_PLAYER_1);
                player[1] = new Player("Синий", true, COLOR_PLAYER_2, SYMBOL_PLAYER_2, new AiTurnSelf());
                player[2] = new Player("Зеленый", true, COLOR_PLAYER_3, SYMBOL_PLAYER_3, new AiTurnSelf());
                onlyAI = false;
                break;
            case GAME_MODE_AVA:
                player = new Player[2];
                player[0] = new Player("Красный", true, COLOR_PLAYER_1, SYMBOL_PLAYER_1, new AiTurnSelf());
                player[1] = new Player("Синий", true, COLOR_PLAYER_2, SYMBOL_PLAYER_2, new AiTurnSelf());
                onlyAI = true;
                break;
            case GAME_MODE_AVAVA:
                player = new Player[3];
                player[0] = new Player("Красный", true, COLOR_PLAYER_1, SYMBOL_PLAYER_1, new AiTurnSelf());
                player[1] = new Player("Синий", true, COLOR_PLAYER_2, SYMBOL_PLAYER_2, new AiTurnSelf());
                player[2] = new Player("Зеленый", true, COLOR_PLAYER_3, SYMBOL_PLAYER_3, new AiTurnSelf());
                onlyAI = true;
                break;
            default:
                throw new RuntimeException("Неизвестный режим игры: " + mode);
        }

        currentTurn = 0;
        currentPlayer = rand.nextInt(player.length);

        repaint();

        nextTurn(lastMove);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        render(g);

        if (onlyAI && gameStatus == GAMESTATUS_STARTED) {
            nextTurn(lastMove);
            repaint();
        }
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
                        if (showTurn) {
                            g.setColor(COLOR_SHOW_TURN);
                            g.setFont(FONT_GAMEOVER);
                            g.drawString("" + turn[x][y], x * cellWidth + cellWidth / 2, y * cellHeight + cellHeight / 2);
                        }
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

    private void nextTurn(int pos) {
        if (player == null)
            return;

        if (gameStatus != GAMESTATUS_STARTED)
            return;

        if (checkWinPos(player[currentPlayer].getSymbol(), pos)) {
            gameStatus = GAMESTATUS_OVER;
            return;
        }
        if (currentTurn >= sizeY * sizeX) {
            gameStatus = GAMESTATUS_OVER_DRAW;
            return;
        }

        if (!onlyAI || time + System.nanoTime() >= 1000000000) {
            currentPlayer = (currentPlayer + 1) % player.length;

            time = -System.nanoTime();
            player[currentPlayer].go();
        }
    }

    private void makeMove(int x, int y) {
        System.out.println("Turn №" + (currentTurn + 1) + " '" + player[currentPlayer].getSymbol() +
                "' moves to " + x + "; " + y);
        table[x][y] = player[currentPlayer].getSymbol();
        turn[x][y] = ++currentTurn;

        repaint();

        lastMove = getPos(x, y);
        nextTurn(lastMove);

    }

    private void initTable() {
        for (int x = 0; x < table.length; x++)
            for (int y = 0; y < table[x].length; y++) {
                table[x][y] = SYMBOL_SPACE;
                turn[x][y] = 0;
            }
    }

    private boolean isValid(int x, int y) {
        return (x >= 0 && x < sizeX && y >= 0 && y < sizeY);
    }

    private boolean isEmpty(int x, int y) {
        return table[x][y] == SYMBOL_SPACE;
    }

    private boolean checkWinPos(int symbol, int pos) {
        return checkWinPos(symbol, pos, table);
    }

    private boolean checkWinPos(int symbol, int pos, int[][] table) {
        int[] xV = {1, 1, 1, 0};
        int[] yV = {-1, 0, 1, 1};
        int x0 = getX(pos);
        int y0 = getY(pos);
        int x, y, streak;
        int direction = 1;

        for (int i = 0; i < xV.length; i++) {
            streak = 1; //количество совпадений
            do {
                direction = -direction; //проверяем в одну сторону, а затем в обратную...

                for (int move = 1; move < win; move++) {
                    x = x0 + xV[i] * move * direction;
                    y = y0 + yV[i] * move * direction;

                    if (x >= 0 && x < sizeX && y >= 0 && y < sizeY && table[x][y] == symbol)
                        streak++; //есть совпадение >> продолжаем
                    else
                        break; //нет совпадения >> выходим
                }
                if (streak >= win) {
//                    System.out.println("xV = " + xV[i] + " yV = " + yV[i] + " dir = " + direction +
//                            " x0 = " + x0 + " y0 = " + y0 + " streak = " + streak + " pos = " + pos);
//                    for (int j = 0; j < table.length; j++) {
//                        System.out.println("" + Arrays.toString(table[j]));
//                    }
                    return true;
                }
            } while (direction != 1);
        }

        return false;
    }

    private class AiTurn implements Runnable { //устарел, заменен на AiTurnVsHuman
        public void run() {
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
                if (checkWinPos(mySymbol, index[step], testTable))
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
            makeMove(getX(maxItems[maxItemsNum]), getY(maxItems[maxItemsNum]));

// Random turn
//        int x,y;
//
//        do {
//            x = rand.nextInt(TABLE_WIDTH);
//            y = rand.nextInt(TABLE_HEIGHT);
//        }while (!isEmpty(x,y));
//        table[x][y] = SYMBOL_0;
        }
    }

    private int getX(int pos) {
        return pos / sizeY;
    }

    private int getY(int pos) {
        return pos % sizeY;
    }

    private int getPos(int x, int y) {
        return x * sizeY + y;
    }

    private class AiTurnSelf implements Runnable {
        public void run() { //пытается остановить всех
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
                    if (checkWinPos(checkSymbol, index[step], testTable)) {
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
            makeMove(getX(maxItems[maxItemsNum]), getY(maxItems[maxItemsNum]));
        }
    }

    private class AiTurnVsHuman implements Runnable {
        public void run() {
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
                    if (checkWinPos(checkSymbol, index[step], testTable)) {
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
            makeMove(getX(maxItems[maxItemsNum]), getY(maxItems[maxItemsNum]));
        }
    }

    private int fillNextEmpty(int from, int symbol, int[][] table) {
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

    private void clearLastStep(int at, int[][] table) {
        if (at < sizeY * sizeX)
            table[getX(at)][getY(at)] = SYMBOL_SPACE;
    }

    class Player {
        private String name;
        private boolean isAI;
        private Color color;
        private int symbol;
        private Runnable runnable;

        public Player(String name, boolean isAI, Color color, int symbol) {
            this(name, isAI, color, symbol, null);
        }

        public Player(String name, boolean isAI, Color color, int symbol, Runnable runnable) {
            this.name = name;
            this.isAI = isAI;
            this.color = color;
            this.symbol = symbol;
            this.runnable = runnable;

            if (isAI && runnable == null)
                throw new RuntimeException("Необходимо заполнить runnable");
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

        public void go() {

            if (runnable != null)
                runnable.run();
        }
    }
}
