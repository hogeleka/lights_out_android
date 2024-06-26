package com.algorithmandblues.lightsout.database;

import android.os.Parcel;
import android.os.Parcelable;

public class GameWinState implements Parcelable{

    private int id;
    private int dimension;
    private String originalStartState;
    private String toggledBulbs;
    private String originalBulbConfiguration;
    private int numberOfMoves;
    private int numberOfHintsUsed;
    private int numberOfStars;
    private int gameMode;
    private long timeStampMs;
    private String moveCounterPerBulbString;
    private int originalBoardPower;

    public GameWinState() {
    }


    private GameWinState(Parcel in) {
        id = in.readInt();
        dimension = in.readInt();
        originalStartState = in.readString();
        toggledBulbs = in.readString();
        originalBulbConfiguration = in.readString();
        numberOfMoves = in.readInt();
        numberOfHintsUsed = in.readInt();
        numberOfStars = in.readInt();
        gameMode = in.readInt();
        timeStampMs = in.readLong();
        moveCounterPerBulbString = in.readString();
        originalBoardPower = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(dimension);
        dest.writeString(originalStartState);
        dest.writeString(toggledBulbs);
        dest.writeString(originalBulbConfiguration);
        dest.writeInt(numberOfMoves);
        dest.writeInt(numberOfHintsUsed);
        dest.writeInt(numberOfStars);
        dest.writeInt(gameMode);
        dest.writeLong(timeStampMs);
        dest.writeString(moveCounterPerBulbString);
        dest.writeInt(originalBoardPower);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GameWinState> CREATOR = new Creator<GameWinState>() {
        @Override
        public GameWinState createFromParcel(Parcel in) {
            return new GameWinState(in);
        }

        @Override
        public GameWinState[] newArray(int size) {
            return new GameWinState[size];
        }
    };

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

    String getOriginalStartState() {
        return this.originalStartState;
    }

    public void setOriginalStartState(String originalStartState) {
        this.originalStartState = originalStartState;
    }

    String getToggledBulbs() {
        return this.toggledBulbs;
    }

    public void setToggledBulbs(String toggledBulbs) {
        this.toggledBulbs = toggledBulbs;
    }

    public String getOriginalBulbConfiguration() {
        return originalBulbConfiguration;
    }

    public void setOriginalBulbConfiguration(String originalBulbConfiguration) {
        this.originalBulbConfiguration = originalBulbConfiguration;
    }

    public int getNumberOfMoves() {
        return this.numberOfMoves;
    }

    public void setNumberOfMoves(int numberOfMoves) {
        this.numberOfMoves = numberOfMoves;
    }

    public int getNumberOfHintsUsed() {
        return numberOfHintsUsed;
    }

    public void setNumberOfHintsUsed(int numberOfHintsUsed) {
        this.numberOfHintsUsed = numberOfHintsUsed;
    }

    public int getNumberOfStars() {
        return this.numberOfStars;
    }

    public void setNumberOfStars(int numberOfStars) {
        this.numberOfStars = numberOfStars;
    }


    public int getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public long getTimeStampMs() {
        return this.timeStampMs;
    }

    public void setTimeStampMs(long timeStampMs) {
        this.timeStampMs = timeStampMs;
    }

    public String getMoveCounterPerBulbString() {
        return this.moveCounterPerBulbString;
    }

    public void setMoveCounterPerBulbString(String moveCounterPerBulbString) {
        this.moveCounterPerBulbString = moveCounterPerBulbString;
    }

    public int getOriginalBoardPower() {
        return this.originalBoardPower;
    }

    public void setOriginalBoardPower(int originalBoardPower) {
        this.originalBoardPower = originalBoardPower;
    }

    @Override
    public String toString() {
        return "GameWinState{" +
                "id=" + id +
                ", dimension=" + dimension +
                ", originalStartState='" + originalStartState + '\'' +
                ", toggledBulbs='" + toggledBulbs + '\'' +
                ", originalBulbConfiguration='" + originalBulbConfiguration + '\'' +
                ", numberOfMoves=" + numberOfMoves +
                ", numberOfHintsUsed=" + numberOfHintsUsed +
                ", numberOfStars=" + numberOfStars +
                ", gameMode=" + gameMode +
                ", timeStampMs=" + timeStampMs +
                ", moveCounterPerBulbString='" + moveCounterPerBulbString + '\'' +
                ", originalBoardPower=" + originalBoardPower +
                '}';
    }
}
