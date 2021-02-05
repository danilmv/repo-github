package J2.Bank;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Account implements Serializable {
    private double balance;
    private final double maxOverdraft;
    private final String clientID;
    private final String password;
    private List<String> history = new ArrayList<>();

    public Account(double balance, double maxOverdraft, String clientID, String password) {
        this.balance = balance;
        this.maxOverdraft = maxOverdraft;
        this.clientID = clientID;
        this.password = password;
    }

    public void credit(double sum) throws NegativeNumberException, ExcessiveAmountException {
        if (sum < 0)
            throw new NegativeNumberException("Amount must not be negative");
        if (sum > balance + maxOverdraft)
            throw new ExcessiveAmountException("You cannot credit more than " + (balance + maxOverdraft));


        history.add("Credit of " + balance + " by " + sum + ", at " + LocalDateTime.now());
        balance -= sum;
    }

    public void debit(double sum) throws NegativeNumberException {
        if (sum < 0)
            throw new NegativeNumberException("Amount must not be negative");

        history.add("Debit of " + balance + " by " + sum + ", at " + LocalDateTime.now());
        balance += sum;
    }

    public boolean checkPassword(String password){
        return this.password.equals(password);
    }

    public String getClientID() {
        return clientID;
    }

    public double getBalance() {
        return balance;
    }

    public String getHistory(){
        return history.stream().collect(Collectors.joining("\n"));
    }
}
