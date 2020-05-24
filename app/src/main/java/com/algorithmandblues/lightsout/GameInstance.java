package com.algorithmandblues.lightsout;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.GridLayout;

import java.util.Arrays;
import java.util.Stack;

public class GameInstance extends BaseObservable {

    private static final String TAG = GameInstance.class.getSimpleName();
    private int dimension;
    private int moveCounter;
    private byte[] toggledBulbs;
    private byte[] originalStartState;
    private byte[] individualBulbStatus;
    private Stack<Integer> undoStack;
    private Stack<Integer> redoStack;
    private GridLayout grid;
    private boolean isShowingSolution;
    private boolean isUndoStackEmpty;
    private boolean isRedoStackEmpty;
    private boolean isGameOver;
    private MediaPlayer onSound;
    private MediaPlayer offSound;

    private static int BULB_GAP;
    private static final double SCREEN_WIDTH_PERCENTAGE_FOR_BULB_GAP = 1.5;

    private String gameOverText;
    private static String GAME_IS_OVER;
    private static String GAME_IS_NOT_OVER;

    public GameInstance(Context context, final int dimension, final byte[] originalStartState, final byte[]
            toggledBulbs, final Stack<Integer> undoStack, final Stack<Integer> redoStack, final int moveCounter) {
        this.dimension = dimension;
        this.originalStartState = originalStartState;
        this.toggledBulbs = toggledBulbs;
        this.individualBulbStatus = new byte[this.dimension*this.dimension];
        this.undoStack = (Stack<Integer>) undoStack.clone();
        this.redoStack = (Stack<Integer>) undoStack.clone();
        this.isUndoStackEmpty = undoStack.isEmpty();
        this.isRedoStackEmpty = redoStack.isEmpty();
        this.isShowingSolution = false;
        this.isGameOver = false;
        this.onSound = MediaPlayer.create(context, R.raw.switchon);
        this.offSound = MediaPlayer.create(context, R.raw.switchoff);

        grid = new GridLayout(context);
        grid.setRowCount(this.dimension);
        grid.setColumnCount(this.dimension);

        this.drawGameBoard(context);
        this.setStartState();
        this.updateIndividualBulbStatus();

        //Always initialize after game board is drawn
        this.setMoveCounter(moveCounter);

        GAME_IS_OVER = context.getString(R.string.all_lights_off);
        GAME_IS_NOT_OVER = context.getString(R.string.turn_off_all_the_lights);
        this.gameOverText = GAME_IS_NOT_OVER;
    }

    private GridLayout.LayoutParams createBulbParameters(int r, int c, int length) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        if (c != this.dimension-1) {
            params.rightMargin = BULB_GAP / 2;
        } else {
            params.rightMargin = BULB_GAP;
        }

        if (c == 0) {
            params.leftMargin = BULB_GAP;
        } else {
            params.leftMargin = BULB_GAP / 2;
        }

        if (r == dimension - 1) {
            params.bottomMargin = BULB_GAP;
        }

        params.height = length;
        params.width = length;

        params.topMargin = BULB_GAP;
        params.setGravity(Gravity.CENTER);
        params.columnSpec = GridLayout.spec(c);
        params.rowSpec = GridLayout.spec(r);

        return params;
    }

    private void clickBulb(Bulb b) {
        if(this.isShowingSolution) {
            if (b.isBorderHighlighted()) {
                b.unHighlightBorder();
            } else {
                b.highlightBorder();
            }
        }

        b.toggle();
        this.incrementMoveCounter();
        individualBulbStatus[b.getBulbId()] = b.isOnOrOff();

        Log.d(TAG, "Bulb: " +b.toString());
        int bulbIndex = b.getBulbId();
        int row = bulbIndex / dimension;
        int col = bulbIndex % dimension;

        //toggle left neighbour
        if (col != 0) {
            int left = (row * dimension) + (col - 1);
            ((Bulb) grid.getChildAt(left)).toggle();
            individualBulbStatus[left] = b.isOnOrOff();
        }

        //toggle right neighbour
        if (col != dimension-1) {
            int right = (row * dimension) + (col + 1);
            ((Bulb) grid.getChildAt(right)).toggle();
            individualBulbStatus[right] = b.isOnOrOff();
        }

        //toggle top neighbour
        if (row != 0) {
            int top = (row - 1) * dimension + col;
            ((Bulb) grid.getChildAt(top)).toggle();
            individualBulbStatus[top] = b.isOnOrOff();
        }

        //toggle bottom neighbour
        if (row != dimension - 1) {
            int bottom = (row + 1) * dimension + col;
            ((Bulb) grid.getChildAt(bottom)).toggle();
            individualBulbStatus[bottom] = b.isOnOrOff();
        }

        this.updateIndividualBulbStatus();
        this.setIsGameOver(this.checkIfAllLightsAreOff());
        this.setGameOverText(this.getIsGameOver() ? GAME_IS_OVER : GAME_IS_NOT_OVER);
        if (this.getIsShowingSolution() && this.getIsGameOver()) {
            this.unHighlightAllBulbs();
        }
    }

    private void unHighlightAllBulbs() {
        for (int i = 0; i < this.dimension * this.dimension; i++) {
            ((Bulb) grid.getChildAt(i)).unHighlightBorder();
        }
    }

    private void updateIndividualBulbStatus() {
        for (int i = 0; i< dimension*dimension; i++) {
            individualBulbStatus[i] = ((Bulb) this.grid.getChildAt(i)).isOnOrOff();
        }
    }

    private boolean checkIfAllLightsAreOff() {
        for (byte status : this.individualBulbStatus) {
            if (status == 1) {
                return false;
            }
        }
        Log.d(TAG, "Game is over: All Lights are off");
        return true;
    }

    public void drawGameBoard(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        grid.removeAllViews();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.d(TAG, "Screen width: " + width);
        BULB_GAP = (int) ((SCREEN_WIDTH_PERCENTAGE_FOR_BULB_GAP / 100) * width);
        Log.d(TAG, "Bulb gap: " + BULB_GAP);
        int size = Math.min(width, height);
        int marginCumulativeWidth = (dimension + 1) * BULB_GAP;
        int bulbWidth = (size - marginCumulativeWidth) / dimension;

        int id = 0;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                final Bulb bulb = new Bulb(context, id);
                bulb.setLayoutParams(this.createBulbParameters(row, col, bulbWidth));
                bulb.setOnClickListener(v -> {
                    recordBulbClick(bulb.getBulbId());
                    handleStackOnBulbClick(bulb);
                    playLightSwitchSound(bulb);
                    clickBulb(bulb);
                });
                grid.addView(bulb);
                id++;
            }
        }
        Log.i(TAG, "GRID num buttons:" + Integer.toString(grid.getChildCount()));
    }

    public void recordBulbClick(int id) {
        byte newVal = (byte) (1 - this.toggledBulbs[id]);
        this.toggledBulbs[id] = newVal;
    }

    public void playLightSwitchSound(Bulb bulb) {
        if (bulb.isOn()) {
            this.playLightOffSound();
        } else {
            this.playLightOnSound();
        }
    }


    public void playLightOnSound() {
        this.onSound.start();
    }

    public void playLightOffSound() {
        this.offSound.start();
    }

    public void handleStackOnBulbClick(Bulb bulb) {
        this.addToUndoStack(bulb.getBulbId());
        this.clearRedoStack();
    }

    public void resetBoardToState(byte[] state, int moveCounter, Stack<Integer> undo, Stack<Integer> redo) {
        this.toggledBulbs = Arrays.copyOf(state, this.dimension * this.dimension);
        Stack<Integer> undoStack = (Stack<Integer>) undo.clone();
        Stack<Integer> redoStack = (Stack<Integer>) redo.clone();

        this.setStartState();
        this.setUndoStack(undoStack);
        this.setRedoStack(redoStack);
        this.setMoveCounter(moveCounter);
        this.setGameOverText(GAME_IS_NOT_OVER);
        this.setIsGameOver(false);
        this.unHighlightAllBulbs();

        Log.d(TAG, "Board Reset complete. \nNew Start State:" + Arrays.toString(state) +
                "\nmoveCounter=" + moveCounter + "\nundoStack=" + undoStack + "\nredoStack=" + redoStack);
    }

    public void highlightSolution(byte[] solution) {
        for(int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                ((Bulb) grid.getChildAt(i)).highlightBorder();
            }
        }

        this.setIsShowingSolution(true);
        Log.d(TAG, "Highlighting Solution:" + Arrays.toString(solution));

    }

    public void unHighlightSolution(byte[] solution) {
        for(int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                ((Bulb) grid.getChildAt(i)).unHighlightBorder();
            }
        }

        this.setIsShowingSolution(false);
        Log.d(TAG, "Highlighting Solution:" + Arrays.toString(solution));
    }

    public void setStartState() {
        for (int i = 0; i < this.dimension*this.dimension; i++) {
            ((Bulb) grid.getChildAt(i)).setOn(true);
        }

        for (int i = 0; i < this.toggledBulbs.length; i++) {
            if (this.toggledBulbs[i] == 0) {
                clickBulb((Bulb)(grid.getChildAt(i)));
            }
        }

        Log.d(TAG, "Setting Start State:" + Arrays.toString(toggledBulbs));
    }

    private void addToUndoStack(int id) {
        this.undoStack.push(id);
        this.setIsUndoStackEmpty(false);
        Log.d(TAG, "Added " + id + " to current undo stack: " + this.undoStack.toString());
    }

    private void addToRedoStack(int id) {
        this.redoStack.push(id);
        this.setIsRedoStackEmpty(false);
        Log.d(TAG, "Added " + id + " to current redo stack: " + this.redoStack.toString());
    }

    public void removeFromUndoStack() {
        int elementPopped = this.undoStack.pop();
        int id = ((Bulb) grid.getChildAt(elementPopped)).getBulbId();
        if(this.getIsGameOver()) {
            this.setIsShowingSolution(false);
        }
        this.recordBulbClick(id);
        this.addToRedoStack(elementPopped);
        this.playLightSwitchSound(((Bulb) grid.getChildAt(elementPopped)));
        this.clickBulb(((Bulb) grid.getChildAt(elementPopped)));

        Log.d(TAG, "Removed " + id + " from current undo stack: " + this.undoStack.toString());

        if (this.undoStack.isEmpty()) {
            this.setIsUndoStackEmpty(true);
        }
    }

    public void removeFromRedoStack() {
        int elementPopped = this.redoStack.pop();
        int id = ((Bulb) grid.getChildAt(elementPopped)).getBulbId();
        this.recordBulbClick(id);
        this.addToUndoStack(id);
        this.playLightSwitchSound(((Bulb) grid.getChildAt(elementPopped)));
        this.clickBulb(((Bulb) grid.getChildAt(elementPopped)));

        Log.d(TAG, "Removed " + id + " from current redo stack: " + this.redoStack.toString());


        if (this.redoStack.isEmpty()) {
            this.setIsRedoStackEmpty(true);
        }
    }

    public void clearUndoStack() {
        this.undoStack.clear();
        this.setIsUndoStackEmpty(true);
    }

    public void clearRedoStack() {
        this.redoStack.clear();
        this.setIsRedoStackEmpty(true);
    }

    public GridLayout getGrid() {
        return this.grid;
    }

    public void setIsUndoStackEmpty(boolean undoStackEmpty) {
        this.isUndoStackEmpty = undoStackEmpty;
        notifyPropertyChanged(BR.isUndoStackEmpty);
    }

    public void setIsRedoStackEmpty(boolean redoStackEmpty) {
        this.isRedoStackEmpty = redoStackEmpty;
        notifyPropertyChanged(BR.isRedoStackEmpty);
    }

    public void incrementMoveCounter() {
        this.incrementMoveCounter(1);
    }

    public void decrementMoveCounter() {
        this.decrementMoveCounter(1);
    }

    public void incrementMoveCounter(int incrementValue) {
        this.setMoveCounter(this.moveCounter + incrementValue);
    }

    public void decrementMoveCounter(int decrementValue) {
        this.setMoveCounter(this.moveCounter - decrementValue);
    }

    @Bindable
    public boolean getIsUndoStackEmpty() {
        return this.isUndoStackEmpty;
    }

    @Bindable
    public boolean getIsRedoStackEmpty() {
        return this.isRedoStackEmpty;
    }

    public byte[] getToggledBulbs() {
        return this.toggledBulbs;
    }

    public void setToggledBulbs(byte[] toggledBulbs) {
        this.toggledBulbs = toggledBulbs;
    }

    public byte[] getOriginalStartState() {
        return this.originalStartState;
    }

    public void setOriginalStartState(byte[] originalStartState) {
        this.originalStartState = originalStartState;
    }

    public Stack<Integer> getUndoStack() {
        return this.undoStack;
    }

    public void setUndoStack(Stack<Integer> undoStack) {
        this.undoStack = undoStack;
        if(this.undoStack.isEmpty()) {
            this.setIsUndoStackEmpty(true);
        } else {
            this.setIsUndoStackEmpty(false);
        }
    }

    public Stack<Integer> getRedoStack() {
        return this.redoStack;
    }

    public void setRedoStack(Stack<Integer> redoStack) {
        this.redoStack = redoStack;
        if(this.redoStack.isEmpty()) {
            this.setIsRedoStackEmpty(true);
        } else {
            this.setIsRedoStackEmpty(false);
        }
    }

    public int getDimension() {
        return this.dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    @Bindable
    public boolean getIsShowingSolution() {
        return this.isShowingSolution;
    }

    public void setIsShowingSolution(boolean showingSolution) {
        this.isShowingSolution = showingSolution;
        notifyPropertyChanged(BR.isShowingSolution);
    }

    @Bindable
    public boolean getIsGameOver() {
        return this.isGameOver;
    }

    public void setIsGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
        notifyPropertyChanged(BR.isGameOver);
    }

    @Bindable
    public String getGameOverText() {
        return this.gameOverText;
    }

    @Bindable
    public int getMoveCounter() {
        return moveCounter;
    }

    public void setMoveCounter(int moveCounter) {
        this.moveCounter = moveCounter;
        notifyPropertyChanged(BR.moveCounter);
    }

    public void setGameOverText(String gameOverText) {
        this.gameOverText = gameOverText;
        notifyPropertyChanged(BR.gameOverText);
    }
}