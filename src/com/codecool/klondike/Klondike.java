package com.codecool.klondike;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
    }

    public void restartGame(Stage primaryStage){
        //primaryStage.close();
        startGame(primaryStage);
    }

    public void startGame(Stage primaryStage) {
        Card.loadCardImages();
        Game game = new Game();
        MouseUtil.myGame = game;
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

        Menu gameMenu = new Menu("Game");
        Menu backSides = new Menu("Themes");

        MenuItem undo = new MenuItem("Undo move");
        MenuItem restart = new MenuItem("Restart");
        MenuItem exit = new MenuItem("Exit");


        gameMenu.getItems().add(undo);
        gameMenu.getItems().add(restart);
        gameMenu.getItems().add(exit);

        backSides.getItems().add(new SeparatorMenuItem());
        backSides.getItems().add(new MenuItem("Greenfox"));

        menuBar.getMenus().addAll(gameMenu, backSides);
        menuBar.setStyle("fx-padding: 1  5 1 5");
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        undo.setOnAction((event) -> {
            game.undoMove();
        });
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));

        restart.setOnAction((event) -> {
            restartGame(primaryStage);
        });

        exit.setOnAction((event) -> {
            System.exit(0);
        });

        game.getChildren().add(menuBar);

    }

}
