package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ContextualAd extends Application {
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("authorization.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("client/style.css");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        launch(args);
    }
}
