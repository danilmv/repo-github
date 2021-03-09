package J2.lesson8;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame {
    private JPanel textAreaPanel;
    private JPanel inputTextPanel;
    private JPanel listOfClientsPanel;
    private JPanel authPanel;

    private JTextArea textArea;
    private JTextArea textClients;

    private Client client;

    public ClientGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(640, 480);

        setLayout(new FlowLayout());
        add(getAuthPanel());
        add(getListOfClientsPanel());
        add(getTextAreaPanel());
        add(getInputTextPanel());

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

            this.setTitle(message);

        });
    }

    private void sayByeBye() {
        sendMessage(Message.MESSAGE_CLIENT_QUITS);
        System.out.println("Bye-bye");
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

        textClients = new JTextArea();
        textClients.setEditable(false);
        JScrollPane textClientsScrollPane = new JScrollPane(textClients, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textClientsScrollPane.setBackground(Color.GRAY);
        textClientsScrollPane.setPreferredSize(new Dimension(580, 30));

        listOfClientsPanel.add(textClientsScrollPane);

        listOfClientsPanel.setVisible(false);
        return listOfClientsPanel;
    }

    private void sendMessage(String message) {
        if (!message.isEmpty())
            client.sendMessage(message);
    }
}
