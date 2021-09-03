package com.example.connectfarmapplication.models;

public class Image {
    String image ;
    boolean isChosen = false;

    public Image(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }
}
