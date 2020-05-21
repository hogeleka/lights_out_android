package com.algorithmandblues.lightsout;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
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
    private byte[] toggledBulbs;
    private byte[] originalStartState;
    private Stack<Integer> undoStack;
    private Stack<Integer> redoStack;
    private GridLayout grid;
    private boolean isShowingSolution;
    private boolean isUndoStackEmpty;
    private boolean isRedoStackEmpty;

    private static final int BULB_GAP = 16;

    public GameInstance(Context context, final int dimension, final byte[] originalStartState, final byte[]
            toggledBulbs, final Stack<Integer> undoStack, final Stack<Integer> redoStack) {
        this.dimension = dimension;
        this.originalStartState = originalStartState;
        this.toggledBulbs = toggledBulbs;
        this.undoStack = undoStack;
        this.redoStack = redoStack;
        this.isUndoStackEmpty = undoStack.isEmpty();
        this.isRedoStackEmpty = redoStack.isEmpty();
        this.isShowingSolution = false;

        grid = new GridLayout(context);
        grid.setRowCount(this.dimension);
        grid.setColumnCount(this.dimension);

        this.drawGameBoard(context);
        this.setStartState();
    }


    private GridLayout.LayoutParams createBulbParameters(int r, int c, int length) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        if (c != this.dimension-1) {
            params.rightMargin = BULB_GAP;
        } else {
            params.rightMargin = 0;
        }

        if (c == 0) {
            params.leftMargin = BULB_GAP;
        } else {
            params.leftMargin = 0;
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
    }

    public void drawGameBoard(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        grid.removeAllViews();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
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
                    handleStackOnBulbClick(bulb.getBulbId());
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

    public void handleStackOnBulbClick(int id) {
        this.addToUndoStack(id);
        this.clearRedoStack();
    }

    public void resetBoardToOriginalStartState() {
        this.toggledBulbs = Arrays.copyOf(this.originalStartState, this.dimension * this.dimension);
        this.clearUndoStack();
        this.clearRedoStack();
        this.setStartState();

        Log.d(TAG, "Board Reset complete");
    }

    public void highlightSolution(byte[] solution) {
        for(int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                ((Bulb) grid.getChildAt(i)).highlightBorder();
            }
        }

        this.isShowingSolution = true;
        Log.d(TAG, "Highlighting Solution:" + Arrays.toString(solution));

    }

    public void unHighlightSolution(byte[] solution) {
        for(int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                ((Bulb) grid.getChildAt(i)).unHighlightBorder();
            }
        }

        this.isShowingSolution = false;
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

    public void addToUndoStack(int id) {
        this.undoStack.push(id);
        this.setIsUndoStackEmpty(false);
        Log.d(TAG, "Added " + id + " to current undo stack: " + this.undoStack.toString());
    }

    public void addToRedoStack(int id) {
        this.redoStack.push(id);
        this.setIsRedoStackEmpty(false);
        Log.d(TAG, "Added " + id + " to current redo stack: " + this.redoStack.toString());
    }

    public void removeFromUndoStack() {
        int elementPopped = this.undoStack.pop();
        int id = ((Bulb) grid.getChildAt(elementPopped)).getBulbId();
        this.recordBulbClick(id);
        this.addToRedoStack(elementPopped);
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
    }

    public Stack<Integer> getRedoStack() {
        return this.redoStack;
    }

    public void setRedoStack(Stack<Integer> redoStack) {
        this.redoStack = redoStack;
    }

    public int getDimension() {
        return this.dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public boolean isShowingSolution() {
        return this.isShowingSolution;
    }

    public void setShowingSolution(boolean showingSolution) {
        this.isShowingSolution = showingSolution;
    }
}