package J2.lesson6.consoleChat;

//1. Написать консольный вариант клиент\серверного приложения, в котором пользователь может писать сообщения,
// как на клиентской стороне, так и на серверной. Т.е. если на клиентской стороне написать «Привет», нажать Enter,
// то сообщение должно передаться на сервер и там отпечататься в консоли. Если сделать то же самое на серверной стороне,
// то сообщение передается клиенту и печатается у него в консоли. Есть одна особенность, которую нужно учитывать:
// клиент или сервер может написать несколько сообщений подряд. Такую ситуацию необходимо корректно обработать.

//Разобраться с кодом с занятия: он является фундаментом проекта-чата
//*ВАЖНО! * Сервер общается только с одним клиентом, т.е. не нужно запускать цикл, который будет ожидать
// второго/третьего/n-го клиентов

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static final int SERVER_PORT = 5000;
    public static final String CLIENT_QUITS = "/buy-buy";

    private BufferedReader clientReader;
    private PrintWriter clientWriter;

    public static void main(String[] args) {
        new Server();
    }

    public Server(){
        startServer();
    }

    private void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Waiting for a clint...");
            Socket client = serverSocket.accept();
            clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            clientWriter = new PrintWriter(client.getOutputStream());
            System.out.println("Client has connected!");

            new Thread(() -> {
                try {
                    while (true) {
                        String clientMessage = clientReader.readLine();

                        if (clientMessage.equals(CLIENT_QUITS)) {
                            clientWriter.println(CLIENT_QUITS);
                            clientWriter.flush();
                            break;
                        }

                        System.out.println("Client writes: " + clientMessage);

                        clientWriter.println("Server has received your message: " + clientMessage);
                        clientWriter.flush();
                    }
                } catch (IOException e) {
                    System.err.println("connection error: " + e.getMessage());
                }
                System.out.println("Client disconnected.");
                System.out.println("shutting down server...");

                try {
                    clientWriter.close();
                    clientReader.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            Thread scannerThread = new Thread(()-> {

                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String message = scanner.nextLine();
                    clientWriter.println(message);
                    clientWriter.flush();
                }
            });
            scannerThread.setDaemon(true);
            scannerThread.start();


        } catch (IOException e) {
            System.err.println("connection error: " + e.getMessage());
        }
    }
}
