package com.algorithmandblues.lightsout;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Array Adapter for the GameWinState objects we get from DB
 */
public class StatsViewAdapter extends ArrayAdapter<GameWinState> {

    private static final String TAG = StatsViewAdapter.class.getSimpleName();

    private static final String[] STATS_CAPTIONS = {
            "watts saved",
            "moves",
            "hints used"
    };
    private static final int STAR_IMAGE_SIZE_PX = 55;
    private static final int ROW_OF_STARS_LEFT_RIGHT_PADDING = 10;
    private static final int ROW_OF_STARS_TOP_BOTTOM_PADDING = 5;
    private static final int TEXT_SIZE_NUMBER_GAME_STAT = 50;
    private static final int TEXT_SIZE_LABEL_GAME_STAT = 16;
    private static final int SIDE_PADDING_STATS_ICONS = 0;


    private Context context; //will be activity context: we use ActivityD
    private ArrayList<GameWinState> gameWinStates;
    public StatsViewAdapter(Context context, ArrayList<GameWinState> gameWinStates) {
        super(context, R.layout.fragment_game_win_stats_list, gameWinStates);
        this.context = context;
        this.gameWinStates = gameWinStates;
        Log.d(TAG, "GameWinStateDBHandler: " + "list size: " + this.gameWinStates.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        GameWinState gameWinState = getItem(position);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.fragment_game_win_stats_item, parent, false);
        viewHolder.mDateContainer = (TextView) convertView.findViewById(R.id.game_win_date);
        viewHolder.mGridContainer = (LinearLayout) convertView.findViewById(R.id.game_win_grid);
        viewHolder.mRowOfStarsHolder = (LinearLayout) convertView.findViewById(R.id.row_of_stars_container);
        viewHolder.mGameStatsContainer = (LinearLayout) convertView.findViewById(R.id.game_stats_container);
        createViewForViewHolder(viewHolder, gameWinState);
        convertView.setTag(viewHolder);
        return convertView;
    }

    private void createViewForViewHolder(ViewHolder viewHolder, GameWinState gameWinState) {
        if (gameWinState!=null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(gameWinState.getTimeStampMs());
            String dateTimeText = DateFormat.format("MM/dd/yyyy HH:mm", cal).toString();
            viewHolder.mDateContainer.setText(dateTimeText);
            byte[] originalBulbStatuses = GameDataUtil.stringToByteArray(gameWinState.getOriginalBulbConfiguration());
            int[] movesperBulbs = GameDataUtil.stringToIntegerArray(gameWinState.getMoveCounterPerBulbString());
            LinearLayout grid = ActivityDrawingUtils.drawGameBoard(this.context, gameWinState, originalBulbStatuses, movesperBulbs);
            LinearLayout rowOfStars = ActivityDrawingUtils.makeRowOfStars(
                    this.context, gameWinState.getNumberOfStars(), STAR_IMAGE_SIZE_PX, ROW_OF_STARS_LEFT_RIGHT_PADDING, ROW_OF_STARS_TOP_BOTTOM_PADDING
            );
            String[] actualData = {
                    String.valueOf(gameWinState.getOriginalBoardPower()),
                    String.valueOf(gameWinState.getNumberOfMoves()),
                    String.valueOf(gameWinState.getNumberOfHintsUsed())
            };
            LinearLayout summaryText = ActivityDrawingUtils.makeGameSummaryTextsAndCaptions(
                    this.context, actualData, STATS_CAPTIONS, TEXT_SIZE_NUMBER_GAME_STAT, TEXT_SIZE_LABEL_GAME_STAT, SIDE_PADDING_STATS_ICONS);

            viewHolder.mGridContainer.addView(grid);
            viewHolder.mRowOfStarsHolder.addView(rowOfStars);
            viewHolder.mGameStatsContainer.addView(summaryText);

        }
    }

    private class ViewHolder {
        public TextView mDateContainer;
        public LinearLayout mGridContainer;
        public LinearLayout mRowOfStarsHolder;
        public LinearLayout mGameStatsContainer;
        public GameWinState gameWinStateItem;

        public ViewHolder() {
        }
    }
}
