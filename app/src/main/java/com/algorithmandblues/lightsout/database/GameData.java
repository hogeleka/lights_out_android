package com.algorithmandblues.lightsout.database;

public class GameData {

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
    private String moveCounterPerBulbString;
    private String originalIndividualBulbStatus;

    public GameData() {
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

    public String getMoveCounterPerBulbString() {
        return this.moveCounterPerBulbString;
    }

    public void setMoveCounterPerBulbString(String moveCounterPerBulbString) {
        this.moveCounterPerBulbString = moveCounterPerBulbString;
    }

    public String getOriginalIndividualBulbStatus() {
        return this.originalIndividualBulbStatus;
    }

    public void setOriginalIndividualBulbStatus(String originalIndividualBulbStatus) {
        this.originalIndividualBulbStatus = originalIndividualBulbStatus;
    }

    @Override
    public String toString() {
        return "GameData{" +
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
                ", moveCounterPerBulbString='" + moveCounterPerBulbString + '\'' +
                ", originalIndividualBulbStatus='" + originalIndividualBulbStatus + '\'' +
                '}';
    }
}
