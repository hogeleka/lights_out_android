package com.algorithmandblues.lightsout;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.DatagramSocketImpl;
import java.util.HashMap;
import java.util.Map;

public class ActivityDrawingUtils {

    public static final float ONE_THIRD = (float) 0.33 ;
    private static final String TAG = ActivityDrawingUtils.class.getSimpleName();
    private static final int MARGIN_PX_BOARD_TO_TEXT = 10;

    public static final float ENABLED_LEVEL_ALPHA = (float) 1.0;
    public static final float DISABLED_LEVEL_ALPHA = (float) 0.25;


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
    public static final double PERCENTAGE_OF_SCREEN_WIDTH_FOR_GRID = 0.75;


    public static LinearLayout makeRowOfStars(Context context, int numberOfStars, int starSize, int horizontalPadding, int verticalPadding) {
        LinearLayout rowOfStars = new LinearLayout(context);
        rowOfStars.setOrientation(LinearLayout.HORIZONTAL);
        rowOfStars.setGravity(Gravity.CENTER);
        int leftRightPdding = convertIntValueToAppropriatePixelValueForScreenSize(context, horizontalPadding);
        int topBottomPadding = convertIntValueToAppropriatePixelValueForScreenSize(context, verticalPadding);
        rowOfStars.setPadding(leftRightPdding, topBottomPadding, leftRightPdding, topBottomPadding);
        //three stars per row
        for (int i = 1; i <= 3; i++) {
            ImageView starImage = new ImageView(context);
            starImage.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, ONE_THIRD));
            starImage.setBackground( i <= numberOfStars ?
                    context.getResources().getDrawable(R.drawable.gold_star_3d)
                    : context.getResources().getDrawable(R.drawable.gold_star_3d)
            );
            starImage.setAlpha(i <= numberOfStars ? ENABLED_LEVEL_ALPHA : DISABLED_LEVEL_ALPHA);
            int imageSize = convertIntValueToAppropriatePixelValueForScreenSize(context, starSize);
            starImage.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));
            rowOfStars.addView(starImage);
        }
        return rowOfStars;
    }

    public static LinearLayout makeGameSummaryTextsAndCaptions(Context context, String[] numberStringsArray, String[] labelArray, int textSizeOfNumber, int textSizeOfLabel, int sidePadding) {
        LinearLayout linearLayout = new LinearLayout(context);
        int leftRightPadding = convertIntValueToAppropriatePixelValueForScreenSize(context, sidePadding);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setPadding(leftRightPadding, 0, leftRightPadding, 0);

        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        for (int i = 0; i < numberStringsArray.length; i++) {
            float widthPerBox = (float) 1 / numberStringsArray.length;
            boolean animate = i == 1;
            LinearLayout numberAndLabel = makeNumberAndLabelBox(context, numberStringsArray[i], labelArray[i], textSizeOfNumber, textSizeOfLabel, widthPerBox, animate);
            linearLayout.addView(numberAndLabel);
        }
        return linearLayout;
    }

    private static LinearLayout makeNumberAndLabelBox(Context context, String numberAsString, String label, int textSizeOfNumber, int textSizeOfLabel, float widthPerBox, boolean animate) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, widthPerBox));
        linearLayout.setOrientation(LinearLayout.VERTICAL);


        TextView texViewNumber = getTextView(context, numberAsString, textSizeOfNumber, animate);
        linearLayout.addView(texViewNumber);

        TextView textViewLabel = getTextView(context, label, textSizeOfLabel, animate);
        linearLayout.addView(textViewLabel);
        return linearLayout;
    }

    public static TextView getTextView(Context context, String string, int textSize, boolean animate) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setText(string);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(context.getResources().getColor(R.color.custom_black));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        return textView;
    }

    public static int convertIntValueToAppropriatePixelValueForScreenSize(Context context, int value) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }


    public static LinearLayout drawGameBoard(Context context, GameWinState gameWinState, byte[] originalbulbStatuses, int[] movesPerBulb) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.topMargin = convertIntValueToAppropriatePixelValueForScreenSize(context, MARGIN_PX_BOARD_TO_TEXT);
        layoutParams.bottomMargin = convertIntValueToAppropriatePixelValueForScreenSize(context, MARGIN_PX_BOARD_TO_TEXT);
        linearLayout.setLayoutParams(layoutParams);
        int dimension = gameWinState.getDimension();
        GridLayout grid = new GridLayout(context);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        grid.setRowCount(dimension);
        grid.setColumnCount(dimension);
        grid.removeAllViews();
        int height = displayMetrics.heightPixels;
        int width = (int) (PERCENTAGE_OF_SCREEN_WIDTH_FOR_GRID * displayMetrics.widthPixels);
        Log.d(TAG, "Screen width: " + width);
//        int bulbGap = (int) ((SCREEN_WIDTH_PERCENTAGE_FOR_BULB_GAP / 100) * width);
        int bulbGap = (int) (BULB_GAP_MAP.get(dimension) * PERCENTAGE_OF_SCREEN_WIDTH_FOR_GRID);
        bulbGap = convertIntValueToAppropriatePixelValueForScreenSize(context, bulbGap);
        Log.d(TAG, "Bulb gap: " + bulbGap);
        int size = Math.min(width, height);
        int marginCumulativeWidth = (dimension + 1) * bulbGap;
        int bulbWidth = (size - marginCumulativeWidth) / dimension;

        int id = 0;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {

                //will take data from last activity which consists of toggle counts per bulb
                // as well as solution
                final Bulb bulb = new Bulb(context, id);
                bulb.setLayoutParams(createBulbParameters(dimension, row, col, bulbWidth, bulbGap));
                if (originalbulbStatuses[id] == (byte) 0) {
                    bulb.setOn(false);
                }
                if (movesPerBulb[id] % 2 == 1) {
                    bulb.highlightBorder();
                }
//                bulb.setText(Integer.toString(movesPerBulb[id]));
//                bulb.setGravity(Gravity.CENTER);
//                bulb.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                grid.addView(bulb);
                id++;
            }
        }
        Log.i(TAG, "GRID num bulbs:" + Integer.toString(grid.getChildCount()));
        linearLayout.addView(grid);
        return linearLayout;
    }

    private static GridLayout.LayoutParams createBulbParameters(int dimension, int r, int c, int bulbSideLength, int bulbGap) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        if (c != dimension - 1) {
            params.rightMargin = bulbGap / 2;
        } else {
            params.rightMargin = bulbGap;
        }

        if (c == 0) {
            params.leftMargin = bulbGap;
        } else {
            params.leftMargin = bulbGap / 2;
        }

        if (r == dimension - 1) {
            params.bottomMargin = bulbGap;
        }

        if(r == 0) {
            params.topMargin = 0;
        } else {
            params.topMargin = bulbGap;
        }

        params.height = bulbSideLength;
        params.width = bulbSideLength;

        params.setGravity(Gravity.CENTER);
        params.columnSpec = GridLayout.spec(c);
        params.rowSpec = GridLayout.spec(r);

        return params;
    }
}
