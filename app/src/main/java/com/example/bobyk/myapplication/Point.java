package com.example.bobyk.myapplication;

/**
 * Created by bobyk on 30/07/16.
 */
public class Point {
    private int posX;
    private int posY;

    public Point(int posX, int posY){
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
}
