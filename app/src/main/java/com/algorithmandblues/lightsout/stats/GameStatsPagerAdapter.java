package com.algorithmandblues.lightsout.stats;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.algorithmandblues.lightsout.database.DatabaseHelper;
import com.algorithmandblues.lightsout.game.GameMode;
import com.algorithmandblues.lightsout.database.GameWinState;
import com.algorithmandblues.lightsout.database.GameWinStateDBHandler;

import java.util.List;

public class GameStatsPagerAdapter extends FragmentStateAdapter {

    private static final int ITEM_COUNT = 2;

    private DatabaseHelper databaseHelper;
    private GameWinStateDBHandler gameWinStateDBHandler;
    static List<GameWinState> gameWinStateList;

    GameStatsPagerAdapter(FragmentActivity fm) {
        super(fm);
        databaseHelper = DatabaseHelper.getInstance(fm.getApplicationContext());
        gameWinStateDBHandler = GameWinStateDBHandler.getInstance(databaseHelper);
        gameWinStateList = gameWinStateDBHandler.fetchAllGameWinStatesInReverseChronological(GameMode.CAMPAIGN);
    }

    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? CompletedGameStatsListFragment.newInstance() : AllTimeStatsFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }
}
