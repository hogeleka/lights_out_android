package com.algorithmandblues.lightsout;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.GridLayout;

import java.util.Arrays;
import java.util.Stack;

class GameInstance implements Parcelable{

    private static final int UNDO_REDO_STACK_SIZE = 40;
    private byte[] toggledBulbs;
    private byte[] startState;
    private Stack<Integer> undoStack;
    private Stack<Integer> redoStack;

    private GridLayout grid;
    private int dimension;
    private static final int BULB_GAP = 20;
    private static final int BOARD_PADDING = 20;

    public GameInstance(Context context, final int dimension, final byte[] startState) {
        this.dimension = dimension;
        this.startState = startState;
        this.undoStack = new Stack<>();

        for (int i = 0; i < UNDO_REDO_STACK_SIZE; i++) {
            this.undoStack.push(-1);
        }

        this.redoStack = new Stack<>();

        for (int i = 0; i < UNDO_REDO_STACK_SIZE; i++) {
            this.redoStack.push(-1);
        }

        grid = new GridLayout(context);
        grid.removeAllViews();
        grid.setRowCount(this.dimension);
        grid.setColumnCount(this.dimension);
        this.toggledBulbs = startState;
        this.drawGameBoard(context);
        this.setStartState();
    }

    public GridLayout getGrid() {
        return this.grid;
    }

    private GridLayout.LayoutParams createBulbParameters(int r, int c, int length) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        if (c != this.dimension-1) {
            params.rightMargin = BOARD_PADDING;
        } else {
            params.rightMargin = 0;
        }

        if (c == 0) {
            params.leftMargin = BOARD_PADDING;
        } else {
            params.leftMargin = 0;
        }

        if (r == dimension - 1) {
            params.bottomMargin = BOARD_PADDING;
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
        b.toggle();

        Log.d("Bulb: ", b.toString());
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

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int size = width <= height ? width : height;
        int marginCumulativeWidth = (dimension + 1) * BULB_GAP;
        int bulbWidth = (size - marginCumulativeWidth) / dimension;

        int id = 0;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                final Bulb bulb = new Bulb(context, id);
                bulb.setLayoutParams(this.createBulbParameters(row, col, bulbWidth));
                bulb.setOnClickListener(v -> {
                    byte newVal = (byte) (1 - toggledBulbs[bulb.getBulbId()]);
                    toggledBulbs[bulb.getBulbId()] = newVal;
                    if (undoStack.size() == UNDO_REDO_STACK_SIZE) {
                        undoStack.remove(0);
                    }
                    undoStack.push(bulb.getBulbId());
                    clickBulb(bulb);
                });
                grid.addView(bulb);
                id++;
            }
        }
        Log.i("GRID num buttons:", Integer.toString(grid.getChildCount()));
    }

    public void setStartState() {
        for (int i = 0; i < this.startState.length; i++) {
            if (this.startState[i] == 0) {
                clickBulb((Bulb)(grid.getChildAt(i)));
            }
        }
    }

    public GameInstance(Parcel in) {
        int dim = in.readInt();
        byte[] toggled = new byte[dim * dim];
        int[] undo = new int[UNDO_REDO_STACK_SIZE];
        int[] redo = new int[UNDO_REDO_STACK_SIZE];
        in.readByteArray(toggled);
        in.readIntArray(undo);
        in.readIntArray(redo);
        dimension = dim;
        toggledBulbs = toggled;
        undoStack = new Stack<>();
        for (int i = 0; i < undo.length; i++) {
            undoStack.push(undo[i]);
        }
        for (int i = 0; i < redo.length; i++) {
            redoStack.push(redo[i]);
        }
    }

    public static final Creator<GameInstance> CREATOR = new Creator<GameInstance>() {
        @Override
        public GameInstance createFromParcel(Parcel in) {
            return new GameInstance(in);
        }

        @Override
        public GameInstance[] newArray(int size) {
            return new GameInstance[size];
        }
    };

    public static Creator<GameInstance> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(dimension);
        parcel.writeByteArray(toggledBulbs);
        int[] undoArray = new int[UNDO_REDO_STACK_SIZE];
        Arrays.fill(undoArray, -1);
        int[] redoArray = new int[UNDO_REDO_STACK_SIZE];
        Arrays.fill(redoArray, -1);

        int undoLen = undoStack.size();
        for (int j = 0; j < undoLen; j++) {
            int element = undoStack.get(j);
            undoArray[UNDO_REDO_STACK_SIZE - undoLen + j] = element;
        }

        int redoLen = redoStack.size();
        for (int j = 0; j < redoLen; j++) {
            int element = redoStack.get(j);
            redoArray[UNDO_REDO_STACK_SIZE - redoLen + j] = element;
        }
        parcel.writeIntArray(undoArray);
        parcel.writeIntArray(redoArray);
    }
}


