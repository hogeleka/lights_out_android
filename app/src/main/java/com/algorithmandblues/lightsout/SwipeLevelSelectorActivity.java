package com.algorithmandblues.lightsout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class SwipeLevelSelectorActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    LevelDBHandler levelDBHandler;
    int userProgressLevel;
    int selectedGameMode;

    private static final int PROGRESS_BAR_HORIZONTAL_PADDING = 15;
    private static final int TEXT_SIZE = 24;
    private static final int BOTTOM_ICONS_IMAGE_SIZE = 40;
    private static final int BOTTOM_ICONS_LABEL_TEXT_SIZE = 16;
    private static final int SIDE_PADDING_STATS_ICONS = 0;
    private static final int BUTTON_PADDING_TOP = 16;
    private static final float ONE_THIRD = (float) 0.33;

    private static final String TAG = SwipeLevelSelectorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_level_selector);
        selectedGameMode = getIntent().getIntExtra(getString(R.string.selected_game_mode), GameMode.ARCADE);
        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        levelDBHandler = LevelDBHandler.getInstance(databaseHelper);
        List<Level> userLevelsForProgress = levelDBHandler.fetchLevelsForGameMode(GameMode.ARCADE);
        userProgressLevel = getUserProgressLevel(userLevelsForProgress);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        CustomPagerAdapter myPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab = tabLayout.getTabAt(selectedGameMode);
        tab.select();
//        RelativeLayout footer = findViewById(R.id.progress_bar_and_other_buttons_holder);
        LinearLayout progressBarHolder = findViewById(R.id.progress_bar_bolder);
        TextView userProgressTextView = getTextViewDisplayForLevel(userProgressLevel);
        ProgressBar userProgressBar = getUserProgressBar(userProgressLevel);
        progressBarHolder.addView(userProgressTextView);
        progressBarHolder.addView(userProgressBar);

        LinearLayout bottomIcons = findViewById(R.id.bottom_icons_container);
        LinearLayout bottomButtonsAndActivitySwitches = getBottomButtonsAndIcons();
        bottomIcons.addView(bottomButtonsAndActivitySwitches);

    }

    @Override
    public void onBackPressed() {
        goToHome();
    }

    private LinearLayout getBottomButtonsAndIcons() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(SIDE_PADDING_STATS_ICONS, BUTTON_PADDING_TOP, SIDE_PADDING_STATS_ICONS, 0);
        LinearLayout statsLinkLayout = createImageIconAndTextLayout(getResources().getDrawable(R.drawable.icons8_chart), getString(R.string.stats_label));
        LinearLayout rulesLinkLayout = createImageIconAndTextLayout(getResources().getDrawable(R.drawable.rules), getString(R.string.rules_label));
        LinearLayout homeLinkLayout = createImageIconAndTextLayout(getResources().getDrawable(R.drawable.home), getString(R.string.home_label));
        //TODO: fix to go to stats page
        statsLinkLayout.setOnClickListener(v -> {
            Log.d(TAG, "Clicked on stats link");
        });

        //TODO: fix to go to rules
        rulesLinkLayout.setOnClickListener(v -> {
            Log.d(TAG, "clicked on rules page");
        });

        homeLinkLayout.setOnClickListener(v -> goToHome());
        linearLayout.addView(statsLinkLayout);
        linearLayout.addView(rulesLinkLayout);
        linearLayout.addView(homeLinkLayout);

        return linearLayout;
    }

    private void goToHome() {
        Intent intent = new Intent(SwipeLevelSelectorActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    private LinearLayout createImageIconAndTextLayout(Drawable drawable, String text) {
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, ONE_THIRD));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        ImageView imageView = getImageView(drawable);
        TextView textView = ActivityDrawingUtils.getTextView(this, text, BOTTOM_ICONS_LABEL_TEXT_SIZE, false);
        layout.addView(imageView);
        layout.addView(textView);
        layout.setEnabled(true);
        layout.setClickable(true);
        TypedValue outValue = new TypedValue();
        getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        layout.setBackgroundResource(outValue.resourceId);
        return layout;

    }

    private TextView getTextViewDisplayForLevel(int level) {
        TextView textView = new TextView(this);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        String levelTitle = getTextToDisplayForUserProgress(level);
        textView.setText(levelTitle);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        textView.setTextColor(getResources().getColor(R.color.custom_black));
        return textView;
    }

    private String getTextToDisplayForUserProgress(int nextLevelToUnlock) {
        return SkillLevelConstants.getSkillLevelForLevel(nextLevelToUnlock);
    }

    private ProgressBar getUserProgressBar(int level) {
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.customprogressbarstyle));
        int horizontalPadding = getPixels(PROGRESS_BAR_HORIZONTAL_PADDING);
        progressBar.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        int progress = (int) (((double) level / DatabaseConstants.MAX_DIMENSION) * 100);
        progressBar.setProgress(progress);
        Log.d(TAG, "progress: " + progressBar.getProgress());
        return progressBar;
    }

    /**
     * Gets the current highest unlocked level dimension from db before new level is unlocked
     */
    private int getUserProgressLevel(List<Level> levels) {
        int result = DatabaseConstants.MIN_DIMENSION;
        for (Level level : levels) {
            if (level.getDimension() > result && level.getIsLocked() == DatabaseConstants.UNLOCKED_LEVEL) {
                result = level.getDimension();
            }
        }
        return result;
    }

    private int getPixels(int value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    private ImageView getImageView(Drawable drawable) {
        ImageView imageView = new ImageView(this);
        imageView.setBackground(drawable);
        int imageSize = getPixels(BOTTOM_ICONS_IMAGE_SIZE);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));
        return imageView;
    }
}
