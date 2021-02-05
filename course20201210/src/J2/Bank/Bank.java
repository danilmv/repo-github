package J2.Bank;

import java.io.*;
import java.util.HashMap;
import java.util.Random;

public class Bank {
    private HashMap<String, Account> clients = new HashMap<>();
    private final Random random = new Random();
    private final String dataSource = "src/J2/Bank/bankData.txt";

    public Bank(){
        loadClients();
    }

    public void createAccount(String id, String password){
        clients.put(id, new Account(0, random.nextDouble()*100, id, password));
    }

    public void saveClients(){
        try(FileOutputStream fos = new FileOutputStream(new File(dataSource))){
            try(ObjectOutputStream oos = new ObjectOutputStream(fos)){
                oos.writeObject(clients);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadClients(){
        try(FileInputStream fis = new FileInputStream(new File(dataSource))) {
            try(ObjectInputStream ois = new ObjectInputStream(fis)){
                clients = (HashMap<String, Account>)ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Account getAccount(String id, String password){
        Account account = clients.get(id);
        if (account != null && account.getClientID().equals(id) && account.checkPassword(password))
            return account;

        return null;
    }
}
