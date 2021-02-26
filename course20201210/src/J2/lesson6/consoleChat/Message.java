package J2.lesson6.consoleChat;

public class Message {
    public static final String MESSAGE_CLIENT_QUITS = "/end";
    public static final String MESSAGE_CLIENTS_LIST = "/list";
    public static final String MESSAGE_PRIVATE = "/w";

    public String command;
    public String name;
    public String message;

    public void parseMessage(String message) {
        this.command = null;
        this.message = null;
        this.name = null;

        if (message == null)
            return;

        if (message.startsWith(MESSAGE_PRIVATE)) {
            String[] subMsg = message.split(" ", 3);
            for (int i = 0; i < subMsg.length; i++) {
                switch (i) {
                    case 0:
                        command = subMsg[i];
                        break;
                    case 1:
                        name = subMsg[i];
                        break;
                    case 2:
                        this.message = subMsg[i];
                        break;
                }
            }
        }else if (message.startsWith(MESSAGE_CLIENTS_LIST)){
            String[] subMsg = message.split(" ", 2);
            for (int i = 0; i < subMsg.length; i++) {
                switch (i) {
                    case 0:
                        command = subMsg[i];
                        break;
                    case 1:
                        this.message = subMsg[i];
                        break;
                }
            }
        }else if (message.startsWith(MESSAGE_CLIENT_QUITS)){
            this.command = message;
        }else{
            this.message = message;
        }
    }
}
