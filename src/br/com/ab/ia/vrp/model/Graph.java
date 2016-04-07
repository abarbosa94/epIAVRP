package br.com.ab.ia.vrp.model;

public class Graph {
    private int[][] adjacentMatrix;
    private int[] demand;
    private int capacity;

    public int[] getDemand() {
        return demand;
    }
    public void setDemand(int[] demand) {
        this.demand = demand;
    }
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public int[][] getAdjacentMatrix() {
        return adjacentMatrix;
    }
    public void setAdjacentMatrix(int[][] adjacentMatrix) {
        this.adjacentMatrix = adjacentMatrix;
    }
}
