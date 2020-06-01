package com.algorithmandblues.lightsout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllTimeStatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllTimeStatsFragment extends Fragment {

    private static final String TAG =  AllTimeStatsFragment.class.getSimpleName();
    private List<GameWinState> gameWinStates;
    private int totalNumberOfCompletedGames;
    private int totalNumberOfCheatedGames;
    private int totalNumberOfMovesMade;
    private int totalNumberOfHintsUsed;
    private double percentageOfCheatedGames;

    private static final int STATISTIC_TEXT_SIZE = 50;
    private static final int STATISTIC_LABEL_SIZE = 16;
    private static final int SIDE_PADDING = 10;
    private static final int STATISTIC_HORIZONTAL_PADDING = 15;


    public AllTimeStatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AllTimeStatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllTimeStatsFragment newInstance() {
        return new AllTimeStatsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameWinStates = GameStatsPagerAdapter.gameWinStateList;
        totalNumberOfCompletedGames = gameWinStates.size();
        totalNumberOfCheatedGames = 0;
        totalNumberOfHintsUsed = 0;
        totalNumberOfMovesMade = 0;
        for (GameWinState gameWinState : gameWinStates) {
            if (gameWinState.getNumberOfStars() == 0) {
                totalNumberOfCheatedGames++;
            }
            totalNumberOfHintsUsed += gameWinState.getNumberOfHintsUsed();
            totalNumberOfMovesMade += gameWinState.getNumberOfMoves();
        }
        percentageOfCheatedGames = ((double) totalNumberOfCheatedGames / totalNumberOfCompletedGames) * 100;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_time_stats, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LinearLayout holder = view.findViewById(R.id.layout_all_time_stats);

        int horizontalPadding = ActivityDrawingUtils.convertIntValueToAppropriatePixelValueForScreenSize(getContext(), STATISTIC_HORIZONTAL_PADDING);

        String[] gamesCompletedLabel = {getString(R.string.stats_games_completed_label)};
        String[] gamesCompletedData = {String.valueOf(totalNumberOfCompletedGames)};
        LinearLayout totalGamesCompletedLayout = ActivityDrawingUtils.makeGameSummaryTextsAndCaptions(
                getContext(), gamesCompletedData, gamesCompletedLabel, STATISTIC_TEXT_SIZE, STATISTIC_LABEL_SIZE, SIDE_PADDING
        );
        totalGamesCompletedLayout.setPadding(0, horizontalPadding, 0, horizontalPadding);
        holder.addView(totalGamesCompletedLayout);

        String[] moveCounterLabel = {getString(R.string.stats_total_moves_label)};
        String[] moveCounterData = {String.valueOf(totalNumberOfMovesMade)};
        LinearLayout moveCounterLayout = ActivityDrawingUtils.makeGameSummaryTextsAndCaptions(
                getContext(), moveCounterData, moveCounterLabel, STATISTIC_TEXT_SIZE, STATISTIC_LABEL_SIZE, SIDE_PADDING
        );
        moveCounterLayout.setPadding(0, horizontalPadding, 0, horizontalPadding);
        holder.addView(moveCounterLayout);

        String[] hintsUsedLabel = {getString(R.string.stats_hints_used_label)};
        String[] hintsUsedData = {String.valueOf(totalNumberOfHintsUsed)};
        LinearLayout hintsUsedLayout = ActivityDrawingUtils.makeGameSummaryTextsAndCaptions(
                getContext(), hintsUsedData, hintsUsedLabel, STATISTIC_TEXT_SIZE, STATISTIC_LABEL_SIZE, SIDE_PADDING
        );
        hintsUsedLayout.setPadding(0, horizontalPadding, 0, horizontalPadding);
        holder.addView(hintsUsedLayout);


        String[] gamesCheatedLabel = {getString(R.string.stats_games_cheated_label)};
        String[] gamesCheatedData = {String.valueOf(totalNumberOfCheatedGames)};
        LinearLayout totalGamesCheatedLayout = ActivityDrawingUtils.makeGameSummaryTextsAndCaptions(
                getContext(), gamesCheatedData, gamesCheatedLabel, STATISTIC_TEXT_SIZE, STATISTIC_LABEL_SIZE, SIDE_PADDING
        );
        totalGamesCheatedLayout.setPadding(0, horizontalPadding, 0, horizontalPadding);
        holder.addView(totalGamesCheatedLayout);


        String[] gameCheatedPercentageLabel = {getString(R.string.stats_cheat_percentage_label)};
        String[] gameCheatedPercentageData = {String.valueOf(Math.round(percentageOfCheatedGames))};
        LinearLayout cheatPercentageLayout = ActivityDrawingUtils.makeGameSummaryTextsAndCaptions(
                getContext(), gameCheatedPercentageData, gameCheatedPercentageLabel, STATISTIC_TEXT_SIZE, STATISTIC_LABEL_SIZE, SIDE_PADDING
        );
        cheatPercentageLayout.setPadding(0, horizontalPadding, 0, horizontalPadding);
        holder.addView(cheatPercentageLayout);
    }

}
