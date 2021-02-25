package J2.lesson6.consoleChat;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private DataInputStream serverReader;
    private DataOutputStream serverWriter;

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        connectToServer();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", Server.SERVER_PORT);
            System.out.println("connected to server");
            serverReader = new DataInputStream(socket.getInputStream());
            serverWriter = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                Message msg = new Message();
                String serverMessage;
                try {
                    while (true) {
                        msg.parseMessage(serverReader.readUTF());
                        if (msg.command != null) {
                            if (msg.command.equals(Message.MESSAGE_CLIENT_QUITS)) {
                                serverWriter.writeUTF(msg.command);
                                break;
                            } else if (msg.command.equals(Message.MESSAGE_CLIENTS_LIST)) {
                                System.out.println("list of clients: " + msg.message);
                            } else if (msg.command.equals(Message.MESSAGE_PRIVATE)) {
                                System.out.println("<private>" + msg.name + " " + msg.message);
                            }
                        } else {
                            System.out.println(msg.message);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("connection error: " + e.getMessage());
                }
                System.out.println("we have disconnected");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            Thread scannerThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);

                while (true) {
                    String message = scanner.nextLine();
                    try {
                        serverWriter.writeUTF(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            scannerThread.setDaemon(true);
            scannerThread.start();

        } catch (IOException e) {
            System.err.println("connection error: " + e.getMessage());
        }
    }
}
