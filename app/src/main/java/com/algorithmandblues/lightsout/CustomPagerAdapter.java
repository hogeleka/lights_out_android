package com.algorithmandblues.lightsout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CustomPagerAdapter extends FragmentStateAdapter {

    private static final int ITEM_COUNT = 2;

    CustomPagerAdapter(FragmentActivity fm) {
        super(fm);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case GameMode.ARCADE: return LevelSelectorFragment.newInstance(GameMode.ARCADE);
            case GameMode.CLASSIC: return LevelSelectorFragment.newInstance(GameMode.CLASSIC);
        }
        return null;
    }
    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }
}