package com.example.myapplication.data;

/**
 * Created by kimtaegyun on 2018. 1. 24..
 */

public class Item {
    int image;
    String title;

    public int getImage() {
        return this.image;
    }

    public String getTitle() {
        return this.title;
    }

    public Item(int image, String title) {
        this.image = image;
        this.title = title;
    }
}