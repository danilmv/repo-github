package J2.lesson6.consoleChat;

//1. Написать консольный вариант клиент\серверного приложения, в котором пользователь может писать сообщения,
// как на клиентской стороне, так и на серверной. Т.е. если на клиентской стороне написать «Привет», нажать Enter,
// то сообщение должно передаться на сервер и там отпечататься в консоли. Если сделать то же самое на серверной стороне,
// то сообщение передается клиенту и печатается у него в консоли. Есть одна особенность, которую нужно учитывать:
// клиент или сервер может написать несколько сообщений подряд. Такую ситуацию необходимо корректно обработать.

//2. *Реализовать личные сообщения так: если клиент пишет «/w nick3 Привет», то только клиенту с ником nick3 должно
// прийти сообщение «Привет».

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        startServer();
    }

    private void startServer() {
        try {
            mainThread = Thread.currentThread();
            startScanner();

            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Waiting for a clint...");
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

        System.out.println(client.getName() + " connected");
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
        sendListOfClients();

        System.out.println(client.getName() + " disconnected");
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
}
