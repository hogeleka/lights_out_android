package com.algorithmandblues.lightsout;

import android.content.Context;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class GameInstance extends BaseObservable {

    private static final String TAG = GameInstance.class.getSimpleName();
    private boolean lastBulbToggleIsOn;
    private boolean isShowingSolution;
    private boolean isUndoStackEmpty;
    private boolean isRedoStackEmpty;
    private boolean isGameOver;
    private boolean hasSeenSolution;
    private boolean hasMadeAtLeastOneMove;
    private int bulbGap;
    public int boardPadding;
    public int gameMode;
    private int dimension;
    private int moveCounter;
    private int hintsAllowed;
    private int hintsUsed;
    private int hintsLeft;
    private int currentPowerConsumption;
    private int originalBoardPower;
    private int starCount;
    private int[] moveCounterPerBulb;
    private byte[] currentToggledBulbs;
    private byte[] originalStartState;
    private byte[] individualBulbStatus;
    private Stack<Integer> undoStack;
    private Stack<Integer> redoStack;
    private BoardLayout grid;
    private MediaPlayer onSound;
    private MediaPlayer offSound;
    private Context context;

    private byte[] originalIndividualBulbStatus;

    PropertyChangeSupport gameOverChange = new PropertyChangeSupport(this);
    private static final String GAME_OVER_PROPERTY_NAME = "isGameOver";
    private static final int BOARD_PADDING_RAW = 16;

    // TODO: Make them more realistic.
//    private static final Map<Integer, Integer> HINTS_ALLOWED_MAP = new HashMap<Integer, Integer>() {{
//        put(2, 5);
//        put(3, 1);
//        put(4, 1);
//        put(5, 10);
//        put(6, 12);
//        put(7, 15);
//        put(8, 25);
//        put(9, 35);
//        put(10, 40);
//    }};

    private static final Map<Integer, Integer> HINTS_ALLOWED_MAP = new HashMap<Integer, Integer>() {{
        put(2, 1);
        put(3, 3);
        put(4, 4);
        put(5, 25);
        put(6, 36);
        put(7, 49);
        put(8, 64);
        put(9, 81);
        put(10, 100);
    }};

    private static final Map<Integer, Integer> BULB_GAP_MAP = new HashMap<Integer, Integer>() {{
        put(2, 20);
        put(3, 18);
        put(4, 16);
        put(5, 14);
        put(6, 10);
        put(7, 9);
        put(8, 8);
        put(9, 7);
        put(10, 6);
    }};

    private static final Map<Integer, Integer> HINT_ICON_Size_FACTOR_MAP = new HashMap<Integer, Integer>() {{
        put(2, 30);
        put(3, 28);
        put(4, 26);
        put(5, 24);
        put(6, 22);
        put(7, 20);
        put(8, 18);
        put(9, 16);
        put(10,14);
    }};

    GameInstance(Context context, final GameDataObject gameDataObject) {
        this.context = context;
        this.gameMode = gameDataObject.getGameMode();
        this.dimension = gameDataObject.getDimension();
        this.originalStartState = GameDataUtil.stringToByteArray(gameDataObject.getOriginalStartState());
        this.currentToggledBulbs = GameDataUtil.stringToByteArray(gameDataObject.getToggledBulbsState());
        this.individualBulbStatus = new byte[this.dimension * this.dimension];
        this.moveCounterPerBulb = new int[this.dimension * this.dimension];
        this.undoStack = GameDataUtil.stringToIntegerStack(gameDataObject.getUndoStackString());
        this.redoStack = GameDataUtil.stringToIntegerStack(gameDataObject.getRedoStackString());
        this.hintsAllowed = HINTS_ALLOWED_MAP.get(this.dimension);
        this.hintsUsed = gameDataObject.getNumberOfHintsUsed();
        this.hintsLeft = this.calculateHintsLeft(this.hintsAllowed, this.hintsUsed);
        this.hasSeenSolution = gameDataObject.getHasSeenSolution();
        this.isUndoStackEmpty = this.undoStack.isEmpty();
        this.isRedoStackEmpty = this.redoStack.isEmpty();
        this.isShowingSolution = false;
        this.isGameOver = false;
        this.onSound = MediaPlayer.create(context, R.raw.switchon);
        this.offSound = MediaPlayer.create(context, R.raw.switchoff);

        grid = new BoardLayout(context);
        grid.setRowCount(this.dimension);
        grid.setColumnCount(this.dimension);

        this.drawGameBoard(context);
        this.setStartState();
        this.updateIndividualBulbStatus();

        // Always call when this.updateIndividualBulbStatus() is called.
        byte[] originalIndividualBulbStatusFromDB = GameDataUtil.stringToByteArray(gameDataObject.getOriginalIndividualBulbStatus());
        this.originalIndividualBulbStatus = this.calculateOriginalIndividualBulbStatus(originalIndividualBulbStatusFromDB);
        this.originalBoardPower = this.calculateOriginalBoardPower(this.getOriginalIndividualBulbStatus());

        // Always initialize after game board is drawn
        this.setMoveCounter(gameDataObject.getMoveCounter());
        this.moveCounterPerBulb = GameDataUtil.stringToIntegerArray(gameDataObject.getMoveCounterPerBulbString());

        this.hasMadeAtLeastOneMove = false;
        this.starCount = 0;
        this.lastBulbToggleIsOn = false;

    }

    private byte[] calculateOriginalIndividualBulbStatus(byte[] originalIndividualBulbStatusFromDB) {
        return  originalIndividualBulbStatusFromDB.length > 0 ? originalIndividualBulbStatusFromDB : Arrays.copyOf(this.getIndividualBulbStatus(), this.getIndividualBulbStatus().length);
    }

    private int calculateOriginalBoardPower(byte[] bulbStatus) {
        int boardPower = 0;
        for(int i = 0; i < bulbStatus.length; i++) {
            boardPower += bulbStatus[i] == (byte) 1 ? 1 : 0;
        }
        return boardPower;
    }
    private void drawGameBoard(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        grid.removeAllViews();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.d(TAG, "Screen width: " + width);

        this.boardPadding = this.getPixels(BOARD_PADDING_RAW);
        this.bulbGap = this.getPixels(BULB_GAP_MAP.get(this.dimension));
        Log.d(TAG, "Bulb gap: " + this.bulbGap);
        int size = Math.min(width, height);
        int marginCumulativeWidth = (dimension - 1) * this.bulbGap + 2*this.boardPadding;
        int bulbSize = (size - marginCumulativeWidth) / dimension;


        int id = 0;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {

                RelativeLayout bulbLayout = new RelativeLayout(context);
                BoardLayout.LayoutParams params = this.createBulbLayoutParameters(row, col, bulbSize);
                bulbLayout.setLayoutParams(params);

                ImageView hintIcon = new ImageView(context);

                int iconSize = this.getPixels(HINT_ICON_Size_FACTOR_MAP.get(this.dimension));
                RelativeLayout.LayoutParams hintParams = new RelativeLayout.LayoutParams(iconSize, iconSize);
                hintParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                hintIcon.setLayoutParams(hintParams);
                hintIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.hint));
                hintIcon.setVisibility(View.INVISIBLE);
                hintIcon.setPadding(0, this.getPixels(3), this.getPixels(3), 0);

                final Bulb bulb = new Bulb(context, id);
                bulb.setLayoutParams(new LinearLayout.LayoutParams(bulbSize, bulbSize));
                bulb.setVisibility(View.VISIBLE);
                bulb.setOnClickListener(v -> {
                    recordBulbClick(bulb.getBulbId());
                    handleStackOnBulbClick(bulb);
                    playLightSwitchSound(bulb);
                    this.incrementMoveCounterPerBulb(bulb.getBulbId());
                    clickBulb(bulb);
                });
                bulbLayout.addView(bulb);
                bulbLayout.addView(hintIcon);
                grid.addView(bulbLayout);
                id++;
            }
        }
        Log.i(TAG, "GRID num bulbs:" + Integer.toString(grid.getChildCount()));
    }

    private GridLayout.LayoutParams createBulbLayoutParameters(int r, int c, int length) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        if (c != this.dimension - 1) {
            params.rightMargin = this.bulbGap / 2;
        } else {
            params.rightMargin = this.boardPadding;
        }

        if (c == 0) {
            params.leftMargin = this.boardPadding;
        } else {
            params.leftMargin = this.bulbGap / 2;
        }

        if (r == 0) {
            params.topMargin = this.boardPadding/3;
        } else {
            params.topMargin = this.bulbGap;
        }

        params.height = length;
        params.width = length;

        params.columnSpec = GridLayout.spec(c);
        params.rowSpec = GridLayout.spec(r);

        return params;
    }

    private void clickBulb(Bulb b) {

        // If the solution is showing and border is highlighted remove the border else add the border
        if (this.getIsShowingSolution()) {
            if (b.getIsBorderHighlighted()) {
                b.unHighlightBorder();
            } else {
                b.highlightBorder();
            }
        }

        // Toggle the main bulb
        b.toggle();

        // If the main bulb is a hint then unhighlight the hint and show the hint icon.
        if (b.getIsHintHighlighted()) {
            b.unhighlightHint();
            b.setIsHintHighlighted(false);
            this.showHintIconOnBulb(b.getBulbId());
        }

        this.incrementMoveCounter();
        if (!this.getHasMadeAtLeastOneMove()) {
            this.setHasMadeAtLeastOneMove(true);
        }

        Log.d(TAG, "Bulb: " + b.toString());
        this.individualBulbStatus[b.getBulbId()] = b.isOnOrOff();
        this.toggleNeighbors(b.getBulbId());
        this.setLastBulbToggleIsOn(b.isOn());
        this.updateIndividualBulbStatus();
        boolean gameOver = this.checkIfAllLightsAreOff();
        this.setStarCount(gameOver ? this.calculateStars() : this.getStarCount());
        this.setIsGameOver(gameOver);

        // When Game is over remove all solution and highlighting.
        if (this.getIsShowingSolution() && this.getIsGameOver()) {
            this.unHighlightAllBulbBorders();
            this.unhighlightAllHints();
            this.removeAllHintIcons();
        }
    }

    private void toggleNeighbors(int id) {
        int dimension = this.dimension;
        int row = id / dimension;
        int col = id % dimension;

        this.toggleLeftNeighbor(row, col);
        this.toggleRightNeighbor(row, col);
        this.toggleTopNeighbor(row, col);
        this.toggleBottomNeighbor(row, col);
    }

    private void toggleLeftNeighbor(int row, int col) {
        if (col != 0) {
            int left = (row * this.dimension) + (col - 1);
            this.toggleNeightborBulbAtId(left);
        }
    }

    private void toggleRightNeighbor(int row, int col) {
        if (col != dimension - 1) {
            int right = (row * this.dimension) + (col + 1);
            this.toggleNeightborBulbAtId(right);
        }
    }

    private void toggleTopNeighbor(int row, int col) {
        if (row != 0) {
            int top = (row - 1) * this.dimension + col;
            this.toggleNeightborBulbAtId(top);
        }
    }

    private void toggleBottomNeighbor(int row, int col) {
        if (row != dimension - 1) {
            int bottom = (row + 1) * this.dimension + col;
            this.toggleNeightborBulbAtId(bottom);
        }
    }

    private void toggleNeightborBulbAtId(int id) {
        (grid.getBulbAt(id)).toggle();

    }

    private void showHintIconOnBulb(int id) {
        grid.getHintIconAt(id).setVisibility(View.VISIBLE);
    }

    private void removeHintIconOnBulb(int id) {
        grid.getHintIconAt(id).setVisibility(View.INVISIBLE);
    }

    private void unHighlightAllBulbBorders() {
        for (int i = 0; i < this.dimension * this.dimension; i++) {
            (grid.getBulbAt(i)).unHighlightBorder();
        }
    }

    private void updateIndividualBulbStatus() {
        this.currentPowerConsumption = 0;
        for (int i = 0; i < dimension * dimension; i++) {
            individualBulbStatus[i] = (this.grid.getBulbAt(i)).isOnOrOff();
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

    void resetBoardToState(byte[] state, byte[] originalIndividualBulbStatus, Stack<Integer> undo,
                           Stack<Integer> redo, int[] moveCounterPerBulb, int moveCounter, int hintsUsed) {
        this.currentToggledBulbs = Arrays.copyOf(state, this.dimension * this.dimension);
        this.setStartState();
        this.updateIndividualBulbStatus();
        this.setOriginalIndividualBulbStatus(this.calculateOriginalIndividualBulbStatus(originalIndividualBulbStatus));
        this.setOriginalBoardPower(this.calculateOriginalBoardPower(this.getOriginalIndividualBulbStatus()));
        this.setUndoStack(undo);
        this.setRedoStack(redo);
        this.setMoveCounter(moveCounter);
        this.setHintsUsed(hintsUsed);
        this.setHintsLeft(this.calculateHintsLeft(this.hintsAllowed, this.hintsUsed));
        this.setIsGameOver(false);
        this.setMoveCounterPerBulb(moveCounterPerBulb);
        this.setHasMadeAtLeastOneMove(false);
        this.setIsShowingSolution(false);
        this.unHighlightAllBulbBorders();
        this.unhighlightAllHints();
        this.removeAllHintIcons();

        Log.d(TAG, "Board Reset complete. \nNew Start State:" + Arrays.toString(state) +
                "\nmoveCounter=" + moveCounter + "\nundoStack=" + undoStack + "\nredoStack=" + redoStack +
                "\nOriginalBoardPower=" + this.getOriginalBoardPower());
    }

    private void removeAllHintIcons() {
        for (int i = 0; i < this.dimension * this.dimension; i++) {
            this.removeHintIconOnBulb(i);
        }
    }

    void highlightSolution(byte[] solution) {
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                (grid.getBulbAt(i)).highlightBorder();
            }
        }

        this.setIsShowingSolution(true);
        Log.d(TAG, "Highlighting Solution:" + Arrays.toString(solution));

    }

    void unHighlightSolution(byte[] solution) {
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                (grid.getBulbAt(i)).unHighlightBorder();
            }
        }

        this.setIsShowingSolution(false);
        Log.d(TAG, "Highlighting Solution:" + Arrays.toString(solution));
    }

    private void setStartState() {
        for (int i = 0; i < this.dimension * this.dimension; i++) {
            (grid.getBulbAt(i)).setOn(true);
        }

        for (int i = 0; i < this.currentToggledBulbs.length; i++) {
            if (this.currentToggledBulbs[i] == 0) {
                clickBulb(grid.getBulbAt(i));
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
        int id = (grid.getBulbAt(elementPopped)).getBulbId();
        if (this.getIsGameOver()) {
            this.setIsShowingSolution(false);
        }
        this.recordBulbClick(id);
        this.addToRedoStack(elementPopped);
        this.playLightSwitchSound((grid.getBulbAt(elementPopped)));
        this.incrementMoveCounterPerBulb(elementPopped);
        this.clickBulb((grid.getBulbAt(elementPopped)));

        Log.d(TAG, "Removed " + id + " from current undo stack: " + this.undoStack.toString());

        if (this.undoStack.isEmpty()) {
            this.setIsUndoStackEmpty(true);
        }
    }

    void removeFromRedoStack() {
        int elementPopped = this.redoStack.pop();
        int id = (grid.getBulbAt(elementPopped)).getBulbId();
        this.recordBulbClick(id);
        this.addToUndoStack(id);
        this.playLightSwitchSound((grid.getBulbAt(elementPopped)));
        this.incrementMoveCounterPerBulb(elementPopped);
        this.clickBulb((grid.getBulbAt(elementPopped)));

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

    private void incrementMoveCounter(int incrementValue) {
        this.setMoveCounter(this.moveCounter + incrementValue);
    }

    private void incrementMoveCounterPerBulb(int id) {
        this.moveCounterPerBulb[id]++;
    }

    private void incrementHintsUsed() {
        this.incrementHintsUsed(1);
    }

    private void incrementHintsUsed(int incrementValue) {
        this.setHintsUsed(this.hintsUsed + incrementValue);
    }

    boolean showHint() {
        byte[] solutionVector = this.getCurrentSolution();
        List<Integer> solutionIds = new ArrayList<>();
        for (int i = 0; i < solutionVector.length; i++) {
            if (solutionVector[i] == 1 && !(grid.getBulbAt(i)).getIsHintHighlighted()) {
                solutionIds.add(i);
            }
        }

        if (!solutionIds.isEmpty()) {
            Random rand = new Random();
            int hintId = solutionIds.get(rand.nextInt(solutionIds.size()));
            this.incrementHintsUsed();
            this.setHintsLeft(this.calculateHintsLeft(this.getHintsAllowed(), this.getHintsUsed()));
            grid.getBulbAt(hintId).highlightHint();
            grid.getHintIconAt(hintId).setVisibility(View.INVISIBLE);
            grid.getBulbAt(hintId).startBounceAnimation();
            return true;
        } else {
            return false;
        }
    }

    private byte[] getCurrentSolution() {
        try {
            return SolutionProvider.getSolution(this.dimension, this.currentToggledBulbs);
        } catch (UnknownSolutionException e) {
            Log.d(TAG, e.getMessage());
            return this.currentToggledBulbs;
        }
    }

    void unhighlightAllHints() {
        for (int i = 0; i < this.dimension * this.dimension; i++) {
            (grid.getBulbAt(i)).unhighlightHint();
            (grid.getBulbAt(i)).setIsHintHighlighted(false);
        }
    }

    private int calculateHintsLeft(int hintsAllowed, int hintsUsed) {
        return hintsAllowed - hintsUsed;
    }

    private int calculateStars() {

        try {
            if (this.getHasSeenSolution()) {
                Log.d(TAG, "Score is 0 because user has seen solution");
                return 0;
            } else {
                int numberOfBulbTogglesNeededForSolving = 0;
                byte[] ogSolution = SolutionProvider.getSolution(this.dimension, this.originalStartState);
                for (int i = 0; i < ogSolution.length; i++) {
                    numberOfBulbTogglesNeededForSolving += ogSolution[i];
                }

                float stars = ((float) this.getMoveCounter() / numberOfBulbTogglesNeededForSolving);
                int result = Math.max((int) Math.floor(4 - stars), 1);

                Log.d(TAG, "Calculating Stars: " +
                        "\nOptimal Solution Count : " + numberOfBulbTogglesNeededForSolving +
                        "\nNumber of Moves: " + this.getMoveCounter() +
                        "\nStars: " + result);
                return result;
            }

        } catch (UnknownSolutionException e) {
            Log.d(TAG, e.getMessage());
            return 0;
        }
    }



    private int getPixels(int value) {
        final float scale = this.getContext().getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
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
        if (this.undoStack.isEmpty()) {
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
        if (this.redoStack.isEmpty()) {
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
        this.gameOverChange.firePropertyChange(GAME_OVER_PROPERTY_NAME, oldValue, isGameOver);
        notifyPropertyChanged(BR.isGameOver);
    }

    @Bindable
    public int getMoveCounter() {
        return moveCounter;
    }

    private void setMoveCounter(int moveCounter) {
        this.moveCounter = moveCounter;
        notifyPropertyChanged(BR.moveCounter);
    }

    public int[] getMoveCounterPerBulb() {
        return this.moveCounterPerBulb;
    }

    public void setMoveCounterPerBulb(int[] moveCounterPerBulb) {
        this.moveCounterPerBulb = moveCounterPerBulb;
    }

    int getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    private int getHintsAllowed() {
        return this.hintsAllowed;
    }

    public void setHintsAllowed(int hintsAllowed) {
        this.hintsAllowed = hintsAllowed;
    }

    @Bindable
    int getHintsUsed() {
        return this.hintsUsed;
    }

    private void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

    @Bindable
    public int getHintsLeft() {
        return this.hintsLeft;
    }

    private void setHintsLeft(int hintsLeft) {
        this.hintsLeft = hintsLeft;
        notifyPropertyChanged(BR.hintsLeft);
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

    public int getOriginalBoardPower() {
        return originalBoardPower;
    }

    public void setOriginalBoardPower(int originalBoardPower) {
        this.originalBoardPower = originalBoardPower;
    }

    @Bindable
    int getStarCount() {
        return this.starCount;
    }

    private void setStarCount(int starCount) {
        this.starCount = starCount;
        notifyPropertyChanged(BR.starCount);
    }

    @Bindable
    public boolean getIsLastBulbToggleIsOn() {
        return this.lastBulbToggleIsOn;
    }

    private void setLastBulbToggleIsOn(boolean lastBulbToggleIsOn) {
        this.lastBulbToggleIsOn = lastBulbToggleIsOn;
        notifyPropertyChanged(BR.isLastBulbToggleIsOn);
    }

    public Context getContext() {
        return this.context;
    }

    public byte[] getIndividualBulbStatus() {
        return this.individualBulbStatus;
    }

    public void setIndividualBulbStatus(byte[] individualBulbStatus) {
        this.individualBulbStatus = individualBulbStatus;
    }

    public byte[] getOriginalIndividualBulbStatus() {
        return originalIndividualBulbStatus;
    }

    public void setOriginalIndividualBulbStatus(byte[] originalIndividualBulbStatus) {
        this.originalIndividualBulbStatus = originalIndividualBulbStatus;
    }
}