package com.algorithmandblues.lightsout.rules;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.algorithmandblues.lightsout.R;
import com.algorithmandblues.lightsout.levelselection.SelectLevelActivity;
import com.algorithmandblues.lightsout.levelselection.SharedPreferencesUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RulesActivity extends FragmentActivity {

    ImageButton mNextBtn;
    Button mSkipBtn, mFinishBtn;
    ImageView zero, one, two;

    int page = 0;   //  to track page position
    ImageView[] indicators;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_rules);

        mNextBtn = findViewById(R.id.intro_btn_next);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            mNextBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right_24dp));
        }

        mSkipBtn = findViewById(R.id.intro_btn_skip);
        mFinishBtn = findViewById(R.id.intro_btn_finish);

        zero = findViewById(R.id.intro_indicator_0);
        one = findViewById(R.id.intro_indicator_1);
        two = findViewById(R.id.intro_indicator_2);

        indicators = new ImageView[]{zero, one, two};

        ViewPager2 vp = findViewById(R.id.pager);
        RulesPageAdapter adapter = new RulesPageAdapter(this);
        vp.setAdapter(adapter);

        vp.setCurrentItem(page);
        updateIndicators(page);

        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                page = position;
                updateIndicators(page);
                mNextBtn.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
                mFinishBtn.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        mNextBtn.setOnClickListener(v -> {
            page += 1;
            vp.setCurrentItem(page, true);
        });

        mSkipBtn.setOnClickListener(v -> {
            SharedPreferencesUtils.saveSharedSetting(RulesActivity.this, SelectLevelActivity.PREF_USER_FIRST_TIME, "false");
            finish();
        });

        mFinishBtn.setOnClickListener(v -> {
            //  update 1st time pref
            SharedPreferencesUtils.saveSharedSetting(RulesActivity.this, SelectLevelActivity.PREF_USER_FIRST_TIME, "false");
            finish();
        });
    }

    void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
