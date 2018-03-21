package com.codecool.klondike;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Klondike extends Application {

    private static final double WINDOW_WIDTH = 1400;
    private static final double WINDOW_HEIGHT = 900;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        startGame(primaryStage);
        restartGame(primaryStage);
    }

    public void restartGame(Stage primaryStage){
        primaryStage.close();
        startGame(primaryStage);
    }

    public void startGame(Stage primaryStage) {
        Card.loadCardImages();
        Game game = new Game();
        game.setStyle(
                "-fx-background-image: url(" +
                        "'https://img00.deviantart.net/1609/i/2016/332/f/7/unicorn_background_for_wildtangent_solitaire_by_catwagons-dapxtfm.png'" +
                        "); " +
                        "-fx-background-size: cover;"
        );

        primaryStage.setTitle("Klondike Solitaire");
        primaryStage.setScene(new Scene(game, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.show();
    }

}
