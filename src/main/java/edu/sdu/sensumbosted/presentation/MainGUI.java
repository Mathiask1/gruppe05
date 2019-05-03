package edu.sdu.sensumbosted.presentation;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
       Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
    
        Scene scene = new Scene(root);
    
        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }
}
