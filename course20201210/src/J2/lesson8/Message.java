package J2.lesson8;

public class Message {
    public static final String MESSAGE_CLIENT_QUITS = "/end";
    public static final String MESSAGE_CLIENTS_LIST = "/list";
    public static final String MESSAGE_PRIVATE = "/w";
    public static final String MESSAGE_AUTHORIZE = "/auth";
    public static final String MESSAGE_TIMEOUT = "/timeout";

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
            String[] subMsg = message.split("\\s", 3);
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
        } else if (message.startsWith(MESSAGE_CLIENTS_LIST)) {
            String[] subMsg = message.split("\\s", 2);
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
        } else if (message.startsWith(MESSAGE_CLIENT_QUITS)) {
            this.command = message;
        } else if (message.startsWith(MESSAGE_AUTHORIZE)) {
            String[] subMsg = message.split("\\s", 3);
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
        } else if (message.startsWith(MESSAGE_TIMEOUT)) {
            String[] subMsg = message.split("\\s", 2);
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
        } else {
            this.command = "";
            this.message = message;
        }
    }
}
