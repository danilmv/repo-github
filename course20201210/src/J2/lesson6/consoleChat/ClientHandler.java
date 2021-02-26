package J2.lesson6.consoleChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private String name;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;

    public ClientHandler(Socket socket, String name, Server server) {
        this.socket = socket;
        this.name = name;
        this.server = server;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                Message msg = new Message();
                String clientMessage;

                try {
                    server.subscribe(this);

                    while (true) {
                        msg.parseMessage(in.readUTF());

                        if (msg.command != null) {
                            if (msg.command.equals(Message.MESSAGE_CLIENT_QUITS)) {
                                sendMessage(Message.MESSAGE_CLIENT_QUITS);
                                break;
                            } else if (msg.command.equals(Message.MESSAGE_PRIVATE)) {
                                ClientHandler receiver = server.getClientByName(msg.name);

                                clientMessage = name + ": " + msg.message;
                                System.out.println("<to " + msg.name + ">: " + clientMessage);
                                server.sendPrivate(msg.command + " " + clientMessage, receiver);
                            }
                        } else {
                            clientMessage = name + ": " + msg.message;

                            System.out.println(clientMessage);
                            server.sendAll(clientMessage);
                        }
                    }
                } catch (IOException e) {
                    System.err.println(name + " connection error: " + e.getMessage());
                }
                close();

            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {

        System.out.println("connection with " + name + " is closed.");

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.unsubscribe(this);
    }

    public String getName() {
        return name;
    }
}
