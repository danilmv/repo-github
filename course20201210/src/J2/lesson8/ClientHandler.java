package J2.lesson8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final Socket socket;
    private String name;
    private DataInputStream in;
    private DataOutputStream out;
    private final Server server;

    private final long TIMEOUT = 120000;

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
                boolean connected = true;
                boolean authorized = false;

                long startTime = System.currentTimeMillis();

                System.out.println(this.name + " connected");

                try {
                    while (connected && server.isServerWorks()) {
                        if (!authorized) {
                            if (System.currentTimeMillis() - startTime > TIMEOUT) {
                                System.out.println(this.name + " timed out");
                                sendMessage(Message.MESSAGE_TIMEOUT + " Server: timeout");
                                connected = false;
                            }
                        }

                        if (in.available() > 0) {
//                            if (!authorized)
//                                startTime = System.currentTimeMillis();

                            msg.parseMessage(in.readUTF());

                            switch (msg.command) {
                                case Message.MESSAGE_AUTHORIZE:
                                    authorized = server.checkAuthorization(msg.name, msg.message);
                                    if (authorized) {
                                        System.out.println(name + " => " + msg.name);
                                        this.name = msg.name;
                                        sendMessage(Message.MESSAGE_AUTHORIZE + " " + msg.name);
                                        server.subscribe(this);
                                    } else
                                        sendMessage("Server: wrong login or password");
                                    break;

                                case Message.MESSAGE_CLIENT_QUITS:
                                    sendMessage(Message.MESSAGE_CLIENT_QUITS);
                                    connected = false;
                                    break;

                                case Message.MESSAGE_PRIVATE:
                                    ClientHandler receiver = server.getClientByName(msg.name);
                                    clientMessage = this.name + ": " + msg.message;
                                    System.out.println("<to " + msg.name + ">: " + clientMessage);

                                    if (receiver != null) {
                                        server.sendPrivate(msg.command + " <to " + msg.name + ">: " + clientMessage, this);
                                        server.sendPrivate(msg.command + " " + clientMessage, receiver);
                                    }
                                    break;

                                default:
                                    clientMessage = this.name + ": " + msg.message;

                                    System.out.println(clientMessage);
                                    server.sendAll(clientMessage);
                                    break;
                            }
//                            }
                        } else {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
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
