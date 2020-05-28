package com.algorithmandblues.lightsout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelSelectorActivity extends AppCompatActivity {

    LinearLayout arcadeModeGrid;
    LinearLayout classicModeGrid;
    DatabaseHelper databaseHelper;
    LevelDBHandler levelDBHandler;
    GameDataObjectDBHandler gameDataObjectDBHandler;
    int selectedGameMode;
    Map<Integer, Level> arcadeLevelMap;
    Map<Integer, Level> classicLevelMap;
    TabHost tabHost;

    int nextLevelToUnlock;

    private static final int STAR_IMAGE_SIZE_PX = 26;
    private static final int TABLE_ROW_MARGIN_HORIZONTAL = 8;
    private static final int INDIVIDUAL_LEVEL_CELL_PADDING = 10;
    private static final int TEXT_SIZE = 24;
    private static final int PROGRESS_BAR_HORIZONTAL_PADDING = 15;

    //stars need to be divided equally over 3 and each cell (dimension) also needs to be divided equally over parent width into 3
    private static final float ONE_THIRD = (float) 0.33;
    private static final float ENABLED_LEVEL_ALPHA = (float) 1.0;
    private static final float DISABLED_LEVEL_ALPHA = (float) 0.4;

    private static int tabUnderlinedColor;


    private static final String TAG = LevelSelectorActivity.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selector);

        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        levelDBHandler = LevelDBHandler.getInstance(databaseHelper);
        gameDataObjectDBHandler = GameDataObjectDBHandler.getInstance(databaseHelper);

        List<Level> arcadeLevels = levelDBHandler.fetchLevelsForGameMode(GameMode.ARCADE);
        List<Level> classicLevels = levelDBHandler.fetchLevelsForGameMode(GameMode.CLASSIC);

        tabUnderlinedColor = getResources().getColor(R.color.bulb_off_color);

        arcadeLevelMap = getHashMapForLevels(arcadeLevels);
        classicLevelMap = getHashMapForLevels(classicLevels);

        nextLevelToUnlock = calculateNextLevelToUnlock(arcadeLevelMap);

        arcadeModeGrid = findViewById(R.id.arcadeTab);
        classicModeGrid = findViewById(R.id.classicTab);
        tabHost = findViewById(R.id.tabhost);
        setUpTabHost();
        selectedGameMode = GameMode.ARCADE;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setUpTabHost() {
        tabHost.setup();
        TabHost.TabSpec spec = tabHost.newTabSpec(String.valueOf(GameMode.ARCADE));
        spec.setContent(R.id.arcadeTab);
        spec.setIndicator(getString(R.string.arcade_tab_name));
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec(String.valueOf(GameMode.CLASSIC));
        spec.setContent(R.id.classicTab);
        spec.setIndicator(getString(R.string.classic_tab_name));
        tabHost.addTab(spec);
        prepareTableForGameMode(arcadeModeGrid, GameMode.ARCADE);
        prepareTableForGameMode(classicModeGrid, GameMode.CLASSIC);

        //set for the initial loading of the activity
        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).getBackground().setColorFilter(
                tabUnderlinedColor, PorterDuff.Mode.MULTIPLY
        );

        tabHost.setOnTabChangedListener(arg0 -> {
            Log.d(TAG, "currently in tab with index: " + tabHost.getCurrentTab());
            selectedGameMode = Integer.parseInt(arg0) == GameMode.ARCADE ? GameMode.ARCADE : GameMode.CLASSIC;
            Log.d(TAG, "Switched tabs to : " + selectedGameMode);
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).getBackground().setColorFilter(
                   tabUnderlinedColor, PorterDuff.Mode.MULTIPLY
            );
        });



    }

    private int calculateNextLevelToUnlock(Map<Integer, Level> levelMap) {
        for (int i = DatabaseConstants.MIN_DIMENSION; i < DatabaseConstants.MAX_DIMENSION; i++) {
            if (levelMap.get(i).getNumberOfStars() == 0) {
                return i;
            }
        }
        return DatabaseConstants.MAX_DIMENSION;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LevelSelectorActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
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
        int tableDimensions = 3; //3 rows and 3 columns for board sizes
        int rowHeight = useDisplayMetricsToCalculateRowHeight();
        LinearLayout.LayoutParams layoutParams = getLayoutParams(rowHeight);

        for (int i = 0; i < tableDimensions; i++) {
            LinearLayout tr = new LinearLayout(this);
            //set table row orientation and other layout parameters
            tr.setOrientation(LinearLayout.HORIZONTAL);
            tr.setLayoutParams(layoutParams);
            tr.setGravity(Gravity.CENTER);
            //add separator
//            RelativeLayout separator = new RelativeLayout(new ContextThemeWrapper(this,R.style.Divider));
//            t1.addView(separator);
            //add each column of table
            for (int j = 0; j < tableDimensions; j++) {
                LinearLayout linearLayout = prepareLinearLayoutForGameModeAndLevel(mapToUse.get(dimension));
                tr.addView(linearLayout);
                dimension++;
            }
            t1.addView(tr);
        }
        //add separator
//        RelativeLayout separator = new RelativeLayout(new ContextThemeWrapper(this,R.style.Divider));
//        t1.addView(separator);
        //progress bar at the end as last row
        LinearLayout tableRow = getLastRowShowingStatusAndProgressBar(layoutParams, nextLevelToUnlock);
        t1.addView(tableRow);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private LinearLayout getLastRowShowingStatusAndProgressBar(LinearLayout.LayoutParams layoutParams, int nextLevelToUnlock) {
        LinearLayout tr = new LinearLayout(this);
        tr.setOrientation(LinearLayout.VERTICAL);
        tr.setLayoutParams(layoutParams);
        tr.setGravity(Gravity.CENTER);
        TextView textView = getTextViewDisplayForLevel(nextLevelToUnlock);
        ProgressBar progressBar = getUserProgressBar();
        tr.addView(textView);
        tr.addView(progressBar);
        return tr;
    }

    private LinearLayout.LayoutParams getLayoutParams(int rowHeight) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rowHeight);
        layoutParams.leftMargin = convertIntValueToAppropriatePixelValueForScreenSize(TABLE_ROW_MARGIN_HORIZONTAL);
        layoutParams.rightMargin = convertIntValueToAppropriatePixelValueForScreenSize(TABLE_ROW_MARGIN_HORIZONTAL);
        return layoutParams;
    }

    private int useDisplayMetricsToCalculateRowHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        //we are dividinng screenwidth by 4 for the level chooser;
        return displayMetrics.widthPixels / 4;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private TextView getTextViewDisplayForLevel(int nextLevelToUnlock) {
        TextView textView = new TextView(this);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        String levelTitle = getTextToDisplayForUserProgress(nextLevelToUnlock);
        textView.setText(levelTitle);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        return textView;

    }

    private String getTextToDisplayForUserProgress(int nextLevelToUnlock) {
        //TODO: fix this to be the strings we decide
        return "Skill: Beginner";
    }

    private ProgressBar getUserProgressBar() {
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogressbarstyle));
        int horizontalPadding = convertIntValueToAppropriatePixelValueForScreenSize(PROGRESS_BAR_HORIZONTAL_PADDING);
        progressBar.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        int progress = (int) (((double) nextLevelToUnlock / DatabaseConstants.MAX_DIMENSION) * 100);

        progressBar.setProgress(progress);
        Log.d(TAG, "progress: " + progressBar.getProgress());
        return progressBar;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private LinearLayout prepareLinearLayoutForGameModeAndLevel(Level level) {
        LinearLayout linearLayout = getParentLinearLayoutWithParams();
        boolean isLevelEnabled = checkIfEnabled(level);

        linearLayout.setEnabled(isLevelEnabled);
        linearLayout.setClickable(isLevelEnabled);

        TextView textView = getTextViewForCurrentLabelChooser(level);
        LinearLayout rowOfStars = getRowOfStarsLayoutForCurrentLevel(level);
        linearLayout.addView(textView);
        linearLayout.addView(rowOfStars);

        //other formatting for the layout
        int cellPadding = convertIntValueToAppropriatePixelValueForScreenSize(INDIVIDUAL_LEVEL_CELL_PADDING);
        linearLayout.setPadding(cellPadding, cellPadding, cellPadding, cellPadding);
        TypedValue outValue = new TypedValue();
        getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        linearLayout.setBackgroundResource(outValue.resourceId);
        linearLayout.setOnClickListener(v -> {
            Log.d(TAG, "tried to click on level: " + level.toString());
            selectLevelLabel(level.getDimension());
        });

        linearLayout.setAlpha(isLevelEnabled ? ENABLED_LEVEL_ALPHA : DISABLED_LEVEL_ALPHA);
        return linearLayout;
    }

    private LinearLayout getRowOfStarsLayoutForCurrentLevel(Level level) {
        LinearLayout rowOfStars = new LinearLayout(this);
        rowOfStars.setOrientation(LinearLayout.HORIZONTAL);
        rowOfStars.setGravity(Gravity.CENTER);

        //three stars per row
        for (int i = 1; i <= 3; i++) {
            ImageView starImage = new ImageView(this);
            starImage.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, ONE_THIRD));
            starImage.setBackground( i <= level.getNumberOfStars() ? getResources().getDrawable(R.drawable.gold_star_3d) : getResources().getDrawable(R.drawable.gold_star_3d));
            starImage.setAlpha( i <= level.getNumberOfStars() ? ENABLED_LEVEL_ALPHA : DISABLED_LEVEL_ALPHA);
            int imageSize = convertIntValueToAppropriatePixelValueForScreenSize(STAR_IMAGE_SIZE_PX);
            starImage.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));
            rowOfStars.addView(starImage);
        }
        return rowOfStars;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private TextView getTextViewForCurrentLabelChooser(Level level) {
        TextView textView = new TextView(this);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        textView.setTextColor(Color.BLACK);
        String label = String.format(getString(R.string.level_chooser_button_label), level.getDimension(), level.getDimension());
        textView.setText(label);
        return textView;
    }

    private LinearLayout getParentLinearLayoutWithParams() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, ONE_THIRD);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(Gravity.CENTER);
        return linearLayout;
    }

    private boolean checkIfEnabled(Level level) {
        if (level.getGameMode() == GameMode.CLASSIC) {
            return true;
        }

        // TODO: Change back when app is ready for not testing
        // return level.getDimension() == DatabaseConstants.MIN_DIMENSION || level.getDimension() == nextLevelToUnlock || level.getNumberOfStars() > 0;

        return true;
    }

    public int convertIntValueToAppropriatePixelValueForScreenSize(int value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    public void selectLevelLabel(int dimension) {
        Log.d(TAG, "selected level " +  Integer.toString(dimension));
        boolean setRandomStateFlag = selectedGameMode == GameMode.ARCADE;
        if (!checkForExistingGame(dimension, selectedGameMode)) {
            Log.d(TAG, "No existing game in DB");
            goToNewGameActivity(dimension, false, setRandomStateFlag);
        } else {
            buildDialogToRequestUserResponse(dimension, setRandomStateFlag);
        }
    }

    public void buildDialogToRequestUserResponse(int dimension, boolean setRandomStateFlag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LevelSelectorActivity.this);
        builder.setTitle(getString(R.string.level_picker_resume_or_restart_title))
                .setMessage(String.format(getString(R.string.level_picker_resume_or_restart_message_prompt), dimension, dimension))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    goToNewGameActivity(dimension, true, setRandomStateFlag);
                })
                .setNegativeButton(getString(R.string.restart_new_game), (dialog, which) -> {
                    goToNewGameActivity(dimension, false, setRandomStateFlag);
                });
        AlertDialog dialog  = builder.create();
        dialog.show();
    }

    public void goToNewGameActivity(int dimension, boolean resumeGameFromDBFlag, boolean setRandomStateFlag) {
        Intent intent = new Intent(LevelSelectorActivity.this, GameGridActivity.class);
        intent.putExtra(getString(R.string.dimension), dimension);
        intent.putExtra(getString(R.string.resume_from_db_flag), resumeGameFromDBFlag);
        intent.putExtra(getString(R.string.set_random_state_flag), setRandomStateFlag);
        int currentBestScoreForLevelAndGameType = selectedGameMode == GameMode.ARCADE ? arcadeLevelMap.get(dimension).getNumberOfStars() : classicLevelMap.get(dimension).getNumberOfStars();
        intent.putExtra(getString(R.string.best_score_level_gameType), currentBestScoreForLevelAndGameType);
        startActivity(intent);
        finish();
    }

    public boolean checkForExistingGame(int dimension, int gameMode) {
        return gameDataObjectDBHandler.getMostRecentGameDataForGameType(dimension, gameMode) != null;
    }

}
