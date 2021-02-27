package J2.lesson8;

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


    private boolean connected;
    private boolean authorized = false;

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
                        switch (msg.command) {
                            case Message.MESSAGE_CLIENT_QUITS:
                                connected = false;
                                break;
                            case Message.MESSAGE_CLIENTS_LIST:
                                raiseEvent(onListReceived, msg.message);
                                break;
                            case Message.MESSAGE_PRIVATE:
                                raiseEvent(onMessageReceived, "<private>" + msg.name + " " + msg.message);
                                break;
                            case Message.MESSAGE_AUTHORIZE:
                                authorized = true;
                                raiseEvent(onAuth, msg.name);
                                break;
                            case Message.MESSAGE_TIMEOUT:
                                connected = false;
                                raiseEvent(onError, msg.message);
                                break;
                            default:
                                if (authorized)
                                    raiseEvent(onMessageReceived, msg.message);
                                else
                                    raiseEvent(onError, msg.message);
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

    private void raiseEvent(ClientEvents<String> event, String message) {
        raiseEvent(event, message, false);
    }

    private void raiseEvent(ClientEvents<String> event, String message, boolean error) {
        String eventName;
        if (event == onError) eventName = "onError";
        else if (event == onListReceived) eventName = "onListReceived";
        else if (event == onMessageReceived) eventName = "onMessageReceived";
        else if (event == onAuth) eventName = "onAuth";
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
}
