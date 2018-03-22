package com.codecool.klondike;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;

public class MouseUtil {

    public static Game myGame;

    public static void slideBack(Card card) {
        double sourceX = card.getLayoutX() + card.getTranslateX();
        double sourceY = card.getLayoutY() + card.getTranslateY();
        double targetX = card.getLayoutX();
        double targetY = card.getLayoutY();
        animateCardMovement(card, sourceX, sourceY,
                targetX, targetY, Duration.millis(150), e -> {
                    card.getDropShadow().setRadius(2);
                    card.getDropShadow().setOffsetX(0);
                    card.getDropShadow().setOffsetY(0);
                });
    }

    public static void slideToDest(List<Card> cardsToSlide, Pile destPile) {
        if (cardsToSlide == null)
            return;
        double destCardGap = destPile.getCardGap();
        double targetX;
        double targetY;

        if (destPile.isEmpty()) {
            targetX = destPile.getLayoutX();
            targetY = destPile.getLayoutY();
        } else {
            targetX = destPile.getTopCard().getLayoutX();
            targetY = destPile.getTopCard().getLayoutY();
        }

        for (int i = 0; i < cardsToSlide.size(); i++) {
            Card currentCard = cardsToSlide.get(i);
            Pile sourcePile = currentCard.getContainingPile();
            double sourceX = currentCard.getLayoutX() + currentCard.getTranslateX();
            double sourceY = currentCard.getLayoutY() + currentCard.getTranslateY();

            animateCardMovement(currentCard, sourceX, sourceY, targetX,
                    targetY + ((destPile.isEmpty() ? i : i + 1) * destCardGap), Duration.millis(150),
                    e -> {
                        currentCard.moveToPile(destPile);
                        currentCard.getDropShadow().setRadius(2);
                        currentCard.getDropShadow().setOffsetX(0);
                        currentCard.getDropShadow().setOffsetY(0);

                        Pile.PileType sourceType = sourcePile.getPileType();

                        if (sourceType == Pile.PileType.TABLEAU || sourceType == Pile.PileType.FOUNDATION) {
                            if (!sourcePile.isEmpty()){
                                Pile.flipTopCardIfTableau(sourcePile);
                            }

                            boolean gameWon = myGame.isGameWon();
                            if (gameWon) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Congratulations!");
                                alert.setHeaderText(null);
                                alert.setContentText("You won!\nThe program will exit now...");
                                alert.show();
                                alert.setOnCloseRequest((event) -> {
                                    System.exit(0);
                                });
                            }
                        }

                    });

        }
    }


    private static void animateCardMovement(
            Card card, double sourceX, double sourceY,
            double targetX, double targetY, Duration duration,
            EventHandler<ActionEvent> doAfter) {

        Path path = new Path();
        path.getElements().add(new MoveToAbs(card, sourceX, sourceY));
        path.getElements().add(new LineToAbs(card, targetX, targetY));

        PathTransition pathTransition = new PathTransition(duration, path, card);
        pathTransition.setInterpolator(Interpolator.EASE_IN);
        pathTransition.setOnFinished(doAfter);

        Timeline blurReset = new Timeline();
        KeyValue bx = new KeyValue(card.getDropShadow().offsetXProperty(), 0, Interpolator.EASE_IN);
        KeyValue by = new KeyValue(card.getDropShadow().offsetYProperty(), 0, Interpolator.EASE_IN);
        KeyValue br = new KeyValue(card.getDropShadow().radiusProperty(), 2, Interpolator.EASE_IN);
        KeyFrame bKeyFrame = new KeyFrame(duration, bx, by, br);
        blurReset.getKeyFrames().add(bKeyFrame);

        ParallelTransition pt = new ParallelTransition(card, pathTransition, blurReset);
        pt.play();

        vomitCard(card);

    }

    private static void vomitCard(Card card) {
        Random rand = new Random();
        int  n = rand.nextInt(12) - 4;

        RotateTransition rt = new RotateTransition(Duration.millis(200), card);
        rt.setByAngle(n);
        rt.setCycleCount(1);
        rt.setAutoReverse(true);
        rt.play();
    }

    private static class MoveToAbs extends MoveTo {
        MoveToAbs(Node node, double x, double y) {
            super(x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2,
                    y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
        }
    }

    private static class LineToAbs extends LineTo {
        LineToAbs(Node node, double x, double y) {
            super(x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2,
                    y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
        }
    }

}
