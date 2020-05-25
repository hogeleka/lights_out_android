package com.algorithmandblues.lightsout;

public class Level {

    private int id;
    private int dimension;
    private int numberOfStars;
    private int gameMode;

    Level() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDimension() {
        return this.dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    int getNumberOfStars() {
        return this.numberOfStars;
    }

    void setNumberOfStars(int numberOfStars) {
        this.numberOfStars = numberOfStars;
    }

    public int getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public String toString() {
        return "Level{" +
                "id=" + id +
                ", dimension=" + dimension +
                ", numberOfStars=" + numberOfStars +
                ", gameMode=" + gameMode +
                '}';
    }
}
