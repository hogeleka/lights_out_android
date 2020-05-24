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

import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Stack;

public class GameInstance extends BaseObservable {

    private static final String TAG = GameInstance.class.getSimpleName();
    private static final double SCREEN_WIDTH_PERCENTAGE_FOR_BULB_GAP = 1.5;
    private static int BULB_GAP;
    private static String GAME_IS_OVER;
    private static String GAME_IS_NOT_OVER;
    private int gameMode;
    private int dimension;
    private int moveCounter;
    private int hintsAllowed;
    private int hintsUsed;
    private int hintsLeft;
    private int currentPowerConsumption;
    private int totalBoardPower;
    private boolean isShowingSolution;
    private boolean isUndoStackEmpty;
    private boolean isRedoStackEmpty;
    private boolean isGameOver;
    private boolean hasSeenSolution;
    private boolean hasMadeAtLeastOneMove;
    private byte[] currentToggledBulbs;
    private byte[] originalStartState;
    private byte[] individualBulbStatus;
    private Stack<Integer> undoStack;
    private Stack<Integer> redoStack;
    private GridLayout grid;
    private MediaPlayer onSound;
    private MediaPlayer offSound;

    PropertyChangeSupport gameOverChange = new PropertyChangeSupport(this);
    private static String gameOverPropertyName = "isGameOver";

    private String gameOverText;

    GameInstance(Context context, final GameDataObject gameDataObject) {

        this.gameMode = gameDataObject.getGameMode();
        this.dimension = gameDataObject.getDimension();
        this.originalStartState = GameDataUtil.stringToByteArray(gameDataObject.getOriginalStartState());
        this.currentToggledBulbs = GameDataUtil.stringToByteArray(gameDataObject.getToggledBulbsState());
        this.individualBulbStatus = new byte[this.dimension*this.dimension];
        this.undoStack = GameDataUtil.stringToIntegerStack(gameDataObject.getUndoStackString());
        this.redoStack = GameDataUtil.stringToIntegerStack(gameDataObject.getRedoStackString());
        this.hintsAllowed = this.dimension * this.dimension;
        this.hintsUsed = gameDataObject.getNumberOfHintsUsed();
        this.hasSeenSolution = gameDataObject.getHasSeenSolution();
        this.isUndoStackEmpty = this.undoStack.isEmpty();
        this.isRedoStackEmpty = this.redoStack.isEmpty();
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
        // Always call when this.updateIndividualBulbStatus() is called and CurrentBoardPower is initialized.
        this.totalBoardPower = this.getCurrentPowerConsumption();

        //Always initialize after game board is drawn
        this.setMoveCounter(gameDataObject.getMoveCounter());
        this.hasMadeAtLeastOneMove = false;

        GAME_IS_OVER = context.getString(R.string.all_lights_off);
        GAME_IS_NOT_OVER = context.getString(R.string.turn_off_all_the_lights);
        this.gameOverText = GAME_IS_NOT_OVER;
    }

    private void drawGameBoard(Context context) {
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

        if (!this.getHasMadeAtLeastOneMove()) {
            this.setHasMadeAtLeastOneMove(true);
        }

        individualBulbStatus[b.getBulbId()] = b.isOnOrOff();

        Log.d(TAG, "Bulb: " +b.toString());
        int bulbIndex = b.getBulbId();
        int row = bulbIndex / dimension;
        int col = bulbIndex % dimension;

        //toggle left neighbour
        if (col != 0) {
            int left = (row * dimension) + (col - 1);
            ((Bulb) grid.getChildAt(left)).toggle();
        }

        //toggle right neighbour
        if (col != dimension-1) {
            int right = (row * dimension) + (col + 1);
            ((Bulb) grid.getChildAt(right)).toggle();
        }

        //toggle top neighbour
        if (row != 0) {
            int top = (row - 1) * dimension + col;
            ((Bulb) grid.getChildAt(top)).toggle();
        }

        //toggle bottom neighbour
        if (row != dimension - 1) {
            int bottom = (row + 1) * dimension + col;
            ((Bulb) grid.getChildAt(bottom)).toggle();
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
        this.currentPowerConsumption = 0;
        for (int i = 0; i< dimension*dimension; i++) {
            individualBulbStatus[i] = ((Bulb) this.grid.getChildAt(i)).isOnOrOff();
            this.setCurrentPowerConsumption(this.getCurrentPowerConsumption() + individualBulbStatus[i]);
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

    private void recordBulbClick(int id) {
        byte newVal = (byte) (1 - this.currentToggledBulbs[id]);
        this.currentToggledBulbs[id] = newVal;
    }

    private void playLightSwitchSound(Bulb bulb) {
        if (bulb.isOn()) {
            this.playLightOffSound();
        } else {
            this.playLightOnSound();
        }
    }


    private void playLightOnSound() {
        this.onSound.start();
    }

    private void playLightOffSound() {
        this.offSound.start();
    }

    private void handleStackOnBulbClick(Bulb bulb) {
        this.addToUndoStack(bulb.getBulbId());
        this.clearRedoStack();
    }

    void resetBoardToState(byte[] state, int moveCounter, Stack<Integer> undo, Stack<Integer> redo) {
        this.currentToggledBulbs = Arrays.copyOf(state, this.dimension * this.dimension);
        this.setStartState();
        this.updateIndividualBulbStatus();
        this.setTotalBoardPower(this.getCurrentPowerConsumption());
        this.setUndoStack(undo);
        this.setRedoStack(redo);
        this.setMoveCounter(moveCounter);
        this.setGameOverText(GAME_IS_NOT_OVER);
        this.setIsGameOver(false);
        this.setHasMadeAtLeastOneMove(false);
        this.unHighlightAllBulbs();

        Log.d(TAG, "Board Reset complete. \nNew Start State:" + Arrays.toString(state) +
                "\nmoveCounter=" + moveCounter + "\nundoStack=" + undoStack + "\nredoStack=" + redoStack +
                "\nTotalBoardPower=" + this.getTotalBoardPower());
    }

    void highlightSolution(byte[] solution) {
        for(int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                ((Bulb) grid.getChildAt(i)).highlightBorder();
            }
        }

        this.setIsShowingSolution(true);
        Log.d(TAG, "Highlighting Solution:" + Arrays.toString(solution));

    }

    void unHighlightSolution(byte[] solution) {
        for(int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                ((Bulb) grid.getChildAt(i)).unHighlightBorder();
            }
        }

        this.setIsShowingSolution(false);
        Log.d(TAG, "Highlighting Solution:" + Arrays.toString(solution));
    }

    private void setStartState() {
        for (int i = 0; i < this.dimension*this.dimension; i++) {
            ((Bulb) grid.getChildAt(i)).setOn(true);
        }

        for (int i = 0; i < this.currentToggledBulbs.length; i++) {
            if (this.currentToggledBulbs[i] == 0) {
                clickBulb((Bulb)(grid.getChildAt(i)));
            }
        }

        Log.d(TAG, "Setting Start State:" + Arrays.toString(currentToggledBulbs));
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

    void removeFromUndoStack() {
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

    void removeFromRedoStack() {
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

    private void clearRedoStack() {
        this.redoStack.clear();
        this.setIsRedoStackEmpty(true);
    }

    GridLayout getGrid() {
        return this.grid;
    }

    private void incrementMoveCounter() {
        this.incrementMoveCounter(1);
    }

    public void decrementMoveCounter() {
        this.decrementMoveCounter(1);
    }

    private void incrementMoveCounter(int incrementValue) {
        this.setMoveCounter(this.moveCounter + incrementValue);
    }

    private void decrementMoveCounter(int decrementValue) {
        this.setMoveCounter(this.moveCounter - decrementValue);
    }

    @Bindable
    public boolean getIsUndoStackEmpty() {
        return this.isUndoStackEmpty;
    }

    private void setIsUndoStackEmpty(boolean undoStackEmpty) {
        this.isUndoStackEmpty = undoStackEmpty;
        notifyPropertyChanged(BR.isUndoStackEmpty);
    }

    @Bindable
    public boolean getIsRedoStackEmpty() {
        return this.isRedoStackEmpty;
    }

    private void setIsRedoStackEmpty(boolean redoStackEmpty) {
        this.isRedoStackEmpty = redoStackEmpty;
        notifyPropertyChanged(BR.isRedoStackEmpty);
    }

    byte[] getCurrentToggledBulbs() {
        return this.currentToggledBulbs;
    }

    public void setCurrentToggledBulbs(byte[] currentToggledBulbs) {
        this.currentToggledBulbs = currentToggledBulbs;
    }

    byte[] getOriginalStartState() {
        return this.originalStartState;
    }

    void setOriginalStartState(byte[] originalStartState) {
        this.originalStartState = originalStartState;
    }

    Stack<Integer> getUndoStack() {
        return this.undoStack;
    }

    private void setUndoStack(Stack<Integer> undoStack) {
        this.undoStack = undoStack;
        if(this.undoStack.isEmpty()) {
            this.setIsUndoStackEmpty(true);
        } else {
            this.setIsUndoStackEmpty(false);
        }
    }

    Stack<Integer> getRedoStack() {
        return this.redoStack;
    }

    private void setRedoStack(Stack<Integer> redoStack) {
        this.redoStack = redoStack;
        if(this.redoStack.isEmpty()) {
            this.setIsRedoStackEmpty(true);
        } else {
            this.setIsRedoStackEmpty(false);
        }
    }

    int getDimension() {
        return this.dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    @Bindable
    public boolean getIsShowingSolution() {
        return this.isShowingSolution;
    }

    private void setIsShowingSolution(boolean showingSolution) {
        this.isShowingSolution = showingSolution;
        notifyPropertyChanged(BR.isShowingSolution);
    }

    @Bindable
    public boolean getIsGameOver() {
        return this.isGameOver;
    }

    public void setIsGameOver(boolean isGameOver) {
        boolean oldValue = this.isGameOver;
        this.isGameOver = isGameOver;
        this.gameOverChange.firePropertyChange(gameOverPropertyName, oldValue, isGameOver);
        notifyPropertyChanged(BR.isGameOver);
    }

    @Bindable
    public String getGameOverText() {
        return this.gameOverText;
    }

    private void setGameOverText(String gameOverText) {
        this.gameOverText = gameOverText;
        notifyPropertyChanged(BR.gameOverText);
    }

    @Bindable
    public int getMoveCounter() {
        return moveCounter;
    }

    private void setMoveCounter(int moveCounter) {
        this.moveCounter = moveCounter;
        notifyPropertyChanged(BR.moveCounter);
    }

    int getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public int getHintsAllowed() {
        return this.hintsAllowed;
    }

    public void setHintsAllowed(int hintsAllowed) {
        this.hintsAllowed = hintsAllowed;
    }

    @Bindable
    public int getHintsUsed() {
        return this.hintsUsed;
    }

    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

    boolean getHasSeenSolution() {
        return this.hasSeenSolution;
    }

    void setHasSeenSolution(boolean hasSeenSolution) {
        this.hasSeenSolution = hasSeenSolution;
    }

    boolean getHasMadeAtLeastOneMove() {
        return this.hasMadeAtLeastOneMove;
    }

    private void setHasMadeAtLeastOneMove(boolean hasMadeAtLeastOneMove) {
        this.hasMadeAtLeastOneMove = hasMadeAtLeastOneMove;
    }

    @Bindable
    public int getCurrentPowerConsumption() {
        return currentPowerConsumption;
    }

    private void setCurrentPowerConsumption(int currentPowerConsumption) {
        this.currentPowerConsumption = currentPowerConsumption;
        notifyPropertyChanged(BR.currentPowerConsumption);
    }

    public int getTotalBoardPower() {
        return totalBoardPower;
    }

    public void setTotalBoardPower(int totalBoardPower) {
        this.totalBoardPower = totalBoardPower;
    }
}