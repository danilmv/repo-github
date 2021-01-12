package lesson7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameMap extends JPanel {
    public static final int GAME_MODE_HVH = 0;
    public static final int GAME_MODE_HVA = 1;

    public static final int GAME_SIZE_MIN = 3;
    public static final int GAME_SIZE_MAX = 8;

    private final Handler handler = new Handler();

    private int mode = GAME_MODE_HVA;
    private int sizeX = 3;
    private int sizeY = 3;
    private int win = 3;


    GameMap(){
        setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout());
    }

    public void startGame(int mode, int sizeX, int sizeY, int win){
        this.mode = mode;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.win = win;

        System.out.println("Новая игра: mode = " + mode +
                            " sizeX = " + sizeX +
                            " sizeY = " + sizeY +
                            " win = " + win );

        removeAll();
        revalidate();
        setLayout(new GridLayout(sizeY, sizeX));

        JButton btn;
        for (int i = 0; i < sizeX * sizeY; i++) {
            btn = new JButton();
            btn.setActionCommand("" + i);
            btn.addActionListener(handler);
            add(btn);
        }
    }

    private class Handler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            int num = Integer.parseInt(e.getActionCommand());
            int x = num % sizeX;
            int y = num / sizeX;

            System.out.println("Нажата кнопка поля " + num +
                                " x = " + x +
                                " y = " + y );

        }
    }
}
