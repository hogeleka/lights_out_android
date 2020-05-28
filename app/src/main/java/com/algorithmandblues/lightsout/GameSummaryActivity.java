package com.algorithmandblues.lightsout;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

public class GameSummaryActivity extends AppCompatActivity {

    private static final int STAR_IMAGE_SIZE_PX = 50;
    private static final int ROW_OF_STARS_LEFT_RIGHT_PADDING = 10;
    private static final int ROW_OF_STARS_TOP_BOTTOM_PADING = 10;
    private static final int TEXT_SIZE_NUMBER_GAME_STAT = 50;
    private static final int TEXT_SIZE_LABEL_GAME_STAT = 16;
    private static final float ONE_THIRD = (float) 0.33;
    private static final int BOTTOM_BUTTONS_SIDE_PADDINGS_PX = 16;
    private static final int BOTTOM_ICONS_IMAGE_SIZE = 40;
    private static final int BOTTOM_ICONS_LABEL_TEXT_SIZE = 16;
    private static final int SIDE_PADDING_STATS_ICONS = 16;

    DatabaseHelper databaseHelper;
    GameDataObjectDBHandler gameDataObjectDBHandler;
    GameWinStateDBHandler gameWinStateDBHandler;
    GameWinState gameWinState;
    LinearLayout pageContent;

    private static final String TAG = GameSummaryActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_summary);

        overridePendingTransition(0, 0);
        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        gameDataObjectDBHandler = GameDataObjectDBHandler.getInstance(databaseHelper);
        gameWinStateDBHandler = GameWinStateDBHandler.getInstance(databaseHelper);
        gameWinState = fetchGameWinStateFromLastCompletedGameGridActivity();
        pageContent = findViewById(R.id.fullscreen_content);
        LinearLayout rowOfStars = ActivityDrawingUtils.makeRowOfStars(this, gameWinState.getNumberOfStars(), STAR_IMAGE_SIZE_PX, ROW_OF_STARS_LEFT_RIGHT_PADDING, ROW_OF_STARS_TOP_BOTTOM_PADING);
        pageContent.addView(rowOfStars);

        int powerSaved = getIntent().getIntExtra(getString(R.string.total_board_power_saved), 0);
        byte[] originalbulbStatuses = getIntent().getByteArrayExtra(getString(R.string.initial_board_config));

        //TODO: update gameInstance and get real variable for user toggles
        byte[] userToggles = GameDataUtil.stringToByteArray(gameWinState.getToggledBulbs());
        Log.d(TAG, "user toggles: " + Arrays.toString(userToggles));

        //TODO: fix with string resources and use the actual power saved!
        String[] labels = {
                getString(R.string.game_summary_watts_saved_label),
                getString(R.string.game_summary_moves_label),
                getString(R.string.game_summary_hints_used_label)
        };
        String[] numbersStrings = {
                String.valueOf(powerSaved) ,
                "0",
//                String.valueOf(gameWinState.getNumberOfMoves()),
                String.valueOf(gameWinState.getNumberOfHintsUsed())
        };

        LinearLayout numbersAndLabels = ActivityDrawingUtils.makeGameSummaryTextsAndCaptions(this, numbersStrings, labels, TEXT_SIZE_NUMBER_GAME_STAT,
                TEXT_SIZE_LABEL_GAME_STAT, SIDE_PADDING_STATS_ICONS);
        pageContent.addView(numbersAndLabels);
        LinearLayout gameGrid = ActivityDrawingUtils.drawGameBoard(this, gameWinState, originalbulbStatuses, userToggles);
        pageContent.addView(gameGrid);

        LinearLayout bottomButtons = createButtonsToOtherActivities();
        pageContent.addView(bottomButtons);


        //TODO: update code for move counter animationsanimations
        TextView movesTextView = (TextView) ((LinearLayout) numbersAndLabels.getChildAt(1)).getChildAt(0);
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(0, gameWinState.getNumberOfMoves());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                movesTextView.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator.setDuration(2000);
        animator.start();


    }

    private LinearLayout createButtonsToOtherActivities() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(SIDE_PADDING_STATS_ICONS, 0, SIDE_PADDING_STATS_ICONS, 0);
        LinearLayout allStatsLinkLayout = createImageIconAndTextLayout(getResources().getDrawable(R.drawable.icons8_chart), "stats");
        LinearLayout nextLevelLinkLayout = createImageIconAndTextLayout(getResources().getDrawable(R.drawable.right_arrow), "next level");
        LinearLayout restartLevelLinkLayout = createImageIconAndTextLayout(getResources().getDrawable(R.drawable.restart_game), "play again");

        //TODO: fix the on click listeners to implement the right methods (eg database calls, dialog boxes, etc)
        allStatsLinkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked on stats link");
            }
        });
        linearLayout.addView(allStatsLinkLayout);

        nextLevelLinkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked on next level link");
            }
        });

        linearLayout.addView(nextLevelLinkLayout);

        restartLevelLinkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked on reset link");
            }
        });
        linearLayout.addView(restartLevelLinkLayout);
        return linearLayout;
    }

    private LinearLayout createImageIconAndTextLayout(Drawable drawable, String text) {
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, ONE_THIRD));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        ImageView imageView = getImageView(drawable);
        TextView textView = ActivityDrawingUtils.getTextView(this, text, BOTTOM_ICONS_LABEL_TEXT_SIZE, false);
        layout.addView(imageView);
        layout.addView(textView);
        layout.setEnabled(true);
        layout.setClickable(true);
        TypedValue outValue = new TypedValue();
        getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        layout.setBackgroundResource(outValue.resourceId);
        return layout;

    }

    private ImageView getImageView(Drawable drawable) {
        ImageView imageView = new ImageView(this);
        imageView.setBackground(drawable);
        int imageSize = ActivityDrawingUtils.convertIntValueToAppropriatePixelValueForScreenSize(this, BOTTOM_ICONS_IMAGE_SIZE);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));
        return imageView;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GameSummaryActivity.this, NewLevelSelectorActivity.class);
        startActivity(intent);
        finish();
    }

    private GameWinState fetchGameWinStateFromLastCompletedGameGridActivity() {
        Intent intent = getIntent();
        GameWinState gameWinState = intent.getParcelableExtra(getString(R.string.game_win_state_label));
        Log.d(TAG, "Recieved game win state from previous activity: " + gameWinState.toString());
        gameWinStateDBHandler.fetchAllGameWinStatesInReverseChronological(gameWinState.getGameMode());
        return gameWinState;
    }


}
