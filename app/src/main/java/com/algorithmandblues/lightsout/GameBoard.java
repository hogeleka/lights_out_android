package com.algorithmandblues.lightsout;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;

class GameBoard {

//    private Bulb selectedBulb;

    private GridLayout grid;
    private int dimension;
    private static final int BULB_GAP = 20;
    private static final int BOARD_PADDING = 20;

    public GameBoard(Context context, final int dimension){
        this.dimension = dimension;

        grid = new GridLayout(context);
        grid.removeAllViews();
        grid.setRowCount(this.dimension);
        grid.setColumnCount(this.dimension);
        this.drawBoard(context);
    }

    public GridLayout getGrid() {
        return this.grid;
    }

    public void drawBoard(Context context) {
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
                bulb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickBulb(bulb);
                    }
                });
                grid.addView(bulb);
                id++;
            }
        }
        Log.i("GRID num buttons:", Integer.toString(grid.getChildCount()));
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
}

