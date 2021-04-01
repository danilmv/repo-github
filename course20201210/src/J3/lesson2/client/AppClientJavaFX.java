package J3.lesson2.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppClientJavaFX extends Application {
    private ClientJavaFXController controller;
    private FXMLLoader fxmlLoader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        fxmlLoader = new FXMLLoader(getClass().getResource("/client_gui.fxml"));
//        Parent root = fxmlLoader.load();
//        или так:
        fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("/client_gui.fxml").openStream());

        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.show();

        controller = fxmlLoader.getController();
//        controller = fxmlLoader.<ClientJavaFXController>getController();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        if (controller != null)
            controller.sayByeBye();
    }
}
