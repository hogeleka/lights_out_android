package com.algorithmandblues.lightsout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CustomPagerAdapter extends FragmentStatePagerAdapter {

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case GameMode.ARCADE: return TabFragment.newInstance(GameMode.ARCADE);
            case GameMode.CLASSIC: return TabFragment.newInstance(GameMode.CLASSIC);
        }
        return null;
    }
    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case GameMode.ARCADE: return "Arcade";
            case GameMode.CLASSIC: return "Classic";
            default: return null;
        }
    }
}
