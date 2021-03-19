package J3.lesson2.client;

import J3.lesson2.server.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ClientGUI extends JFrame {
    private final int NUM_OF_ROWS_TO_SAVE = 100;
    private final String HISTORY_PATH = "src/J3/lesson2/client/history/";

    private JPanel textAreaPanel;
    private JPanel inputTextPanel;
    private JPanel listOfClientsPanel;
    private JPanel authPanel;
    private JPanel nickNamePanel;

    private JTextArea textArea;
    private JTextArea textClients;

    private JTextField oldNameField;

    private Client client;

    public ClientGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(640, 480);

        setLayout(new FlowLayout());
        add(getAuthPanel());
        add(getListOfClientsPanel());
        add(getTextAreaPanel());
        add(getInputTextPanel());
        add(getNickNamePanel());

        new Thread(() -> {
            client = new Client();
            setCallBacks();
            client.connectToServer();
        }).start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                sayByeBye();
            }
        });
        setVisible(true);
    }

    public void setCallBacks() {
        client.setOnMessageReceived((message) -> textArea.append(message + "\n"));
        client.setOnListReceived((message) -> textClients.setText(message));
        client.setOnError((message) -> {
            JOptionPane.showMessageDialog(this, message);
        });

        client.setOnAuth((message) -> {
            authPanel.setVisible(false);

            listOfClientsPanel.setVisible(true);
            textAreaPanel.setVisible(true);
            inputTextPanel.setVisible(true);

            loadHistory();

            this.setTitle(message);

        });

        client.setOnNickChangeAccepted(this::nickNameAccepted);
    }

    private void sayByeBye() {
        sendMessage(Message.MESSAGE_CLIENT_QUITS);

        saveHistory();
        System.out.println("Bye-bye");
    }

    private void saveHistory() {
        String[] rows = textArea.getText().split("\n");

        try {
            Files.createDirectories(Paths.get(HISTORY_PATH));
        } catch (IOException e) {
            System.err.println("saveHistory: " + e.getMessage());
        }

        Path path = Paths.get(HISTORY_PATH + client.getLogin() + ".txt");
        try {
            Files.write(path, "".getBytes(StandardCharsets.UTF_8));
            for (int i = Math.max(0, rows.length - NUM_OF_ROWS_TO_SAVE); i < rows.length; i++) {
                Files.write(path, rows[i].getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
                Files.write(path, "\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            }
            Files.write(path, (">>> history from:" +LocalDateTime.now() + "<<< \n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void loadHistory() {
        Path path = Paths.get(HISTORY_PATH + client.getLogin() + ".txt");
        try {
            List<String> rows = Files.readAllLines(path);
            for (String str : rows) {
                textArea.append(str);
                textArea.append("\n");
            }
        } catch (IOException e) {
            System.err.println("loadHistory: " + e.getMessage());
        }
    }

    private JPanel getAuthPanel() {
        authPanel = new JPanel();

        authPanel.setBackground(Color.GRAY);

        authPanel.setPreferredSize(new Dimension(200, 120));
        authPanel.setBorder(BorderFactory.createTitledBorder("Sign in"));
        authPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        authPanel.add(new JLabel("Login: "));
        JTextField loginField = new JTextField();
        loginField.setPreferredSize(new Dimension(100, 25));
        authPanel.add(loginField);

        authPanel.add(new JLabel("Password: "));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(100, 25));
        authPanel.add(passwordField);

        passwordField.addActionListener((e) -> sendMessage(Message.MESSAGE_AUTHORIZE + " " + loginField.getText() + " " + String.valueOf(passwordField.getPassword())));

        JButton buttonOk = new JButton("Ok");
        authPanel.add(buttonOk);

        buttonOk.addActionListener((e) -> sendMessage(Message.MESSAGE_AUTHORIZE + " " + loginField.getText() + " " + String.valueOf(passwordField.getPassword())));

        this.setTitle("Sign in...");

        return authPanel;
    }

    private JPanel getTextAreaPanel() {
        textAreaPanel = new JPanel();
        textAreaPanel.setBackground(Color.GRAY);
        textAreaPanel.setBorder(BorderFactory.createTitledBorder("Messages"));
        textAreaPanel.setPreferredSize(new Dimension(600, 300));

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        JScrollPane textAreaScrollPane = new JScrollPane(textArea);
        textAreaScrollPane.setBackground(Color.GRAY);
        textAreaScrollPane.setPreferredSize(new Dimension(580, 270));

        textAreaPanel.add(textAreaScrollPane);

        textAreaPanel.setVisible(false);
        return textAreaPanel;
    }

    private JPanel getInputTextPanel() {
        inputTextPanel = new JPanel();
        inputTextPanel.setBackground(Color.GRAY);
        inputTextPanel.setPreferredSize(new Dimension(600, 40));

        JLabel messageLabel = new JLabel("Message: ");
        inputTextPanel.add(messageLabel);

        JTextField messageField = new JTextField();
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(messageField.getText());
                messageField.setText("");
            }
        });
        messageField.setPreferredSize(new Dimension(440, 30));
        inputTextPanel.add(messageField);

        JButton buttonSend = new JButton("Send");
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(messageField.getText());
                messageField.setText("");
            }
        });
        inputTextPanel.add(buttonSend);

        inputTextPanel.setVisible(false);
        return inputTextPanel;
    }

    private JPanel getListOfClientsPanel() {
        listOfClientsPanel = new JPanel();
        listOfClientsPanel.setBackground(Color.GRAY);
        listOfClientsPanel.setPreferredSize(new Dimension(600, 40));
        listOfClientsPanel.setLayout(new FlowLayout());

        textClients = new JTextArea();
        textClients.setEditable(false);
        JScrollPane textClientsScrollPane = new JScrollPane(textClients, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textClientsScrollPane.setBackground(Color.GRAY);
        textClientsScrollPane.setPreferredSize(new Dimension(500, 30));

        listOfClientsPanel.add(textClientsScrollPane);

        JButton rename = new JButton("rename");
        rename.addActionListener((e) -> showChangeName());
        listOfClientsPanel.add(rename);

        listOfClientsPanel.setVisible(false);
        return listOfClientsPanel;
    }

    private void showChangeName() {
        listOfClientsPanel.setVisible(false);
        textAreaPanel.setVisible(false);
        inputTextPanel.setVisible(false);

        oldNameField.setText(client.getName());
        nickNamePanel.setVisible(true);
    }

    private void nickNameAccepted(String nickname) {
        listOfClientsPanel.setVisible(true);
        textAreaPanel.setVisible(true);
        inputTextPanel.setVisible(true);

        nickNamePanel.setVisible(false);

        this.setTitle(nickname);
    }

    private JPanel getNickNamePanel() {
        nickNamePanel = new JPanel();

        nickNamePanel.setBackground(Color.GRAY);

        nickNamePanel.setPreferredSize(new Dimension(230, 120));
        nickNamePanel.setBorder(BorderFactory.createTitledBorder("Changing nickname"));
        nickNamePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        nickNamePanel.add(new JLabel("Current name: "));
        oldNameField = new JTextField();
        oldNameField.setEditable(false);
        oldNameField.setPreferredSize(new Dimension(100, 25));
        nickNamePanel.add(oldNameField);

        nickNamePanel.add(new JLabel("New name: "));
        JTextField newNameField = new JTextField();
        newNameField.setPreferredSize(new Dimension(100, 25));
        nickNamePanel.add(newNameField);

        newNameField.addActionListener((e) -> sendMessage(Message.MESSAGE_CHANGE_NICK + " " + newNameField.getText()));

        JButton buttonOk = new JButton("Ok");
        nickNamePanel.add(buttonOk);

        buttonOk.addActionListener((e) -> sendMessage(Message.MESSAGE_CHANGE_NICK + " " + newNameField.getText()));

        nickNamePanel.setVisible(false);

        return nickNamePanel;
    }

    private void sendMessage(String message) {
        if (!message.isEmpty())
            client.sendMessage(message);
    }
}
