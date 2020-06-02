package com.algorithmandblues.lightsout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class StatsActivity extends FragmentActivity  {

    private static final String TAG = StatsActivity.class.getSimpleName();

    private static final String[] TAB_NAMES = {"completed games", "campaign stats"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        ViewPager2 viewPager = (ViewPager2) findViewById(R.id.pager_stats_activity);
        GameStatsPagerAdapter pagerAdapter = new GameStatsPagerAdapter(this);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout_stats_activity);
        viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(TAB_NAMES[position])
        ).attach();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
