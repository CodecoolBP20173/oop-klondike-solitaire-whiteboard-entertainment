package com.codecool.klondike;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Pile extends Pane {

    private PileType pileType;
    private String name;
    private double cardGap;
    private ObservableList<Card> cards = FXCollections.observableArrayList();

    private static Game myGame;

    public Pile(PileType pileType, String name, double cardGap, Game game) {
        this.pileType = pileType;
        this.cardGap = cardGap;
        myGame = game;
    }

    public PileType getPileType() {
        return pileType;
    }

    public String getName() {
        return name;
    }

    public double getCardGap() {
        return cardGap;
    }

    public ObservableList<Card> getCards() {
        return cards;
    }

    public int numOfCards() {
        return this.cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public void clear() {
        this.cards.clear();
    }

    public void addCard(Card card) {
        cards.add(card);
        card.setContainingPile(this);
        card.toFront();
        layoutCard(card);
    }

    private void layoutCard(Card card) {
        card.relocate(card.getLayoutX() + card.getTranslateX(), card.getLayoutY() + card.getTranslateY());
        card.setTranslateX(0); //-35
        card.setTranslateY(0); //25
        card.setLayoutX(getLayoutX());
        card.setLayoutY(getLayoutY() + (cards.size() - 1) * cardGap);
    }

    public Card getTopCard() {
        if (cards.isEmpty())
            return null;
        else
            return cards.get(cards.size() - 1);
    }

    public static void flipTopCardIfTableau(Pile sourcePile) {
        if (sourcePile.getPileType() == Pile.PileType.TABLEAU) {
            Card card = sourcePile.getTopCard();
            if (card != null){
                if (card.isFaceDown()){
                    card.flip();

                    List<Card> flippedCards = new ArrayList<>();
                    flippedCards.add(card);

                    Move m = new Move(flippedCards, sourcePile, sourcePile, true);
                    myGame.saveMove(m);
                }

            }
        }
    }

    public void setBlurredBackground() {
        setPrefSize(Card.WIDTH, Card.HEIGHT);
        BackgroundFill backgroundFill = new BackgroundFill(Color.gray(0.0, 0.2), null, null);
        Background background = new Background(backgroundFill);
        GaussianBlur gaussianBlur = new GaussianBlur(10);
        setBackground(background);
        setEffect(gaussianBlur);
    }

    public ArrayList<Card> getAllFlippedCards() {
        ArrayList<Card> flippedCards = new ArrayList<>();
        for (Card currCard : this.getCards()){
            if (!currCard.isFaceDown()) flippedCards.add(currCard);
        }
        return flippedCards;
    }

    public enum PileType {
        STOCK,
        DISCARD,
        FOUNDATION,
        TABLEAU
    }
}
