package J3.lesson2.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private String name;
    private String login;
    private DataInputStream in;
    private DataOutputStream out;
    private final Server server;
    private boolean authorized = false;

    public static final int CLIENT_AUTH_LEVEL = 556;
    private static final Level clientMessageLevel = new MyMessageLevel("CLNTMSG", 555);
    private static final Level clientAuthorizedLevel = new MyMessageLevel("CLNTAUTH", CLIENT_AUTH_LEVEL);

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
            Server.getLOGGER().severe(e.getMessage());
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            Server.getLOGGER().severe("error sending message to client: " + e.getMessage());
            close();
        }
    }

    private void close() {

        Server.getLOGGER().info("connection with " + name + " is closed.");

        try {
            socket.close();
        } catch (IOException e) {
            Server.getLOGGER().severe("error closing socket with client: " + e.getMessage());
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

        long startTime = System.currentTimeMillis();

        Server.getLOGGER().info(this.name + " connected");

        try {
            while (connected && server.isServerWorks()) {
                if (!authorized) {
                    if (System.currentTimeMillis() - startTime > TIMEOUT) {
                        Server.getLOGGER().info(this.name + " timed out");
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
                                Server.getLOGGER().log(clientAuthorizedLevel, name + " => " + nickname);
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
                            Server.getLOGGER().log(clientMessageLevel, "<to " + msg.getName() + ">: " + clientMessage);

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

                            Server.getLOGGER().log(clientMessageLevel, clientMessage);
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
            Server.getLOGGER().severe(name + " connection error: " + e.getMessage());
        }
        close();

    }

    public boolean isAuthorized() {
        return authorized;
    }
}

class MyMessageLevel extends Level {

    protected MyMessageLevel(String name, int value) {
        super(name, value);
    }
}
