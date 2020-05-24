package com.algorithmandblues.lightsout;

public class GameDataObject {

    private int id;
    private int dimension;
    private String originalStartState;
    private String toggledBulbsState;
    private String lastSavedState;
    private String undoStackString;
    private String redoStackString;
    private int gameMode;
    private boolean hasSeenSolution;
    private int moveCounter;
    private int numberOfHintsUsed;

    public GameDataObject() {
    }

    public GameDataObject(int id, int dimension, String originalStartState, String toggledBulbsState,
                          String lastSavedState, String undoStackString, String redoStackString,
                          int gameMode, boolean hasSeenSolution, int moveCounter, int numberOfHintsUsed) {
        this.id = id;
        this.dimension = dimension;
        this.originalStartState = originalStartState;
        this.toggledBulbsState = toggledBulbsState;
        this.lastSavedState = lastSavedState;
        this.undoStackString = undoStackString;
        this.redoStackString = redoStackString;
        this.gameMode = gameMode;
        this.hasSeenSolution = hasSeenSolution;
        this.moveCounter = moveCounter;
        this.numberOfHintsUsed = numberOfHintsUsed;
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

    public String getOriginalStartState() {
        return this.originalStartState;
    }

    public void setOriginalStartState(String originalStartState) {
        this.originalStartState = originalStartState;
    }

    public String getToggledBulbsState() {
        return this.toggledBulbsState;
    }

    public void setToggledBulbsState(String toggledBulbsState) {
        this.toggledBulbsState = toggledBulbsState;
    }

    public String getLastSavedState() {
        return this.lastSavedState;
    }

    public void setLastSavedState(String lastSavedState) {
        this.lastSavedState = lastSavedState;
    }

    public String getUndoStackString() {
        return this.undoStackString;
    }

    public void setUndoStackString(String undoStackString) {
        this.undoStackString = undoStackString;
    }

    public String getRedoStackString() {
        return this.redoStackString;
    }

    public void setRedoStackString(String redoStackString) {
        this.redoStackString = redoStackString;
    }

    public int getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public boolean getHasSeenSolution() {
        return this.hasSeenSolution;
    }

    public void setHasSeenSolution(boolean hasSeenSolution) {
        this.hasSeenSolution = hasSeenSolution;
    }

    public int getMoveCounter() {
        return this.moveCounter;
    }

    public void setMoveCounter(int moveCounter) {
        this.moveCounter = moveCounter;
    }

    public int getNumberOfHintsUsed() {
        return this.numberOfHintsUsed;
    }

    public void setNumberOfHintsUsed(int numberOfHintsUsed) {
        this.numberOfHintsUsed = numberOfHintsUsed;
    }

    @Override
    public String toString() {
        return "GameDataObject{" +
                "id=" + id +
                ", dimension=" + dimension +
                ", originalStartState='" + originalStartState + '\'' +
                ", toggledBulbsState='" + toggledBulbsState + '\'' +
                ", lastSavedState='" + lastSavedState + '\'' +
                ", undoStackString='" + undoStackString + '\'' +
                ", redoStackString='" + redoStackString + '\'' +
                ", gameMode=" + gameMode +
                ", hasSeenSolution=" + hasSeenSolution +
                ", moveCounter=" + moveCounter +
                ", numberOfHintsUsed=" + numberOfHintsUsed +
                '}';
    }
}
