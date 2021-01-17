package lesson7;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Settings extends JDialog {
    private final MainWindow mainWindow;
    private final static int WINDOW_WIDTH = 250;
    private final static int WINDOW_HEIGHT = 380;

    private JRadioButton rbtnHVH;
    private JRadioButton rbtnHVA;
    private JRadioButton rbtnHVAA;
    private JRadioButton rbtnHVAVA;
    private JRadioButton rbtnAVA;
    private JRadioButton rbtnAVAVA;
    private JSlider sldSizeX;
    private int currentX = GameMap.GAME_SIZE_MIN;
    private JSlider sldSizeY;
    private int currentY = GameMap.GAME_SIZE_MIN;
    private JSlider sldWin;
    private int currentWin = GameMap.GAME_SIZE_MIN;
    private JCheckBox checkShowTurn;

    Settings(MainWindow mainWindow){
        this.mainWindow = mainWindow;

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setTitle("Настройки");
        setModal(true);

        setLayout(new GridLayout(18,1));

        addFieldsMode();
        addFieldsSize();
        addFieldsOptions();

        JButton btnStart = new JButton("Начать игру");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        add(btnStart);
    }

    private void addFieldsMode(){
        JPanel pnlTitle = new JPanel();
        pnlTitle.setBackground(Color.GRAY);
        JLabel lblTitle = new JLabel("Режим игры");
        lblTitle.setForeground(Color.WHITE);
        pnlTitle.add(lblTitle, BorderLayout.CENTER);
        add(pnlTitle);

        rbtnHVH = new JRadioButton("Человек vs человек", false);
        rbtnHVA = new JRadioButton("Человек vs компьютер", true);
        rbtnHVAA = new JRadioButton("Человек vs компьютер + компьютер", false);
        rbtnHVAVA = new JRadioButton("Человек vs компьютер vs компьютер", false);
        rbtnAVA = new JRadioButton("Компьютер vs компьютер", false);
        rbtnAVAVA = new JRadioButton("Компьютер vs компьютер vs компьютер", false);
        ButtonGroup bGroup = new ButtonGroup();
        bGroup.add(rbtnHVH);
        bGroup.add(rbtnHVA);
        bGroup.add(rbtnHVAA);
        bGroup.add(rbtnHVAVA);
        bGroup.add(rbtnAVA);
        bGroup.add(rbtnAVAVA);
        add(rbtnHVH);
        add(rbtnHVA);
        add(rbtnHVAA);
        add(rbtnHVAVA);
        add(rbtnAVA);
        add(rbtnAVAVA);
    }

    private void addFieldsSize(){
        final String strWidth = "Ширина: ";
        final String strHeight = "Высота: ";
        final String strWin = "Выигрышная длина: ";

        JPanel pnlTitle = new JPanel();
        pnlTitle.setBackground(Color.GRAY);
        JLabel lblTitle = new JLabel("Размер поля");
        lblTitle.setForeground(Color.WHITE);
        pnlTitle.add(lblTitle, BorderLayout.CENTER);
        add(pnlTitle);


        JLabel lblSizeX = new JLabel(strWidth + currentX);
        sldSizeX = new JSlider(GameMap.GAME_SIZE_MIN, GameMap.GAME_SIZE_MAX, currentX);
        sldSizeX.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentX = sldSizeX.getValue();
                lblSizeX.setText(strWidth + currentX);
                sldWin.setMaximum(Math.min(currentX, currentY));
            }
        });
        add(lblSizeX);
        add(sldSizeX);

        JLabel lblSizeY = new JLabel(strHeight + currentY);
        sldSizeY = new JSlider(GameMap.GAME_SIZE_MIN, GameMap.GAME_SIZE_MAX, currentY);
        sldSizeY.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentY = sldSizeY.getValue();
                lblSizeY.setText(strHeight + currentY);
                sldWin.setMaximum(Math.min(currentX, currentY));
            }
        });
        add(lblSizeY);
        add(sldSizeY);


        pnlTitle = new JPanel();
        pnlTitle.setBackground(Color.GRAY);
        lblTitle = new JLabel("Условие победы");
        lblTitle.setForeground(Color.WHITE);
        pnlTitle.add(lblTitle, BorderLayout.CENTER);
        add(pnlTitle);

        JLabel lblWin = new JLabel(strWin + currentWin);
        sldWin = new JSlider(GameMap.GAME_SIZE_MIN, GameMap.GAME_SIZE_MIN, currentWin);
        sldWin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentWin = sldWin.getValue();
                lblWin.setText(strWin + currentWin);
            }
        });
        add(lblWin);
        add(sldWin);
    }

    private void addFieldsOptions(){
        JPanel pnlTitle = new JPanel();
        pnlTitle.setBackground(Color.GRAY);
        JLabel lblTitle = new JLabel("Опции");
        lblTitle.setForeground(Color.WHITE);
        pnlTitle.add(lblTitle, BorderLayout.CENTER);
        add(pnlTitle);

        checkShowTurn = new JCheckBox("Показывать ход", true);
        add(checkShowTurn);
    }

    public void showWindow(){
        setLocationRelativeTo(mainWindow);
        setVisible(true);
    }

    private void startGame(){
        setVisible(false);

        int currentMode;

        if (rbtnHVA.isSelected())
            currentMode = GameMap.GAME_MODE_HVA;
        else if (rbtnHVH.isSelected())
            currentMode = GameMap.GAME_MODE_HVH;
        else if (rbtnHVAA.isSelected())
            currentMode = GameMap.GAME_MODE_HVAA;
        else if (rbtnHVAVA.isSelected())
            currentMode = GameMap.GAME_MODE_HVAVA;
        else if (rbtnAVA.isSelected())
            currentMode = GameMap.GAME_MODE_AVA;
        else if (rbtnAVAVA.isSelected())
            currentMode = GameMap.GAME_MODE_AVAVA;
        else
            throw new RuntimeException("Неизвестный режим игры");


        mainWindow.startGame(currentMode, currentX, currentY, currentWin, checkShowTurn.isSelected());
    }
}
