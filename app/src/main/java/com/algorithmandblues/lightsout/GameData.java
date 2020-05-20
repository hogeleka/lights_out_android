package com.algorithmandblues.lightsout;

public class GameData {

    private int dimension;
    private String originalStartState;
    private String toggledBulbsState;
    private String undoStackString;
    private String redoStackString;


    public GameData() {
    }

    public GameData(int dimension, String originalStartState, String toggledBulbsState, String undoStackString, String redoStackString) {
        this.dimension = dimension;
        this.originalStartState = originalStartState;
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

    public String getOriginalStartState() {
        return originalStartState;
    }

    public void setOriginalStartState(String originalStartState) {
        this.originalStartState = originalStartState;
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
                ", originalStartState='" + originalStartState + '\'' +
                ", toggledBulbsState='" + toggledBulbsState + '\'' +
                ", undoStackString='" + undoStackString + '\'' +
                ", redoStackString='" + redoStackString + '\'' +
                '}';
    }
}
