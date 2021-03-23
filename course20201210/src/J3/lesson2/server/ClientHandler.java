package J3.lesson2.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private String name;
    private String login;
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
//            new Thread(this).start();
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

    public String getLogin() {
        return login;
    }

    @Override
    public void run() {
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
                    msg.parseMessage(in.readUTF());

                    switch (msg.getCommand()) {
                        case Message.MESSAGE_AUTHORIZE:
                            String nickname = server.checkAuthorization(msg.getName(), msg.getMessage());
                            authorized = nickname != null;
                            if (authorized) {
                                System.out.println(name + " => " + nickname);
                                this.login = msg.getName();
                                this.name = nickname;
                                if (server.subscribe(this))
                                    sendMessage(Message.MESSAGE_AUTHORIZE + " " + nickname + " " + this.login);
                                else {
                                    authorized = false;
                                }
                            } else
                                sendMessage("Server: wrong login or password");
                            break;

                        case Message.MESSAGE_CLIENT_QUITS:
                            sendMessage(Message.MESSAGE_CLIENT_QUITS);
                            connected = false;
                            break;

                        case Message.MESSAGE_PRIVATE:
                            ClientHandler receiver = server.getClientByName(msg.getName());
                            clientMessage = this.name + ": " + msg.getMessage();
                            System.out.println("<to " + msg.getName() + ">: " + clientMessage);

                            if (receiver != null) {
                                server.sendPrivate(msg.getCommand() + " <to " + msg.getName() + ">: " + clientMessage, this);
                                server.sendPrivate(msg.getCommand() + " " + clientMessage, receiver);
                            }
                            break;

                        case Message.MESSAGE_CHANGE_NICK:
                            String oldName = getName();

                            if (!msg.getName().isEmpty())
                                this.name = msg.getName();

                            sendMessage(Message.MESSAGE_CHANGE_NICK_OK + " " + this.getName());

                            if (!oldName.equals(getName()))
                                server.informNickChanged(this, oldName);

                            break;

                        default:
                            clientMessage = this.name + ": " + msg.getMessage();

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

    }
}
