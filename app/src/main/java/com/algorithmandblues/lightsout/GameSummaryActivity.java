package com.algorithmandblues.lightsout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class GameSummaryActivity extends AppCompatActivity {

    private static final int STAR_IMAGE_SIZE_PX = 55;
    private static final int ROW_OF_STARS_LEFT_RIGHT_PADDING = 10;
    private static final int ROW_OF_STARS_TOP_BOTTOM_PADDING = 10;
    private static final int TEXT_SIZE_NUMBER_GAME_STAT = 50;
    private static final int TEXT_SIZE_LABEL_GAME_STAT = 16;
    private static final int BOTTOM_ICONS_IMAGE_SIZE = 40;
    private static final int BOTTOM_ICONS_LABEL_TEXT_SIZE = 16;
    private static final int SIDE_PADDING_STATS_ICONS = 0;
    private static final int BUTTON_PADDING_TOP = 16;
    private static final int PROGRESS_BAR_ANIMATION_DURATION = 500;
    private static final int PROGRESS_BAR_PADDING = 20;
    private static final int SOLUTION_DESCRIPTOR_PADDING_TOP = 24;
    private static final int SOLUTION_DESCRIPTOR_PADDING_BOTTOM = 10;
    private static final int TEXT_SIZE_SKILL = 30;
    private static final float ONE_THIRD = (float) 0.33;


    // 750 is duration of each star animation. Defined in staranimation.xml
    private static final BounceInterpolator BOUNCE_INTERPOLATOR = new BounceInterpolator(0.2, 20);
    private static final String SOLUTION_DESCRIPTOR = "your solution:";

    private boolean newLevelIsUnlocked;
    private int bestScoreForLevelAndGameType;
    DatabaseHelper databaseHelper;
    GameDataObjectDBHandler gameDataObjectDBHandler;
    GameWinStateDBHandler gameWinStateDBHandler;
    GameWinState gameWinState;
    LinearLayout pageContent;
    Level nextLevel;
    LevelDBHandler levelDBHandler;
    ProgressBarAnimation progressBarAnimation;
    ValueAnimator moveTextAnimator;
    TextSwitcher skillLevelTextSwitcher;

    private static final String TAG = GameSummaryActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_summary);
        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        gameDataObjectDBHandler = GameDataObjectDBHandler.getInstance(databaseHelper);
        gameWinStateDBHandler = GameWinStateDBHandler.getInstance(databaseHelper);
        gameWinState = fetchGameWinStateFromLastCompletedGameGridActivity();

        levelDBHandler = LevelDBHandler.getInstance(databaseHelper);

        bestScoreForLevelAndGameType = getIntent().getIntExtra(getString(R.string.best_score_level_gameType), 0);
        pageContent = findViewById(R.id.fullscreen_content);
        LinearLayout rowOfStars = ActivityDrawingUtils.makeRowOfStars(this, gameWinState.getNumberOfStars(), STAR_IMAGE_SIZE_PX, ROW_OF_STARS_LEFT_RIGHT_PADDING, ROW_OF_STARS_TOP_BOTTOM_PADDING);
        pageContent.addView(rowOfStars);
        Animation starAnimation  = AnimationUtils.loadAnimation(this, R.anim.staranimation);
        starAnimation.setInterpolator(BOUNCE_INTERPOLATOR);
        LayoutAnimationController rowOfStarsAnimationController = new LayoutAnimationController(starAnimation);
        rowOfStarsAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        rowOfStars.setLayoutAnimation(rowOfStarsAnimationController);

        int currentBestLevel = getCurrentBestLevelDimensionFromDb();
        newLevelIsUnlocked = false;
        if(gameWinState.getDimension() != DatabaseConstants.MAX_DIMENSION) {
            nextLevel = levelDBHandler.getLevelFromDb(gameWinState.getGameMode(), gameWinState.getDimension() + 1);
            newLevelIsUnlocked = checkIfNewLevelUnlocked(nextLevel);
            if(newLevelIsUnlocked) {
                updateNewLevelInDb();
            }
        }

        int powerSaved = gameWinState.getOriginalBoardPower();
        byte[] originalBulbStatuses = getIntent().getByteArrayExtra(getString(R.string.initial_board_config));
        int[] movesPerBulb = getIntent().getIntArrayExtra(getString(R.string.moves_per_bulb));

        Log.d(TAG, "moves per bulb: " + Arrays.toString(movesPerBulb));

        String[] labels = {
                getString(R.string.game_summary_watts_saved_label),
                getString(R.string.game_summary_moves_label),
                getString(R.string.game_summary_hints_used_label)
        };
        String[] numbersStrings = {
                String.valueOf(powerSaved) ,
                "0",
                String.valueOf(gameWinState.getNumberOfHintsUsed())
        };

        LinearLayout numbersAndLabels = ActivityDrawingUtils.makeGameSummaryTextsAndCaptions(this, numbersStrings, labels, TEXT_SIZE_NUMBER_GAME_STAT,
                TEXT_SIZE_LABEL_GAME_STAT, SIDE_PADDING_STATS_ICONS);
        pageContent.addView(numbersAndLabels);

        TextView solutionDescriptor = ActivityDrawingUtils.getTextView(this, SOLUTION_DESCRIPTOR, TEXT_SIZE_LABEL_GAME_STAT, false);
        solutionDescriptor.setPadding(0, getPixels(SOLUTION_DESCRIPTOR_PADDING_TOP), getPixels(SOLUTION_DESCRIPTOR_PADDING_BOTTOM), 0);
        pageContent.addView(solutionDescriptor);

        LinearLayout gameGrid = ActivityDrawingUtils.drawGameBoard(this, gameWinState, originalBulbStatuses, movesPerBulb);
        pageContent.addView(gameGrid);


        LinearLayout bottomButtons = createButtonsToOtherActivities();
        pageContent.addView(bottomButtons);

        skillLevelTextSwitcher = createSkillLevelTextSwitcher(this, currentBestLevel);
        pageContent.addView(skillLevelTextSwitcher);


        ProgressBar progressBar = this.getUserProgressBar(currentBestLevel);
        Log.d(TAG, "progress: " + progressBar.getProgress());
        pageContent.addView(progressBar);


        TextView movesTextView = (TextView) ((LinearLayout) numbersAndLabels.getChildAt(1)).getChildAt(0);
        moveTextAnimator = createMoveTextAnimator(movesTextView);
        progressBarAnimation = createProgressBarAnimation(progressBar);

        rowOfStarsAnimationController.start();
        rowOfStars.setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveTextAnimator.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        moveTextAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (newLevelIsUnlocked) {
                    progressBar.startAnimation(progressBarAnimation);
                }
            }
        });


        progressBarAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                skillLevelTextSwitcher.setText(getSkillText(nextLevel.getDimension()));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private ValueAnimator createMoveTextAnimator(TextView movesTextView) {
        return new ValueAnimator() {{
            setObjectValues(0, gameWinState.getNumberOfMoves());
            addUpdateListener(animation1 -> movesTextView.setText(String.valueOf(animation1.getAnimatedValue())));
            setDuration(calculateMoveTextAnimatorDuration(gameWinState.getNumberOfMoves()));
        }};
    }

    private ProgressBarAnimation createProgressBarAnimation(ProgressBar progressBar) {
        int nextLevelProgress = (int) (((double) nextLevel.getDimension() / DatabaseConstants.MAX_DIMENSION) * 100);
        return new ProgressBarAnimation(progressBar, progressBar.getProgress(), nextLevelProgress) {{
            setDuration(PROGRESS_BAR_ANIMATION_DURATION);
        }};
    }

    private TextSwitcher createSkillLevelTextSwitcher(Context context, int currentBestLevel) {
        return new TextSwitcher(context) {{
            setInAnimation(context, android.R.anim.slide_in_left);
            setOutAnimation(context, android.R.anim.slide_out_right);
            setFactory(() -> ActivityDrawingUtils.getTextView(context, getSkillText(currentBestLevel), TEXT_SIZE_SKILL, false));
            setPadding(0, getPixels(20), 0, 0);
        }};
    }

    /**
     * Gets the current highest unlocked level dimension from db before new level is unlocked
     */
    private int getCurrentBestLevelDimensionFromDb() {
        List<Level> levels = levelDBHandler.fetchLevelsForGameMode(GameMode.ARCADE);
        int result = 2;
        for (Level level : levels) {
            if (level.getDimension() > result && level.getIsLocked() == DatabaseConstants.UNLOCKED_LEVEL) {
                result = level.getDimension();
            }
        }
        return result;
    }

    private long calculateMoveTextAnimatorDuration(int numberOfMoves) {
        NavigableMap<Integer, Integer> map = new TreeMap<Integer, Integer>() {{
            put(0, 500);      // 0..9       => 0
            put(10, 1000);    // 10..100    => 1
            put(100, 2000);   // 100..200   => 2
        }};

        // To do a lookup for some value in 'key'
        if (numberOfMoves < 0 || numberOfMoves > 200) {
            return 3000;
        } else {
            return map.floorEntry(numberOfMoves).getValue();
        }
    }

    private String getSkillText(int dimension) {
        return SkillLevelConstants.getSkillLevelForLevel(dimension);
    }

    private TextView createSkillLevelTextView(String currentSkillText) {
        TextView skillTextView = ActivityDrawingUtils.getTextView(this, currentSkillText, BOTTOM_ICONS_LABEL_TEXT_SIZE, false);
        skillTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        return skillTextView;
    }

    private void updateNewLevelInDb() {
        nextLevel.setIsLocked(DatabaseConstants.UNLOCKED_LEVEL);
        levelDBHandler.updateLevelWithNewNumberOfStars(nextLevel);
    }

    private boolean checkIfNewLevelUnlocked(Level nextLevel) {

        // If it is classic return false as all levels are already unlocked.
        if (nextLevel.getGameMode() == GameMode.CLASSIC) {
            return false;
        } else {
            // If next level is already unlocked then return false
            if (nextLevel.getIsLocked() == DatabaseConstants.UNLOCKED_LEVEL) {
                return false;
            } else {

                // if next level is locked but the user cheated then return false
                if (gameWinState.getNumberOfStars() == 0) {
                    return false;
                } else {

                    // Next level is locked and user did not cheat so return true to unlock this level.
                    return true;
                }
            }
        }
    }

    private LinearLayout createButtonsToOtherActivities() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(SIDE_PADDING_STATS_ICONS, BUTTON_PADDING_TOP, SIDE_PADDING_STATS_ICONS, 0);
        LinearLayout allStatsLinkLayout = createImageIconAndTextLayout(getResources().getDrawable(R.drawable.icons8_chart), "stats");
        LinearLayout nextLevelLinkLayout = createImageIconAndTextLayout(getResources().getDrawable(R.drawable.right_arrow), "next level");
        LinearLayout restartLevelLinkLayout = createImageIconAndTextLayout(getResources().getDrawable(R.drawable.restart_game), "play again");

        //TODO: fix the on click listeners to implement the right methods (eg database calls, dialog boxes, etc)
        allStatsLinkLayout.setOnClickListener(v -> Log.d(TAG, "Clicked on stats link"));
        linearLayout.addView(allStatsLinkLayout);

        nextLevelLinkLayout.setEnabled(gameWinState.getDimension() != DatabaseConstants.MAX_DIMENSION
                && nextLevel.getIsLocked() == DatabaseConstants.UNLOCKED_LEVEL);

        nextLevelLinkLayout.setAlpha(gameWinState.getDimension() != DatabaseConstants.MAX_DIMENSION
                && nextLevel.getIsLocked() == DatabaseConstants.UNLOCKED_LEVEL ?
                ActivityDrawingUtils.ENABLED_LEVEL_ALPHA : ActivityDrawingUtils.DISABLED_LEVEL_ALPHA);

        nextLevelLinkLayout.setOnClickListener(v -> {
            Log.d(TAG, "clicked on next level link");
            goToNextLevel();
        });

        linearLayout.addView(nextLevelLinkLayout);

        restartLevelLinkLayout.setOnClickListener(v -> {
            Log.d(TAG, "Clicked on restart link");
            restartLevel();
        });
        linearLayout.addView(restartLevelLinkLayout);
        return linearLayout;
    }

    private void goToNextLevel() {
        boolean hasDataForNextLevel = gameDataObjectDBHandler.getMostRecentGameDataForGameType(nextLevel.getDimension(), nextLevel.getGameMode()) != null;
        if(hasDataForNextLevel) {
               buildDialogToRequestUserResponse(nextLevel.getDimension(), gameWinState.getGameMode() == GameMode.ARCADE);
        } else {
            goToLevel(nextLevel.getDimension(), false, nextLevel.getGameMode() == GameMode.ARCADE, nextLevel.getNumberOfStars());
        }
    }

    private void restartLevel() {
        goToLevel(gameWinState.getDimension(), false, gameWinState.getGameMode() == GameMode.ARCADE, bestScoreForLevelAndGameType);
    }

    public void buildDialogToRequestUserResponse(int dimension, boolean setRandomStateFlag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameSummaryActivity.this);
        builder.setTitle(getString(R.string.level_picker_resume_or_restart_title))
                .setMessage(String.format(getString(R.string.level_picker_resume_or_restart_message_prompt), dimension, dimension))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    goToLevel(dimension, true, setRandomStateFlag, bestScoreForLevelAndGameType);
                })
                .setNegativeButton(getString(R.string.restart_new_game), (dialog, which) -> {
                    goToLevel(dimension, false, setRandomStateFlag, bestScoreForLevelAndGameType);
                });
        AlertDialog dialog  = builder.create();
        dialog.show();
    }

    private void goToLevel(int dimension, boolean resumeFromDb, boolean shouldSetRandomStateFlag, int bestScoreForLevelAndGameType) {
        Intent intent = new Intent(GameSummaryActivity.this, GameGridActivity.class);
        intent.putExtra(getString(R.string.dimension), dimension);
        intent.putExtra(getString(R.string.resume_from_db_flag), resumeFromDb);
        intent.putExtra(getString(R.string.set_random_state_flag), shouldSetRandomStateFlag);
        intent.putExtra(getString(R.string.best_score_level_gameType), bestScoreForLevelAndGameType);
        startActivity(intent);
        finish();
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
        int imageSize = getPixels(BOTTOM_ICONS_IMAGE_SIZE);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));
        return imageView;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GameSummaryActivity.this, SelectLevelActivity.class);
        intent.putExtra(getString(R.string.selected_game_mode), gameWinState.getGameMode());
        startActivity(intent);
        finish();
    }

    private GameWinState fetchGameWinStateFromLastCompletedGameGridActivity() {
        Intent intent = getIntent();
        GameWinState gameWinState = intent.getParcelableExtra(getString(R.string.game_win_state_label));
        Log.d(TAG, "Recieved game win state from previous activity: " + gameWinState.toString());
        return gameWinState;
    }


    private ProgressBar getUserProgressBar(int level) {
        return new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal) {{
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setProgressDrawable(getResources().getDrawable(R.drawable.customprogressbarstyle));
            setPadding(getPixels(PROGRESS_BAR_PADDING), 0, getPixels(PROGRESS_BAR_PADDING), 0);
            setProgress((int) (((double) level / DatabaseConstants.MAX_DIMENSION) * 100));
        }};
    }

    public int getPixels(int value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }
}
