package flixtime;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Platform.setImplicitExit(false);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("mainWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("FlixTime");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(
                new Image("/resources/images/logo.png")
        );
        Controller mainController = loader.getController();
        Scene scene = new Scene(root, 300, 275);
        primaryStage.setScene(scene);
        mainController.createButtons();
        mainController.createTrayIcon(primaryStage, root);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
