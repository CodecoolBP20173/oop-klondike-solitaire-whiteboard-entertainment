package com.codecool.klondike;

import java.util.List;
import java.util.ArrayList;

public class Move {
    private List<Card> movedCards = new ArrayList<Card>();
    private Pile originalPile;
    private Pile targetPile;
    private boolean wasFlip;

    public List<Card> getMovedCards() { return this.movedCards; }

    public Pile getOriginalPile() { return this.originalPile; }

    public Pile getTargetPile() { return this.targetPile; }

    public boolean getIfFlip() { return this.wasFlip; }

    public Move(List<Card> cards, Pile originalPile, Pile targetPile, boolean wasFlip){
        this.movedCards = cards;
        this.originalPile = originalPile;
        this.targetPile = targetPile;
        this.wasFlip = wasFlip;
    }
}
