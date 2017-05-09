package dev.renner;

import dev.renner.backend.util.ParadoxHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by renne on 08.05.2017.
 */
public class Main extends Application {

    public static void main (String args[])
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MainWindow.fxml"));

        primaryStage.setTitle("Paradox Mod Manager");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();

    }
}
