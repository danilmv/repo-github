package J2.lesson6.consoleChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private BufferedReader serverReader;
    private PrintWriter serverWriter;

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
            serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverWriter = new PrintWriter(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String serverMessage = serverReader.readLine();
                        if (serverMessage.equals(Server.CLIENT_QUITS)) {
                            serverWriter.println(serverMessage);
                            serverWriter.flush();
                            System.out.println("server is shutting down");
                            break;
                        }

                        System.out.println("we have received a message from the server: " + serverMessage);
                    }
                } catch (IOException e) {
                    System.err.println("connection error: " + e.getMessage());
                }
                System.out.println("we have disconnected");
                try {
                    serverWriter.close();
                    serverReader.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            Thread scannerThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);

                while (true) {
                    String message = scanner.nextLine();
                    serverWriter.println(message);
                    serverWriter.flush();
                }
            });
            scannerThread.setDaemon(true);
            scannerThread.start();

        } catch (IOException e) {
            System.err.println("connection error: " + e.getMessage());
        }
    }
}
