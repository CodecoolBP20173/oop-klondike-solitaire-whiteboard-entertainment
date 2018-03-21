package com.codecool.klondike;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;

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

        addMenu(primaryStage, game);

        primaryStage.setTitle("Klondike Solitaire");
        primaryStage.setScene(new Scene(game, WINDOW_WIDTH, WINDOW_HEIGHT));

        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private void addMenu(Stage primaryStage, Game game) {

        MenuBar menuBar = new MenuBar();

        Menu newGame = new Menu("New game");
        Menu backSides = new Menu("Themes");

        MenuItem crap = new MenuItem("Crap");
        MenuItem monaLiza= new MenuItem("Crap");


        backSides.getItems().add(crap);
        backSides.getItems().add(monaLiza);
        backSides.getItems().add(new SeparatorMenuItem());
        backSides.getItems().add(new MenuItem("Greenfox"));

        menuBar.getMenus().addAll(newGame, backSides);
        menuBar.setStyle("fx-padding: 1  5 1 5");
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        crap.setOnAction((event) -> {
                System.exit(0);
        });
        game.getChildren().add(menuBar);

    }

}
