package J1.lesson7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 555;
    private Settings settings = new Settings(this);
    private GameMap gameMap = new GameMap();
    MainWindow(){
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Главное окно");

        JPanel pnlButtons = new JPanel();
        pnlButtons.setLayout(new GridLayout(1, 2));

        JButton btnNewGame = new JButton("Новая игра");
        btnNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Нажали на кнопку новая игра");
                settings.showWindow();
            }
        });

        JButton btnExit = new JButton("Выход");
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Нажали на кнопку выход");
                System.exit(0);
            }
        });

        pnlButtons.add(btnNewGame);
        pnlButtons.add(btnExit);

        add(pnlButtons, BorderLayout.SOUTH);

        add(gameMap);

        setVisible(true);
    }

    public void startGame(int mode, int sizeX, int sizeY, int win, boolean showTurn){
        gameMap.startGame(mode, sizeX, sizeY, win, showTurn);
    }
}
