package J3.lesson2.client;

import J3.lesson2.server.Message;
import J3.lesson2.server.Server;
import javafx.concurrent.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class Client {
    private final int NUM_OF_ROWS_TO_SAVE = 100;
    private final String HISTORY_PATH = "src/J3/lesson2/client/history/";
    private final String HISTORY_MARK = ">>> history from: ";

    private DataInputStream serverReader;
    private DataOutputStream serverWriter;

    private ClientEvents<String> onMessageReceived;
    private ClientEvents<String> onListReceived;
    private ClientEvents<String> onError;
    private ClientEvents<String> onAuth;
    private ClientEvents<String> onNickChangeAccepted;


    private boolean connected;
    private boolean authorized = false;

    private String name;
    private String login;

    public static void main(String[] args) {
        new Client();
    }

    public Client(Boolean connect) {
        if (connect) connectToServer();
    }

    public Client() {
    }

    public void connectToServer() {
        try {
            Thread scannerThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);

                while (true) {
                    String input = scanner.nextLine();
                    if (authorized)
                        sendMessage(input);
                }
            });
            scannerThread.setDaemon(true);
            scannerThread.start();

            Socket socket = new Socket("localhost", Server.SERVER_PORT);
            connected = true;
            System.out.println("connected to server");
            serverReader = new DataInputStream(socket.getInputStream());
            serverWriter = new DataOutputStream(socket.getOutputStream());

            Message msg = new Message();
            try {
                while (connected) {
                    msg.parseMessage(serverReader.readUTF());
                    switch (msg.getCommand()) {
                        case Message.MESSAGE_CLIENT_QUITS:
                            connected = false;
                            break;
                        case Message.MESSAGE_CLIENTS_LIST:
                            raiseEvent(onListReceived, msg.getMessage());
                            break;
                        case Message.MESSAGE_PRIVATE:
                            raiseEvent(onMessageReceived, "<private>" + msg.getName() + " " + msg.getMessage());
                            break;
                        case Message.MESSAGE_AUTHORIZE:
                            authorized = true;
                            name = msg.getName();
                            login = msg.getMessage();
                            raiseEvent(onAuth, msg.getName());
                            break;
                        case Message.MESSAGE_TIMEOUT:
                            connected = false;
                            raiseEvent(onError, msg.getMessage());
                            break;
                        case Message.MESSAGE_CHANGE_NICK_OK:
                            name = msg.getName();
                            raiseEvent(onNickChangeAccepted, msg.getName());
                            break;
                        default:
                            if (authorized)
                                raiseEvent(onMessageReceived, msg.getMessage());
                            else
                                raiseEvent(onError, msg.getMessage());
                            break;
                    }
                }
            } catch (IOException e) {
                raiseEvent(onError, "connection error: " + e.getMessage(), true);
            }
            try {
                connected = false;
                socket.close();
                raiseEvent(onListReceived, "");
                raiseEvent(onMessageReceived, "we disconnected");
            } catch (IOException e) {
                raiseEvent(onError, e.getMessage(), true);
            }

        } catch (IOException e) {
            raiseEvent(onError, "connection error: " + e.getMessage(), true);
        }
    }

    public void sendMessage(String message) {
        try {
            if (connected)
                serverWriter.writeUTF(message);
        } catch (IOException e) {
            raiseEvent(onError, e.getMessage(), true);
        }
    }

    public void setOnMessageReceived(ClientEvents<String> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    public void setOnListReceived(ClientEvents<String> onListReceived) {
        this.onListReceived = onListReceived;
    }

    public void setOnError(ClientEvents<String> onError) {
        this.onError = onError;
    }

    public void setOnNickChangeAccepted(ClientEvents<String> onNickChangeAccepted) {
        this.onNickChangeAccepted = onNickChangeAccepted;
    }

    private void raiseEvent(ClientEvents<String> event, String message) {
        raiseEvent(event, message, false);
    }

    private void raiseEvent(ClientEvents<String> event, String message, boolean error) {
        String eventName;
        if (event == onError) eventName = "onError";
        else if (event == onListReceived) eventName = "onListReceived";
        else if (event == onMessageReceived) eventName = "onMessageReceived";
        else if (event == onAuth) eventName = "onAuth";
        else if (event == onNickChangeAccepted) eventName = "onNickChangeAccepted";
        else eventName = "";

        if (error)
            System.err.println(eventName + ": " + message);
        else
            System.out.println(eventName + ": " + message);

        if (event != null)
            event.action(message);
    }

    public void setOnAuth(ClientEvents<String> onAuth) {
        this.onAuth = onAuth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void saveHistory(String text) {
        if (login == null) return;

        String pattern = HISTORY_MARK + ".*<<<";
        if (text.isEmpty())return;
        String[] split = text.split(pattern);
        if (split.length <= 0 || split[split.length-1].matches("\\s")) return;

        try {
            Files.createDirectories(Paths.get(HISTORY_PATH));
        } catch (IOException e) {
            System.err.println("saveHistory: " + e.getMessage());
        }

        Path path = Paths.get(HISTORY_PATH + login + ".txt");
        try {
            Files.write(path, split[split.length-1].getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            Files.write(path, (HISTORY_MARK + LocalDateTime.now() + "<<<").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("saveHistory: " + e.getMessage());
        }
    }

    public String loadHistory() {
        StringBuilder sb = new StringBuilder();
        Path path = Paths.get(HISTORY_PATH + login + ".txt");
        try {
            List<String> rows = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (int i = Math.max(0, rows.size() - NUM_OF_ROWS_TO_SAVE); i < rows.size(); i++) {
                sb.append(rows.get(i));
                sb.append("\n");
            }
        } catch (IOException e) {
            System.err.println("loadHistory: " + e.getMessage());
        }

        return sb.toString();
    }
}
