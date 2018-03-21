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
        Theme standardTheme = new Theme("https://img00.deviantart.net/1609/i/2016/332/f/7/unicorn_background_for_wildtangent_solitaire_by_catwagons-dapxtfm.png",
                "card_images/card_back.png");
        startGame(primaryStage, standardTheme);
    }

    public void restartGame(Stage primaryStage){
        //primaryStage.close();
        Theme standardTheme = new Theme("https://img00.deviantart.net/1609/i/2016/332/f/7/unicorn_background_for_wildtangent_solitaire_by_catwagons-dapxtfm.png",
                "card_images/card_back.png");
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

        MenuItem restart = new MenuItem("Restart");
        MenuItem exit = new MenuItem("Exit");


        MenuItem firstTheme = new MenuItem("Slutty Unicorn");
        MenuItem secondTheme = new MenuItem("Tróger Unicorn");
        MenuItem thirdTheme = new MenuItem("Horror Unicorn");



        gameMenu.getItems().add(restart);
        gameMenu.getItems().add(exit);

        backSides.getItems().add(new SeparatorMenuItem());
        backSides.getItems().add(firstTheme);
        backSides.getItems().add(secondTheme);
        backSides.getItems().add(thirdTheme);


        menuBar.getMenus().addAll(gameMenu, backSides);
        menuBar.setStyle("fx-padding: 1  5 1 5");
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        restart.setOnAction((event) -> {
            restartGame(primaryStage);
        });

        exit.setOnAction((event) -> {
            System.exit(0);
        });

        Theme creepyTheme = new Theme("https://i.pinimg.com/originals/f0/df/1a/f0df1ae42d7ffb8fbdcc41b3d59c7937.jpg", "card_images/card_back1.png");
        thirdTheme.setOnAction((event) -> {
            startGame(primaryStage, creepyTheme);
        });

        Theme sluttyLisaTheme = new Theme("http://getwallpapers.com/wallpaper/full/5/e/e/605242.jpg", "card_images/card_back.png");
        firstTheme.setOnAction((event) -> {
            startGame(primaryStage, sluttyLisaTheme);
        });

            game.getChildren().add(menuBar);

    }

}
