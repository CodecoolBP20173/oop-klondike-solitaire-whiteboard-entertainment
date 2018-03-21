package com.codecool.klondike;

public class Theme {

    private String backgroundImage;
    private String cardBack;

    public Theme(String themeImage, String cardBack){
        this.backgroundImage = themeImage;
        this.cardBack = cardBack;
    }

    public String getBackgroundImage() {
        return this.backgroundImage;
    }

    public String cardBack() {
        return this.cardBack;
    }
}