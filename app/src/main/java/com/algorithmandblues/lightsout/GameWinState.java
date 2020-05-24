package com.algorithmandblues.lightsout;

import android.os.Parcel;
import android.os.Parcelable;

public class GameWinState implements Parcelable {

    private int id;
    private int dimension;
    private String originalStartState;
    private String toggledBulbs;
    private int numberOfMoves;
    private int numberOfStars;
    private int gameMode;
    private long timeStampMs;

    public GameWinState() {
    }

    protected GameWinState(Parcel in) {
        id = in.readInt();
        dimension = in.readInt();
        originalStartState = in.readString();
        toggledBulbs = in.readString();
        numberOfMoves = in.readInt();
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
        dest.writeInt(numberOfMoves);
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

    public String getOriginalStartState() {
        return this.originalStartState;
    }

    public void setOriginalStartState(String originalStartState) {
        this.originalStartState = originalStartState;
    }

    public String getToggledBulbs() {
        return this.toggledBulbs;
    }

    public void setToggledBulbs(String toggledBulbs) {
        this.toggledBulbs = toggledBulbs;
    }

    public int getNumberOfMoves() {
        return this.numberOfMoves;
    }

    public void setNumberOfMoves(int numberOfMoves) {
        this.numberOfMoves = numberOfMoves;
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

    @Override
    public String toString() {
        return "GameWinState{" +
                "id=" + id +
                ", dimension=" + dimension +
                ", originalStartState='" + originalStartState + '\'' +
                ", toggledBulbs='" + toggledBulbs + '\'' +
                ", numberOfMoves=" + numberOfMoves +
                ", numberOfStars=" + numberOfStars +
                ", gameMode=" + gameMode +
                ", timeStampMs=" + timeStampMs +
                '}';
    }
}
