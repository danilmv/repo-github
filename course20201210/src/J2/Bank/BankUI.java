package J2.Bank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BankUI extends JFrame {
    private JPanel signInPanel;
    private JPanel accountPanel;
    private JPanel historyPanel;

    private JLabel accountOfLabel;
    private String accountOfString = "Account of ";
    private JLabel balanceLabel;
    private String balanceString = "Balance is: ";

    private JTextArea historyArea;

    private Bank bank = new Bank();
    private Account account;

    public BankUI() {
        setTitle("MyBank");
        setSize(640, 480);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                bank.saveClients();
            }
        });

        add(getButtonsPanel(), BorderLayout.SOUTH);

        JPanel screenPanel = new JPanel();

        screenPanel.setLayout(new FlowLayout());
        screenPanel.add(getSignInPanel());
        screenPanel.add(getAccountPanel());
        screenPanel.add(getHistoryPanel());
        add(screenPanel);

        setVisibleSignInPanel(true);

        setVisible(true);
    }

    private JPanel getSignInPanel() {
        signInPanel = new JPanel();
        signInPanel.setVisible(false);
        signInPanel.setBackground(Color.GRAY);

        signInPanel.setPreferredSize(new Dimension(200, 120));
        signInPanel.setBorder(BorderFactory.createTitledBorder("Sign in"));
        signInPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        signInPanel.add(new JLabel("Login: "));
        JTextField loginField = new JTextField();
        loginField.setPreferredSize(new Dimension(100, 25));
        signInPanel.add(loginField);

        signInPanel.add(new JLabel("Password: "));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(100, 25));
        signInPanel.add(passwordField);

        JButton buttonOk = new JButton("Ok");
        signInPanel.add(buttonOk);

        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                account = bank.getAccount(loginField.getText(), String.valueOf(passwordField.getPassword()));
                if (account != null) {
                    setVisibleSignInPanel(false);

                    setVisibleAccountPanel(true);
                    setVisibleHistoryPanel(true);

                    updateAccountPanel();
                }
            }
        });
        return signInPanel;
    }

    private void setVisibleSignInPanel(boolean visible) {
        signInPanel.setVisible(visible);
    }

    private JPanel getAccountPanel() {
        accountPanel = new JPanel();
        accountPanel.setVisible(false);
        accountPanel.setPreferredSize(new Dimension(600, 200));
        accountPanel.setBackground(Color.GRAY);
        accountPanel.setBorder(BorderFactory.createTitledBorder("Bank account"));
        accountPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel clientPanel = new JPanel();
        clientPanel.setPreferredSize(new Dimension(580, 30));
        accountOfLabel = new JLabel(accountOfString);
        clientPanel.add(accountOfLabel);
        accountPanel.add(clientPanel);

        JPanel balancePanel = new JPanel();
        balancePanel.setPreferredSize(new Dimension(580, 30));
        balancePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        balancePanel.setBackground(Color.GRAY);
        balanceLabel = new JLabel(balanceString);
        balancePanel.add(balanceLabel);
        accountPanel.add(balancePanel);

        JPanel changeAmount = new JPanel();
        changeAmount.setBorder(BorderFactory.createTitledBorder("Change balance"));
        changeAmount.setBackground(new Color(100, 100, 100));
        changeAmount.setPreferredSize(new Dimension(250, 100));
        JTextField amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(200, 30));
        changeAmount.add(amountField);
        JButton creditButton = new JButton("credit");
        creditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    account.credit(amount);
                    updateAccountPanel();
                } catch (NumberFormatException exc) {
                    amountField.setText("");
                } catch (ExcessiveAmountException | NegativeNumberException exc) {
                    alert(exc.getMessage());
                }
            }
        });
        JButton debitButton = new JButton("debit");
        debitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    account.debit(amount);
                    updateAccountPanel();
                } catch (NumberFormatException numberFormatException) {
                    amountField.setText("");
                } catch (NegativeNumberException exc) {
                    alert(exc.getMessage());
                }
            }
        });
        changeAmount.add(creditButton);
        changeAmount.add(debitButton);
        accountPanel.add(changeAmount);

        return accountPanel;
    }

    private void updateAccountPanel() {
        if (account == null)
            return;

        accountOfLabel.setText(accountOfString + account.getClientID());
        balanceLabel.setText(balanceString + account.getBalance());

        updateHistory();
    }

    private void setVisibleAccountPanel(boolean visible) {
        accountPanel.setVisible(visible);
    }

    private void alert(String message) {
        JOptionPane.showMessageDialog(this, message, "Impossible operation", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel getHistoryPanel() {
        historyPanel = new JPanel();
        historyPanel.setVisible(false);
        historyPanel.setPreferredSize(new Dimension(600, 200));
        historyPanel.setBackground(Color.GRAY);
        historyPanel.setBorder(BorderFactory.createTitledBorder("Account history"));
        historyPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane historyPane = new JScrollPane(historyArea);
        historyPane.setPreferredSize(new Dimension(580, 150));
        historyPane.setBackground(Color.GRAY);

        historyPanel.add(historyPane);

        return historyPanel;
    }

    private void setVisibleHistoryPanel(boolean visible) {
        historyPanel.setVisible(visible);
    }

    private void updateHistory() {
        if (historyPanel.isVisible())
            historyArea.setText(account.getHistory());
    }

    private JPanel getButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2));

        JButton closeButton = new JButton("close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (accountPanel.isVisible()){
                    setVisibleAccountPanel(false);
                    setVisibleHistoryPanel(false);

                    setVisibleSignInPanel(true);
                }
            }
        });
        JButton historyButton = new JButton("history");
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (accountPanel.isVisible())
                    setVisibleHistoryPanel(!historyPanel.isVisible());
            }
        });

        buttonsPanel.add(closeButton);
        buttonsPanel.add(historyButton);

        return buttonsPanel;
    }
}
