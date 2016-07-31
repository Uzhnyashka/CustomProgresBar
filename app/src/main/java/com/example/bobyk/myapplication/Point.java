package com.example.bobyk.myapplication;

/**
 * Created by bobyk on 30/07/16.
 */
public class Point {
    private int posX;
    private int posY;
    private int startPosX;
    private int startPosY;

    public Point(int posX, int posY, int startPosX, int startPosY){
        this.posX = posX;
        this.posY = posY;
        this.startPosX = startPosX;
        this.startPosY = startPosY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getStartPosX() {
        return startPosX;
    }

    public int getStartPosY() {
        return startPosY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setStartPosX(int startPosX) {
        this.startPosX = startPosX;
    }

    public void setStartPosY(int startPosY) {
        this.startPosY = startPosY;
    }
}
