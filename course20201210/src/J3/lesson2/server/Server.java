package J3.lesson2.server;

import J3.lesson2.client.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    public static final int SERVER_PORT = 5000;

    private int clientID = 0;
    private boolean serverWorks = true;

    private ServerSocket serverSocket;

    private final List<ClientHandler> clients = new ArrayList<>();

    private List<String> logins = new ArrayList<>();

    private AuthorizationCheck checkFromDB;

    public Server() {
        startServer();
    }

    private void startServer() {
        try {
            try {
                checkFromDB = new MySQLConnection();
                System.out.println("MySQL connected");
            } catch (SQLException throwable) {
                try {
                    System.out.println("MySQL is not accessible... trying SQLite...");
                    checkFromDB = SQLiteConnection.getInstance();
                    System.out.println("SQLite connected");
                } catch (SQLException throwable2) {
                    System.out.println("DB is not accessible: " + throwable2.getMessage());
                    System.out.println("using dummy server");
                    checkFromDB = new DummyAuthCheck();
                }
            }
            startScanner();

            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Waiting for clients...");
            while (serverWorks) {

                Socket client = serverSocket.accept();
                new ClientHandler(client, "Client" + ++clientID, this);
            }

            System.out.println("server is shutting down...");

            checkFromDB.close();

        } catch (IOException e) {
            System.err.println("connection error: " + e.getMessage());
        }
    }

    private void startScanner() {
        Thread scannerThread = new Thread(() -> {

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                if (message.startsWith(Message.MESSAGE_CLIENT_QUITS)) {
                    sendAll(message);
                    serverWorks = false;
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    sendAll("Server: " + message);
            }
        });
        scannerThread.setDaemon(true);
        scannerThread.start();
    }

    public void sendAll(String message) {
        if (serverWorks)
            for (ClientHandler client : clients)
                client.sendMessage(message);
    }

    public void sendPrivate(String message, ClientHandler receiver) {
        if (receiver == null)
            return;

        receiver.sendMessage(message);
    }

    public boolean subscribe(ClientHandler client) {
        if (logins.contains(client.getLogin()))
            return false;

        logins.add(client.getLogin());

        clients.add(client);
        sendListOfClients();

        System.out.println(client.getName() + " subscribed");

        sendAll("Server: " + client.getName() + " connected");

        return true;
    }

    public void unsubscribe(ClientHandler client) {
        logins.remove(client.getLogin());

        clients.remove(client);
        sendListOfClients();

        System.out.println(client.getName() + " unsubscribed");
        sendAll("Server: " + client.getName() + " disconnected");
    }

    private void sendListOfClients() {
        StringBuilder sb = new StringBuilder();
        sb.append(Message.MESSAGE_CLIENTS_LIST).append(" ");
        for (ClientHandler client : clients) {
            sb.append(client.getName());
            sb.append(" ");
        }
        sendAll(sb.toString());
    }

    public ClientHandler getClientByName(String name) {
        for (ClientHandler client : clients)
            if (client.getName().equalsIgnoreCase(name))
                return client;
        return null;
    }

    public String checkAuthorization(String login, String password) {
        if (checkFromDB != null)
            return checkFromDB.checkAuthorization(login, password);

        return null;
    }

    public boolean isServerWorks() {
        return serverWorks;
    }

    public void informNickChanged(ClientHandler client, String oldName) {
        sendListOfClients();
        sendAll("Server: user <" + oldName + "> changed nick to <" + client.getName() + ">");

        if (checkFromDB != null)
            checkFromDB.changeNickname(client.getLogin(), client.getName());
    }
}
