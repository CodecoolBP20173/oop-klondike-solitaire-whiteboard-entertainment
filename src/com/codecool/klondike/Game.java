package com.codecool.klondike;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control. *;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Game extends Pane {

    private List<Card> deck = new ArrayList<>();

    private Pile stockPile;
    private Pile discardPile;
    private List<Pile> foundationPiles = FXCollections.observableArrayList();
    private List<Pile> tableauPiles = FXCollections.observableArrayList();

    private double dragStartX, dragStartY;
    private List<Card> draggedCards = FXCollections.observableArrayList();

    private static double STOCK_GAP = 1;
    private static double FOUNDATION_GAP = 0;
    private static double TABLEAU_GAP = 30;


    private EventHandler<MouseEvent> onMouseClickedHandler = e -> {
        Card card = (Card) e.getSource();
        if (card.getContainingPile().getPileType() == Pile.PileType.STOCK) {
            card.moveToPile(discardPile);
            card.flip();
            card.setMouseTransparent(false);
            System.out.println("Placed " + card + " to the waste.");
        }
    };

    private EventHandler<MouseEvent> stockReverseCardsHandler = e -> {
        refillStockFromDiscard();
    };

    private EventHandler<MouseEvent> onMousePressedHandler = e -> {
        dragStartX = e.getSceneX();
        dragStartY = e.getSceneY();
    };

    private EventHandler<MouseEvent> onMouseDraggedHandler = e -> {
        Card card = (Card) e.getSource();
        Pile activePile = card.getContainingPile();
        ArrayList<Card> flippedCardsInPile = activePile.getAllFlippedCards();
        if (flippedCardsInPile.contains(card)) {
            if (activePile.getPileType() == Pile.PileType.STOCK)
                return;
            double offsetX = e.getSceneX() - dragStartX;
            double offsetY = e.getSceneY() - dragStartY;

            draggedCards.clear();
            int currCardIdx = flippedCardsInPile.indexOf(card);
            if (flippedCardsInPile.indexOf(card) != flippedCardsInPile.size())
            {
                for (int i = currCardIdx; i < flippedCardsInPile.size(); i++){
                    draggedCards.add(flippedCardsInPile.get(i));
                }
            } else {
                draggedCards.add(card);
            }


            card.getDropShadow().setRadius(20);
            card.getDropShadow().setOffsetX(10);
            card.getDropShadow().setOffsetY(10);

            for (Card dragged : draggedCards) {
                dragged.toFront();
                dragged.setTranslateX(offsetX);
                dragged.setTranslateY(offsetY);
            }
        }
    };

    private EventHandler<MouseEvent> onMouseReleasedHandler = e -> {
        if (draggedCards.isEmpty())
            return;
        Card card = (Card) e.getSource();
        Pile pile = getValidIntersectingPile(card, tableauPiles);

        //TODO ?Might be done?
        if (pile != null) {
            handleValidMove(card, pile);
            MouseUtil.slideToDest(draggedCards, pile);
        } else {
            pile = getValidIntersectingPile(card, foundationPiles);
            if (pile == null) {
                draggedCards.forEach(MouseUtil::slideBack);
                draggedCards.clear();
            } else {
                handleValidMove(card, pile);
                MouseUtil.slideToDest(draggedCards, pile);
            }
        }
        isGameWon();
    };

    public boolean isGameWon() {
        //TODO
        int cardCount = 0;
        for (int i = 0; i < foundationPiles.size(); i++) {
            cardCount += foundationPiles.get(i).numOfCards();
        }
        if (cardCount == 52) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("You won!");
            alert.showAndWait();
            return true;
        }
        return false;
    }

    public Game() {
        deck = Card.createNewDeck();
        initPiles();
        dealCards();
        flipTheTopCardOfAllTableauPiles();
    }

    public void addMouseEventHandlers(Card card) {
        card.setOnMousePressed(onMousePressedHandler);
        card.setOnMouseDragged(onMouseDraggedHandler);
        card.setOnMouseReleased(onMouseReleasedHandler);
        card.setOnMouseClicked(onMouseClickedHandler);
    }

    public void refillStockFromDiscard() {
        Collections.reverse(discardPile.getCards());
        for (Card currCard : discardPile.getCards()) {
            currCard.flip();
            stockPile.addCard(currCard);
        }
        discardPile.clear();
        System.out.println("Stock refilled from discard pile.");
    }

    public boolean isMoveValid(Card card, Pile destPile) {
        switch (destPile.getPileType()){
            case TABLEAU:
                Card lastCardInPile = destPile.getTopCard();
                int rankOfKing = 13;
                if (lastCardInPile == null){
                    if (card.getRank() == rankOfKing){
                        return true;
                    }
                    return false;
                } else {
                    return Card.isOppositeColor(card, lastCardInPile) && lastCardInPile.getRank() - card.getRank() == 1;
                }
            case FOUNDATION:
                int aceCardRank = 1;
                lastCardInPile = destPile.getTopCard();
                if (lastCardInPile == null){
                    if (card.getRank() == aceCardRank){
                        return true;
                    }
                    return false;
                } else {
                    return Card.isSameSuit(card, lastCardInPile) && lastCardInPile.getRank() - card.getRank() == -1;
                }
        }
        return false;
    }

    private Pile getValidIntersectingPile(Card card, List<Pile> piles) {
        Pile result = null;
        for (Pile pile : piles) {
            if (!pile.equals(card.getContainingPile()) &&
                    isOverPile(card, pile) &&
                    isMoveValid(card, pile))
                result = pile;
        }
        return result;
    }

    private boolean isOverPile(Card card, Pile pile) {
        if (pile.isEmpty())
            return card.getBoundsInParent().intersects(pile.getBoundsInParent());
        else
            return card.getBoundsInParent().intersects(pile.getTopCard().getBoundsInParent());
    }

    private void handleValidMove(Card card, Pile destPile) {
        String msg = null;
//        Pile sourcePile = card.getContainingPile();
        if (destPile.isEmpty()) {
            if (destPile.getPileType().equals(Pile.PileType.FOUNDATION))
                msg = String.format("Placed %s to the foundation.", card);
            if (destPile.getPileType().equals(Pile.PileType.TABLEAU))
                msg = String.format("Placed %s to a new pile.", card);
        } else {
            msg = String.format("Placed %s to %s.", card, destPile.getTopCard());
        }
        System.out.println(msg);
        MouseUtil.slideToDest(draggedCards, destPile);
        draggedCards.clear();
//        sourcePile.getTopCard().flip();
    }


    private void initPiles() {
        stockPile = new Pile(Pile.PileType.STOCK, "Stock", STOCK_GAP);
        stockPile.setBlurredBackground();
        stockPile.setLayoutX(95);
        stockPile.setLayoutY(20);
        stockPile.setOnMouseClicked(stockReverseCardsHandler);
        getChildren().add(stockPile);

        discardPile = new Pile(Pile.PileType.DISCARD, "Discard", STOCK_GAP);
        discardPile.setBlurredBackground();
        discardPile.setLayoutX(285);
        discardPile.setLayoutY(20);
        getChildren().add(discardPile);

        for (int i = 0; i < 4; i++) {
            Pile foundationPile = new Pile(Pile.PileType.FOUNDATION, "Foundation " + i, FOUNDATION_GAP);
            foundationPile.setBlurredBackground();
            foundationPile.setLayoutX(610 + i * 180);
            foundationPile.setLayoutY(20);
            foundationPiles.add(foundationPile);
            getChildren().add(foundationPile);
        }
        for (int i = 0; i < 7; i++) {
            Pile tableauPile = new Pile(Pile.PileType.TABLEAU, "Tableau " + i, TABLEAU_GAP);
            tableauPile.setBlurredBackground();
            tableauPile.setLayoutX(95 + i * 180);
            tableauPile.setLayoutY(275);
            tableauPiles.add(tableauPile);
            getChildren().add(tableauPile);
        }
    }

    public void dealCards() {
        Iterator<Card> deckIterator = deck.iterator();
        deckIterator.forEachRemaining(card -> {
            if (tableauPiles.get(6).numOfCards() < 7){
                tableauPiles.get(6).addCard(card);
            } else if (tableauPiles.get(5).numOfCards() < 6) {
                tableauPiles.get(5).addCard(card);
            } else if (tableauPiles.get(4).numOfCards() < 5) {
                tableauPiles.get(4).addCard(card);
            } else if (tableauPiles.get(3).numOfCards() < 4) {
                tableauPiles.get(3).addCard(card);
            } else if (tableauPiles.get(2).numOfCards() < 3) {
                tableauPiles.get(2).addCard(card);
            } else if (tableauPiles.get(1).numOfCards() < 2) {
                tableauPiles.get(1).addCard(card);
            } else if (tableauPiles.get(0).numOfCards() < 1) {
                tableauPiles.get(0).addCard(card);
            } else {
                stockPile.addCard(card);
            }
            addMouseEventHandlers(card);
            getChildren().add(card);
        });

    }

    private void flipTheTopCardOfAllTableauPiles() {
        for (Pile tableauPile : tableauPiles) {
            tableauPile.getTopCard().flip();
        }
    }

    public void setTableBackground(Image tableBackground) {
        setBackground(new Background(new BackgroundImage(tableBackground,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }


    }


