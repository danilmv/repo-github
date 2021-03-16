package J3.lesson2.client;

import J3.lesson2.server.Message;
import J3.lesson2.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
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
            Socket socket = new Socket("localhost", Server.SERVER_PORT);
            connected = true;
            System.out.println("connected to server");
            serverReader = new DataInputStream(socket.getInputStream());
            serverWriter = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
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
            }).start();

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
}
