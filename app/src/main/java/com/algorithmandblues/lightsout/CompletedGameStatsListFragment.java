package com.algorithmandblues.lightsout;
import android.os.Bundle;
import androidx.fragment.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * List Fragment which helps us display the Game win stats
 */
public class CompletedGameStatsListFragment extends ListFragment {

    DatabaseHelper databaseHelper;
    GameWinStateDBHandler gameWinStateDBHandler;
    ArrayList<GameWinState> gameWinStates;
    StatsViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CompletedGameStatsListFragment() {
    }

    static CompletedGameStatsListFragment newInstance() {
        return new CompletedGameStatsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(getContext());
        gameWinStateDBHandler = GameWinStateDBHandler.getInstance(databaseHelper);
        gameWinStates = new ArrayList<>();
        for (GameWinState gameWinState : GameStatsPagerAdapter.gameWinStateList) {
            if (gameWinState.getNumberOfStars() > 0) {
                gameWinStates.add(gameWinState);
            }
        }
        adapter = new StatsViewAdapter(getActivity(), gameWinStates);
        StatsViewAdapter adapter = new StatsViewAdapter(getActivity(), gameWinStates);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_win_stats_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
