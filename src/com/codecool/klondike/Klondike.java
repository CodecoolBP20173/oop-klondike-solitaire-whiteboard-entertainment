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
    private static String lastBackground = "https://img00.deviantart.net/1609/i/2016/332/f/7/unicorn_background_for_wildtangent_solitaire_by_catwagons-dapxtfm.png";
    private static String lastCardBack = "card_images/unicorn_cardback.png";


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Theme standardTheme = new Theme(lastBackground, lastCardBack);
        startGame(primaryStage, standardTheme);
    }

    public void restartGame(Stage primaryStage){
        //primaryStage.close();
        Theme standardTheme = new Theme(lastBackground, lastCardBack);
        startGame(primaryStage, standardTheme);
    }

    public void startGame(Stage primaryStage, Theme theme) {


        Card.loadCardImages(theme.cardBack());
        Game game = new Game();
        MouseUtil.myGame = game;
        game.setStyle(
                "-fx-background-image: url(" +
                        theme.getBackgroundImage() +
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


        MenuItem firstTheme = new MenuItem("Standard Unicorn");
        MenuItem secondTheme = new MenuItem("Slutty Lisa");
        MenuItem thirdTheme = new MenuItem("Creepy Red");
        MenuItem fourthTheme = new MenuItem("Creepy Black");

        gameMenu.getItems().add(undo);
      
        gameMenu.getItems().add(restart);
        gameMenu.getItems().add(exit);

        backSides.getItems().add(new SeparatorMenuItem());
        backSides.getItems().add(firstTheme);
        backSides.getItems().add(secondTheme);
        backSides.getItems().add(thirdTheme);
        backSides.getItems().add(fourthTheme);


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

        Theme standardUnicorn = new Theme("https://img00.deviantart.net/1609/i/2016/332/f/7/unicorn_background_for_wildtangent_solitaire_by_catwagons-dapxtfm.png",
                "card_images/unicorn_cardback.png");
        firstTheme.setOnAction((event) -> {
            lastBackground = "https://img00.deviantart.net/1609/i/2016/332/f/7/unicorn_background_for_wildtangent_solitaire_by_catwagons-dapxtfm.png";
            lastCardBack = "card_images/unicorn_cardback.png";
            startGame(primaryStage, standardUnicorn);
        });

        Theme sluttyLisaTheme = new Theme("/table/green.png", "card_images/card_back.png");
        secondTheme.setOnAction((event) -> {
            lastBackground = "/table/green.png";
            lastCardBack = "card_images/card_back.png";
            startGame(primaryStage, sluttyLisaTheme);
        });

        Theme creepyTheme = new Theme("https://i.pinimg.com/originals/f0/df/1a/f0df1ae42d7ffb8fbdcc41b3d59c7937.jpg", "/card_images/pennywise.png");
        thirdTheme.setOnAction((event) -> {
            lastBackground = "https://i.pinimg.com/originals/f0/df/1a/f0df1ae42d7ffb8fbdcc41b3d59c7937.jpg";
            lastCardBack = "/card_images/pennywise.png";
            startGame(primaryStage, creepyTheme);
        });

        Theme anotherCreepyTheme = new Theme("https://i.pinimg.com/originals/f0/df/1a/f0df1ae42d7ffb8fbdcc41b3d59c7937.jpg", "/card_images/penny.jpg");
        fourthTheme.setOnAction((event) -> {
            lastBackground = "https://i.pinimg.com/originals/f0/df/1a/f0df1ae42d7ffb8fbdcc41b3d59c7937.jpg";
            lastCardBack = "/card_images/penny.jpg";
            startGame(primaryStage, anotherCreepyTheme);
        });

            game.getChildren().add(menuBar);

    }

}
