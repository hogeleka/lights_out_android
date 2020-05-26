package com.algorithmandblues.lightsout;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewLevelSelectorActivity extends AppCompatActivity {

    LinearLayout arcadeModeGrid;
    LinearLayout practiceModeGrid;
    DatabaseHelper databaseHelper;
    LevelDBHandler levelDBHandler;
    Map<Integer, Level> arcadeLevelMap;
    Map<Integer, Level> classicLevelMap;


    private static final int TABLE_ROW_HEIGHT = 80;
    private static final int STAR_IMAGE_SIZE_PX = 15;
    private static final int TABLE_ROW_MARGIN_HORIZONTAL = 8;
    private static final int TABLE_ROW_MARGIN_VERTICAL = 4;
    private static final int ROW_OF_STARS_PADDING = 10;
    private static final int INDIVIDUAL_LEVEL_CELL_PADDING = 10;
    private static final int TEXT_SIZE = 8;

    private static final String TAG = NewLevelSelectorActivity.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_level_selector);

        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        levelDBHandler = LevelDBHandler.getInstance(databaseHelper);
        List<Level> arcadeLevels = levelDBHandler.fetchLevelsForGameMode(GameMode.ARCADE);
        List<Level> classicLevels = levelDBHandler.fetchLevelsForGameMode(GameMode.CLASSIC);
        arcadeLevelMap = getHashMapForLevels(arcadeLevels);
        classicLevelMap = getHashMapForLevels(classicLevels);

        arcadeModeGrid = findViewById(R.id.arcade_mode_grid);
        prepareTableForGameMode(arcadeModeGrid, GameMode.ARCADE);
    }

    private Map<Integer, Level> getHashMapForLevels(List<Level> arcadeLevels) {
        Map<Integer, Level> map = new HashMap<>();
        for (Level level : arcadeLevels) {
            map.put(level.getDimension(), level);
        }
        return map;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void prepareTableForGameMode(LinearLayout t1, int gameMode) {
        Map<Integer, Level> mapToUse = gameMode == GameMode.ARCADE ? arcadeLevelMap : classicLevelMap;
        int dimension = DatabaseConstants.MIN_DIMENSION;
        int tableDimensions = 3;
        for (int i = 0; i < tableDimensions; i++) {
            LinearLayout tr = new LinearLayout(this);
            tr.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertIntValueToAppropriatePixelValueForScreenSize(TABLE_ROW_HEIGHT));
            layoutParams.leftMargin = convertIntValueToAppropriatePixelValueForScreenSize(TABLE_ROW_MARGIN_HORIZONTAL);
            layoutParams.rightMargin = convertIntValueToAppropriatePixelValueForScreenSize(TABLE_ROW_MARGIN_HORIZONTAL);
            tr.setLayoutParams(layoutParams);
            tr.setGravity(Gravity.CENTER);
            RelativeLayout separator = new RelativeLayout(new ContextThemeWrapper(this,R.style.Divider));
            t1.addView(separator);
            for (int j = 0; j < tableDimensions; j++) {
                LinearLayout linearLayout = prepareLinearLayoutForGameModeAndLevel(mapToUse.get(dimension));
                tr.addView(linearLayout);
                dimension++;
            }
            t1.addView(tr);
        }
        RelativeLayout separator = new RelativeLayout(new ContextThemeWrapper(this,R.style.Divider));
        t1.addView(separator);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private LinearLayout prepareLinearLayoutForGameModeAndLevel(Level level) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, (float) 0.33);
        linearLayout.setLayoutParams(layoutParams);
        boolean isLevelEnabled = checkIfEnabled(level);
        linearLayout.setEnabled(isLevelEnabled);
        linearLayout.setClickable(isLevelEnabled);
        TextView textView = new TextView(this);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        float textSize = (float) convertIntValueToAppropriatePixelValueForScreenSize(TEXT_SIZE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        textView.setTextColor(getResources().getColor(R.color.black_overlay));
        String label = String.format(getString(R.string.level_chooser_button_label), level.getDimension(), level.getDimension());
        textView.setText(label);
        LinearLayout rowOfStars = new LinearLayout(this);
        rowOfStars.setOrientation(LinearLayout.HORIZONTAL);
        rowOfStars.setGravity(Gravity.CENTER);
        for (int i = 1; i <= 3; i++) {
            ImageView starImage = new ImageView(this);
            starImage.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 0.33));
            starImage.setBackground( i <= level.getNumberOfStars() ? getResources().getDrawable(R.drawable.starfilled) : getResources().getDrawable(R.drawable.staroutline));
            int imageSize = convertIntValueToAppropriatePixelValueForScreenSize(STAR_IMAGE_SIZE_PX);
            starImage.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));
            rowOfStars.addView(starImage);
        }
        linearLayout.addView(textView);
        linearLayout.addView(rowOfStars);
        int cellPadding = convertIntValueToAppropriatePixelValueForScreenSize(INDIVIDUAL_LEVEL_CELL_PADDING);
        linearLayout.setPadding(cellPadding, cellPadding, cellPadding, cellPadding);
        TypedValue outValue = new TypedValue();
        getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        linearLayout.setBackgroundResource(outValue.resourceId);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "tried to click on level: " + level.toString());
            }
        });
        linearLayout.setAlpha(isLevelEnabled ? (float) 1.0 : (float) 0.4);
        return linearLayout;
    }

    private boolean checkIfEnabled(Level level) {
        if (level.getGameMode() == GameMode.CLASSIC) {
            return true;
        } else return level.getDimension() == DatabaseConstants.MIN_DIMENSION || level.getNumberOfStars() > 0;
    }

    public int convertIntValueToAppropriatePixelValueForScreenSize(int value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }
}
