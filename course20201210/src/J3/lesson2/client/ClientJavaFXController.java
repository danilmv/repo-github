package J3.lesson2.client;

import J3.lesson2.server.Message;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientJavaFXController implements Initializable {
    @FXML
    public VBox mainPanel;

    @FXML
    public VBox authPanel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passField;

    @FXML
    public HBox chatPanel;
    @FXML
    public TextArea textArea;
    @FXML
    public ListView<String> clientsList;

    @FXML
    public HBox msgPanel;
    @FXML
    public TextField msgField;


    private Stage stage;
    Client client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                client = new Client();
                setCallBacks();
                client.connectToServer();
                return null;
            }
        };
        new Thread(task).start();

        // scene еще не создана...
//        authPanel.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                client.sendMessage(Message.MESSAGE_CLIENT_QUITS);
//                client.saveHistory(textArea.getText());
//            }
//        });
    }

    private void setCallBacks() {
        client.setOnMessageReceived((message) -> textArea.appendText(message + "\n"));

        client.setOnListReceived((message) -> {
            Platform.runLater(() -> {
                clientsList.getItems().clear();
                clientsList.getItems().addAll(Arrays.stream(message.split("\\s")).collect(Collectors.toList()));
            });
        });

        client.setOnError(this::showAlert);

        client.setOnAuth((message) -> {
            authPanel.setVisible(false);
            authPanel.setManaged(false);

            chatPanel.setVisible(true);
            chatPanel.setManaged(true);
            msgPanel.setVisible(true);
            msgPanel.setManaged(true);

            textArea.appendText(client.loadHistory());

            Platform.runLater(() -> {
                stage = (Stage) authPanel.getScene().getWindow();
                stage.titleProperty().set(message);
            });
        });
//
//        client.setOnNickChangeAccepted(this::nickNameAccepted);
    }

    public void sendAuth(ActionEvent actionEvent) {
        client.sendMessage(Message.MESSAGE_AUTHORIZE + " " + loginField.getText() + " " + passField.getText());
    }

    public void sendMsg(ActionEvent actionEvent) {
        String message = msgField.getText();
        if (!message.isEmpty())
            client.sendMessage(message);

        msgField.clear();
        msgField.requestFocus();
    }

    public void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
            alert.showAndWait();
        });
    }

    public void sayByeBye() {
        System.out.println("Bye-bye");

        client.sendMessage(Message.MESSAGE_CLIENT_QUITS);

        client.saveHistory(textArea.getText());
    }

    public void onMouseClickedList(MouseEvent mouseEvent) {
        String user = clientsList.getSelectionModel().getSelectedItem();
        if (!user.isEmpty()) {
            Message msg = new Message();
            msg.parseMessage(msgField.getText());
            msgField.setText(Message.MESSAGE_PRIVATE + " " + user + " " + msg.getMessage());
        }
    }
}
