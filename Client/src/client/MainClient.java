package client;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainClient extends Application {

    public static void main(String[] args){
        launch(args);
    }
    public void start(Stage stage){
        FormInterface client = new FormInterface(stage);

    }
}
