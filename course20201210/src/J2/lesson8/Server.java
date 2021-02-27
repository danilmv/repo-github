package J2.lesson8;

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

    private Thread mainThread;
    private ServerSocket serverSocket;

    private List<ClientHandler> clients = new ArrayList<>();

    private AuthorizationCheck checkFromDB;

    public Server() {
        startServer();
    }

    private void startServer() {
        try {
            try {
                checkFromDB = new MySQLConnection();
                System.out.println("DB connected");
            } catch (SQLException throwables) {
                System.out.println("DB is not accessible: " + throwables.getMessage());
                System.out.println("using dummy server");
                checkFromDB = new DummyAuthCheck();
            }

            mainThread = Thread.currentThread();
            startScanner();

            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Waiting for clients...");
            while (serverWorks) {

                Socket client = serverSocket.accept();
                new ClientHandler(client, "Client" + ++clientID, this);
            }

            System.out.println("server is shutting down...");

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

    public void subscribe(ClientHandler client) {
        clients.add(client);
        sendListOfClients();

        System.out.println(client.getName() + " subscribed");

        sendAll("Server: " + client.getName() + " connected");
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
        sendListOfClients();

        System.out.println(client.getName() + " unsubscribed");
        sendAll("Server: " + client.getName() + " disconnected");
    }

    private void sendListOfClients() {
        StringBuilder sb = new StringBuilder();
        sb.append(Message.MESSAGE_CLIENTS_LIST).append(" ");
        for (ClientHandler client : clients) {
            sb.append(client.getName() + " ");
        }
        sendAll(sb.toString());
    }

    public ClientHandler getClientByName(String name) {
        for (ClientHandler client : clients)
            if (client.getName().equalsIgnoreCase(name))
                return client;
        return null;
    }

    public boolean checkAuthorization(String login, String password){
        if (checkFromDB != null)
            return checkFromDB.checkAuthorization(login, password);

        return true;
    }
    public boolean isServerWorks(){
        return serverWorks;
    }
}
