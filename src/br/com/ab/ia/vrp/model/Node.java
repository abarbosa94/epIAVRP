package br.com.ab.ia.vrp.model;

public class Node  {
    public boolean visited;
    public Route route;
    private int index;
    private double x;
    private double y;
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

}
