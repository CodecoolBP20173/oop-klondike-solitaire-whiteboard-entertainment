package com.codecool.klondike;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;


import java.util.*;

public class Game extends Pane {

    private List<Card> deck = new ArrayList<>();

    private Pile stockPile;
    private Pile discardPile;
    private List<Pile> foundationPiles = FXCollections.observableArrayList();
    private List<Pile> tableauPiles = FXCollections.observableArrayList();

    private Stack<Move> madeMoves = new Stack<>();

    private double dragStartX, dragStartY;
    List<Card> draggedCards = FXCollections.observableArrayList();

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

            List<Card> movedCards = new ArrayList<>();
            movedCards.add(card);

            MouseUtil.slideToDest(movedCards, discardPile);

            Move m = new Move(movedCards, stockPile, discardPile, false);
            saveMove(m);
            m = new Move(movedCards, discardPile, discardPile, true);
            saveMove(m);
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

            if (draggedCards.isEmpty()) {
                int currCardIdx = flippedCardsInPile.indexOf(card);
                if (flippedCardsInPile.indexOf(card) != flippedCardsInPile.size()) {
                    for (int i = currCardIdx; i < flippedCardsInPile.size(); i++) {
                        draggedCards.add(flippedCardsInPile.get(i));
                    }
                } else {
                    draggedCards.add(card);
                }
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

        if (pile != null) {
            makeMove(card, draggedCards, pile);
        } else {
            pile = getValidIntersectingPile(card, foundationPiles);
            if (pile == null) {
                draggedCards.forEach(MouseUtil::slideBack);
            } else {
                makeMove(card, draggedCards, pile);
            }
        }

    };

    private EventHandler<MouseEvent> onMouseDoubleClickedHandler = e -> {
        if (!draggedCards.isEmpty()) return;

        Card currentCard = (Card) e.getSource();
        if (currentCard.isFaceDown()) return;

        int clickCount = e.getClickCount();
        if (clickCount == 2){
            int aceRank = 1;
            if (currentCard.getRank() == aceRank){
                handleAceMoving(currentCard);
            } else {
                handleCardMoving(currentCard);
            }
        }
    };

    private void handleAceMoving(Card card){
        Pile destPile = findEmptyFoundation();
        if (destPile != null){
            List<Card> movedCards = new ArrayList<>();
            movedCards.add(card);

            makeMove(card, movedCards, destPile);
        }
    }

    private Pile findEmptyFoundation(){
        for (Pile fPile : foundationPiles){
            if (fPile.isEmpty()) return fPile;
        }
        return null;
    }

    private Pile findCorrectFoundation(Card card){
        for (Pile fPile : foundationPiles){
            if (!fPile.isEmpty()) {
                Card pileTopCard = fPile.getTopCard();
                if (Card.isSameSuit(card, pileTopCard)) {
                    if (isMoveValid(card, fPile)) return fPile;
                }
            }
        }
        return null;
    }

    private void handleCardMoving(Card card){
        Pile destPile = findCorrectFoundation(card);
        if (destPile != null) {
            List<Card> movedCards = new ArrayList<>();
            movedCards.add(card);

            makeMove(card, movedCards, destPile);
        }
    }

    private void makeMove(Card card, List<Card> dCards, Pile dPile){
        List<Card> movedCards = new ArrayList<>(dCards);
        Move m = new Move(movedCards, card.getContainingPile(), dPile, false);
        saveMove(m);

        handleValidMove(card, dPile);
        MouseUtil.slideToDest(dCards, dPile);
    }

    void saveMove(Move m){
        this.madeMoves.push(m);
    }

    private Move getLastMove(){
        if (!this.madeMoves.empty()){
            return this.madeMoves.pop();
        }
        throw new EmptyStackException();
    }

    public void undoMove(){
        try {
            Move lastMove = this.getLastMove();
            List<Card> movedCards = lastMove.getMovedCards();

            if (lastMove.getIfFlip()){
                for (Card card: movedCards){
                    card.flip();
                }
                undoMove();
            } else {

                Pile destPile = lastMove.getOriginalPile();

                ListIterator<Card> iter = movedCards.listIterator();
                while (iter.hasPrevious()){
                    Card card = iter.previous();
                    handleValidMove(card, destPile);
                }
                MouseUtil.slideToDest(movedCards, destPile);
            }
        } catch (EmptyStackException e){
            System.out.println("There is no move to undo");
        }
    }

    public boolean isGameWon() {
        int cardCount = 0;
        for (int i = 0; i < foundationPiles.size(); i++) {
            cardCount += foundationPiles.get(i).numOfCards();
        }
        if (cardCount == 52) {
            return true;
        }
        return false;
    }

    public Game() {
        deck = Card.createNewDeck();
        initPiles();
        dealCards();
//        addButton();
        flipTheTopCardOfAllTableauPiles();
    }

    public void addMouseEventHandlers(Card card) {
        card.setOnMousePressed(onMousePressedHandler);
        card.setOnMouseDragged(onMouseDraggedHandler);
        card.setOnMouseReleased(onMouseReleasedHandler);
        card.setOnMouseClicked(onMouseClickedHandler);
        card.addEventHandler(MouseEvent.MOUSE_CLICKED, onMouseDoubleClickedHandler);
    }

    public void refillStockFromDiscard() {
        Collections.reverse(discardPile.getCards());
        for (Card currCard : discardPile.getCards()) {
            currCard.flip();
            stockPile.addCard(currCard);
        }

        Collections.reverse(discardPile.getCards());
        List<Card> refilledCards = new ArrayList<>(discardPile.getCards());

        Move m = new Move(refilledCards, discardPile, stockPile, false);
        saveMove(m);
        m = new Move(refilledCards, stockPile, stockPile, true);
        saveMove(m);

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
        stockPile = new Pile(Pile.PileType.STOCK, "Stock", STOCK_GAP, this);
        stockPile.setBlurredBackground();
        stockPile.setLayoutX(60);// 95
        stockPile.setLayoutY(45); //20
        stockPile.setOnMouseClicked(stockReverseCardsHandler);
        getChildren().add(stockPile);

        discardPile = new Pile(Pile.PileType.DISCARD, "Discard", STOCK_GAP, this);
        discardPile.setBlurredBackground();
        discardPile.setLayoutX(250); //285
        discardPile.setLayoutY(45); //20
        getChildren().add(discardPile);

        for (int i = 0; i < 4; i++) {
            Pile foundationPile = new Pile(Pile.PileType.FOUNDATION, "Foundation " + i, FOUNDATION_GAP, this);
            foundationPile.setBlurredBackground();
            foundationPile.setLayoutX(575 + i * 180); //610
            foundationPile.setLayoutY(45); //20
            foundationPiles.add(foundationPile);
            getChildren().add(foundationPile);
        }
        for (int i = 0; i < 7; i++) {
            Pile tableauPile = new Pile(Pile.PileType.TABLEAU, "Tableau " + i, TABLEAU_GAP, this);
            tableauPile.setBlurredBackground();
            tableauPile.setLayoutX(60 + i * 180);
            tableauPile.setLayoutY(300); //275
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


