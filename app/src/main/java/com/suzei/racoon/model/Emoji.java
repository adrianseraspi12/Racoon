package com.suzei.racoon.model;

public class Emoji {

    private String name, image;

    public Emoji() {
    }

    public Emoji(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
