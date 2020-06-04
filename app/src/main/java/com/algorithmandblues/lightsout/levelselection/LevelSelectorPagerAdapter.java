package com.algorithmandblues.lightsout.levelselection;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.algorithmandblues.lightsout.game.GameMode;

public class LevelSelectorPagerAdapter extends FragmentStateAdapter {

    private static final int ITEM_COUNT = 2;

    LevelSelectorPagerAdapter(FragmentActivity fm) {
        super(fm);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case GameMode.CAMPAIGN: return LevelSelectorFragment.newInstance(GameMode.CAMPAIGN);
            case GameMode.PRACTICE: return LevelSelectorFragment.newInstance(GameMode.PRACTICE);
        }
        return null;
    }
    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }
}
