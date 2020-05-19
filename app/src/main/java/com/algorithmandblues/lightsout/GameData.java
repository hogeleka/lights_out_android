package com.algorithmandblues.lightsout;

public class GameData {

    private int dimension;
    private String startState;
    private String toggledBulbsState;
    private String undoStackString;
    private String redoStackString;


    public GameData() {
    }

    public GameData(int dimension, String startState, String toggledBulbsState, String undoStackString, String redoStackString) {
        this.dimension = dimension;
        this.startState = startState;
        this.toggledBulbsState = toggledBulbsState;
        this.undoStackString = undoStackString;
        this.redoStackString = redoStackString;
    }


    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public String getStartState() {
        return startState;
    }

    public void setStartState(String startState) {
        this.startState = startState;
    }

    public String getToggledBulbsState() {
        return toggledBulbsState;
    }

    public void setToggledBulbsState(String toggledBulbsState) {
        this.toggledBulbsState = toggledBulbsState;
    }

    public String getUndoStackString() {
        return undoStackString;
    }

    public void setUndoStackString(String undoStackString) {
        this.undoStackString = undoStackString;
    }

    public String getRedoStackString() {
        return redoStackString;
    }

    public void setRedoStackString(String redoStackString) {
        this.redoStackString = redoStackString;
    }

    @Override
    public String toString() {
        return "GameData{" +
                "dimension=" + dimension +
                ", startState='" + startState + '\'' +
                ", toggledBulbsState='" + toggledBulbsState + '\'' +
                ", undoStackString='" + undoStackString + '\'' +
                ", redoStackString='" + redoStackString + '\'' +
                '}';
    }
}
