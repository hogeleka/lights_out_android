package com.algorithmandblues.lightsout;

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

    GameWinState() {
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

    void setOriginalStartState(String originalStartState) {
        this.originalStartState = originalStartState;
    }

    String getToggledBulbs() {
        return this.toggledBulbs;
    }

    void setToggledBulbs(String toggledBulbs) {
        this.toggledBulbs = toggledBulbs;
    }

    String getOriginalBulbConfiguration() {
        return originalBulbConfiguration;
    }

    void setOriginalBulbConfiguration(String originalBulbConfiguration) {
        this.originalBulbConfiguration = originalBulbConfiguration;
    }

    int getNumberOfMoves() {
        return this.numberOfMoves;
    }

    void setNumberOfMoves(int numberOfMoves) {
        this.numberOfMoves = numberOfMoves;
    }

    int getNumberOfHintsUsed() {
        return numberOfHintsUsed;
    }

    void setNumberOfHintsUsed(int numberOfHintsUsed) {
        this.numberOfHintsUsed = numberOfHintsUsed;
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

    long getTimeStampMs() {
        return this.timeStampMs;
    }

    void setTimeStampMs(long timeStampMs) {
        this.timeStampMs = timeStampMs;
    }

    @Override
    public String toString() {
        return "GameWinState{" +
                "id=" + this.id +
                ", dimension=" + this.dimension +
                ", originalStartState='" + this.originalStartState + '\'' +
                ", toggledBulbs='" + this.toggledBulbs + '\'' +
                ", originalBulbConfiguration='" + this.originalBulbConfiguration + '\'' +
                ", numberOfMoves=" + this.numberOfMoves +
                ", numberOfHintsUsed=" + this.numberOfHintsUsed +
                ", numberOfStars=" + this.numberOfStars +
                ", gameMode=" + this.gameMode +
                ", timeStampMs=" + this.timeStampMs +
                '}';
    }
}
