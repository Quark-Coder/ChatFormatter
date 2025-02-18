package com.quark.chatformatter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ChatFormatter extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatFormatter.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ChatFormatter");

        Image icon = new Image(Objects.requireNonNull(ChatFormatter.class.getResourceAsStream
                ("/com/quark/chatformatter/img/main_icon.png")));
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
