package com.algorithmandblues.lightsout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GameStatsPagerAdapter extends FragmentStateAdapter {

    private static final int ITEM_COUNT = 2;

    GameStatsPagerAdapter(FragmentActivity fm) {
        super(fm);
    }

    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? GameStatsFragment.newInstance() : AllTimeStatsFragment.newInstance("test", "test");
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }
}
