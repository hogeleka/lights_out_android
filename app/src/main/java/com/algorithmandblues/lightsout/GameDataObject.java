package com.algorithmandblues.lightsout;

public class GameDataObject {

    private int id;
    private int dimension;
    private String originalStartState;
    private String toggledBulbsState;
    private String undoStackString;
    private String redoStackString;
    private int gameMode;
    private boolean hasSeenSolution;
    private int moveCounter;
    private int numberOfHintsUsed;

    GameDataObject() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int getDimension() {
        return this.dimension;
    }

    void setDimension(int dimension) {
        this.dimension = dimension;
    }

    String getOriginalStartState() {
        return this.originalStartState;
    }

    void setOriginalStartState(String originalStartState) {
        this.originalStartState = originalStartState;
    }

    String getToggledBulbsState() {
        return this.toggledBulbsState;
    }

    void setToggledBulbsState(String toggledBulbsState) {
        this.toggledBulbsState = toggledBulbsState;
    }

    String getUndoStackString() {
        return this.undoStackString;
    }

    void setUndoStackString(String undoStackString) {
        this.undoStackString = undoStackString;
    }

    String getRedoStackString() {
        return this.redoStackString;
    }

    void setRedoStackString(String redoStackString) {
        this.redoStackString = redoStackString;
    }

    int getGameMode() {
        return this.gameMode;
    }

    void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    boolean getHasSeenSolution() {
        return this.hasSeenSolution;
    }

    void setHasSeenSolution(boolean hasSeenSolution) {
        this.hasSeenSolution = hasSeenSolution;
    }

    int getMoveCounter() {
        return this.moveCounter;
    }

    void setMoveCounter(int moveCounter) {
        this.moveCounter = moveCounter;
    }

    int getNumberOfHintsUsed() {
        return this.numberOfHintsUsed;
    }

    void setNumberOfHintsUsed(int numberOfHintsUsed) {
        this.numberOfHintsUsed = numberOfHintsUsed;
    }

    @Override
    public String toString() {
        return "GameDataObject{" +
                "id=" + id +
                ", dimension=" + dimension +
                ", originalStartState='" + originalStartState + '\'' +
                ", toggledBulbsState='" + toggledBulbsState + '\'' +
                ", undoStackString='" + undoStackString + '\'' +
                ", redoStackString='" + redoStackString + '\'' +
                ", gameMode=" + gameMode +
                ", hasSeenSolution=" + hasSeenSolution +
                ", moveCounter=" + moveCounter +
                ", numberOfHintsUsed=" + numberOfHintsUsed +
                '}';
    }
}
