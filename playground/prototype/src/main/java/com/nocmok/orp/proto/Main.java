package com.nocmok.orp.proto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        var primaryScreen = javafx.stage.Screen.getPrimary().getBounds();
        stage.setWidth(800);
        stage.setHeight(600);

        var fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("main_layout.fxml"));
        var root = fxmlLoader.<GridPane>load();
        var controller = fxmlLoader.<MainController>getController();

        stage.setScene(new Scene(root));
        stage.show();
    }
}
