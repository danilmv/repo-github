package J3.lesson2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class Server {
    public static final int SERVER_PORT = 5000;
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";

    private int clientID = 0;
    private boolean serverWorks = true;

    private ServerSocket serverSocket;

    private final List<ClientHandler> clients = new ArrayList<>();

    private List<String> logins = new ArrayList<>();

    private AuthorizationCheck checkFromDB;

    private ExecutorService clientExecutor;

    public Server() {
        LOGGER.setLevel(Level.ALL);
        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                String color;
                switch (record.getLevel().intValue()) {
                    case ClientHandler.CLIENT_AUTH_LEVEL:
                        color = ANSI_GREEN;
                        break;
                    default:
                        color = ANSI_RESET;
                        break;
                }
                return String.format("%s%s:\t%10s %20s : %s\n", color,
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE), record.getLevel(),
                        record.getSourceMethodName(), record.getMessage());
            }
        });
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(handler);
        startServer();
    }

    private void startServer() {
        try {
            try {
                checkFromDB = new MySQLConnection();
                LOGGER.config("MySQL connected");
            } catch (SQLException throwable) {
                try {
                    LOGGER.config("MySQL is not accessible... trying SQLite...");
                    checkFromDB = SQLiteConnection.getInstance();
                    LOGGER.config("SQLite connected");
                } catch (SQLException throwable2) {
                    LOGGER.warning("DB is not accessible: " + throwable2.getMessage());
                    LOGGER.config("using dummy server");
                    checkFromDB = new DummyAuthCheck();
                }
            }
            startScanner();

            clientExecutor = Executors.newCachedThreadPool();

            serverSocket = new ServerSocket(SERVER_PORT);
            LOGGER.info("Waiting for clients...");
            while (serverWorks) {

                Socket client = serverSocket.accept();
                clientExecutor.submit(new ClientHandler(client, "Client" + ++clientID, this));
            }

            LOGGER.info("server is shutting down...");

            checkFromDB.close();
            clientExecutor.shutdown();

        } catch (IOException e) {
            LOGGER.severe("connection error: " + e.getMessage());
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

        LOGGER.info(client.getName() + " subscribed");

        sendAll("Server: " + client.getName() + " connected");

        return true;
    }

    public void unsubscribe(ClientHandler client) {
        logins.remove(client.getLogin());

        clients.remove(client);
        sendListOfClients();

        LOGGER.info(client.getName() + " unsubscribed");
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

    public static Logger getLOGGER() {
        return LOGGER;
    }
}
