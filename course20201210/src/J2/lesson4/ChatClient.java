package J2.lesson4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatClient extends JFrame {
    private JPanel textAreaPanel;
    private JPanel inputTextPanel;

    private JTextArea textArea;

    public static void main(String[] args) {
        new ChatClient();
    }

    public ChatClient() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(640, 480);

        setLayout(new FlowLayout());
        add(getTextAreaPanel());
        add(getInputTextPanel());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                sayByeBye();
            }
        });
        setVisible(true);
    }

    private void sayByeBye() {
        System.out.println("Bye-bye");
    }

    private JPanel getTextAreaPanel() {
        textAreaPanel = new JPanel();
        textAreaPanel.setBackground(Color.GRAY);
        textAreaPanel.setBorder(BorderFactory.createTitledBorder("Messages"));
        textAreaPanel.setPreferredSize(new Dimension(600, 300));

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane textAreaScrollPane = new JScrollPane(textArea);
        textAreaScrollPane.setBackground(Color.GRAY);
        textAreaScrollPane.setPreferredSize(new Dimension(580, 270));

        textAreaPanel.add(textAreaScrollPane);

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

        return inputTextPanel;
    }

    private void sendMessage(String message) {
        if (!message.isEmpty())
            textArea.append(message + "\n");
    }
}
