package com.algorithmandblues.lightsout.levelselection;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.algorithmandblues.lightsout.utils.ActivityDrawingUtils;
import com.algorithmandblues.lightsout.game.GameMode;
import com.algorithmandblues.lightsout.R;
import com.algorithmandblues.lightsout.home.HomePageActivity;
import com.algorithmandblues.lightsout.rules.RulesActivity;
import com.algorithmandblues.lightsout.stats.StatsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectLevelActivity extends FragmentActivity {

    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    boolean isUserFirstTime;

    int selectedGameMode;

    private static final int BOTTOM_ICONS_IMAGE_SIZE = 40;
    private static final int BOTTOM_ICONS_LABEL_TEXT_SIZE = 16;
    private static final int SIDE_PADDING_STATS_ICONS = 0;
    private static final int BUTTON_PADDING_TOP = 16;
    private static final float ONE_THIRD = (float) 0.33;

    private static final String[] TAB_NAMES = {GameMode.CAMPAIGN_STRING, GameMode.PRACTICE_STRING};
    private static final String TAG = SelectLevelActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);

        isUserFirstTime = Boolean.parseBoolean(SharedPreferencesUtils.readSharedSetting(SelectLevelActivity.this, PREF_USER_FIRST_TIME, "true"));

        Intent introIntent = new Intent(SelectLevelActivity.this, RulesActivity.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);

        if (isUserFirstTime) {
            startActivity(introIntent);
        }

        selectedGameMode = getIntent().getIntExtra(getString(R.string.selected_game_mode), GameMode.CAMPAIGN);
        ViewPager2 viewPager = (ViewPager2) findViewById(R.id.pager);
        LevelSelectorPagerAdapter myPagerAdapter = new LevelSelectorPagerAdapter(this);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager.setAdapter(myPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(TAB_NAMES[position])
        ).attach();
        TabLayout.Tab tab = tabLayout.getTabAt(selectedGameMode);
        tab.select();
        LinearLayout bottomIcons = findViewById(R.id.bottom_icons_container);
        LinearLayout bottomButtonsAndActivitySwitches = getBottomButtonsAndIcons();
        bottomIcons.addView(bottomButtonsAndActivitySwitches);
    }

    @Override
    public void onBackPressed() {
        goToHome();
    }

    private LinearLayout getBottomButtonsAndIcons() {
        LinearLayout linearLayout = new LinearLayout(this) {{
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setGravity(Gravity.CENTER);
            setOrientation(LinearLayout.HORIZONTAL);
            setPadding(SIDE_PADDING_STATS_ICONS, BUTTON_PADDING_TOP, SIDE_PADDING_STATS_ICONS, 0);
        }};

        LinearLayout statsLinkLayout = createImageIconAndTextLayout(getResources().getDrawable(R.drawable.stats), getString(R.string.stats_label));
        LinearLayout rulesLinkLayout = createImageIconAndTextLayout(getResources().getDrawable(R.drawable.rules), getString(R.string.rules_label));
        LinearLayout homeLinkLayout = createImageIconAndTextLayout(getResources().getDrawable(R.drawable.home), getString(R.string.home_label));

        statsLinkLayout.setOnClickListener(v -> goToStats());
        rulesLinkLayout.setOnClickListener(v -> goToRules());
        homeLinkLayout.setOnClickListener(v -> goToHome());

        linearLayout.addView(statsLinkLayout);
        linearLayout.addView(rulesLinkLayout);
        linearLayout.addView(homeLinkLayout);

        return linearLayout;
    }

    private void goToStats() {
        Log.d(TAG, "Clicked on stats link");
        Intent intent = new Intent(SelectLevelActivity.this, StatsActivity.class);
        startActivity(intent);
    }

    private void goToRules() {
        Log.d(TAG, "Clicked on rules link");
        Intent intent = new Intent(SelectLevelActivity.this, RulesActivity.class);
        startActivity(intent);
    }

    private void goToHome() {
        Log.d(TAG, "Clicked on home link");
        Intent intent = new Intent(SelectLevelActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    private LinearLayout createImageIconAndTextLayout(Drawable drawable, String text) {
        ImageView imageView = getImageView(drawable);
        TextView textView = ActivityDrawingUtils.getTextView(this, text, BOTTOM_ICONS_LABEL_TEXT_SIZE, false);

        TypedValue outValue = new TypedValue();
        getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);

        LinearLayout layout = new LinearLayout(this) {{
            setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, ONE_THIRD));
            setOrientation(LinearLayout.VERTICAL);
            setGravity(Gravity.CENTER);
            setEnabled(true);
            setClickable(true);
            setBackgroundResource(outValue.resourceId);
        }};

        layout.addView(imageView);
        layout.addView(textView);

        return layout;

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
