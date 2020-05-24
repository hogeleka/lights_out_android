package com.algorithmandblues.lightsout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.algorithmandblues.lightsout.databinding.ActivityGameGridBinding;

import java.util.Arrays;
import java.util.Stack;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameGridActivity extends AppCompatActivity {

    SQLiteDatabaseHandler dbHandler;

    private static final String TAG = GameGridActivity.class.getSimpleName();

    private int dimension;
    private boolean shouldResumeGameFromDB;
    private boolean shouldSetRandomOriginalStartState;
    public GameInstance gameInstance;
    GameData gameData;
    private RelativeLayout gridLayoutHolder;
    private Button undo;
    private Button redo;
    private Button reset;
    private Button showSolution;
    private Button randomize;
    private LinearLayout gameButtonsHolder;
    private RelativeLayout gameTextHolder;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 0;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = () -> {
        // Delayed display of UI elements
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    };

    private final Runnable mHideRunnable = this::hide;
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    @SuppressLint("ClickableViewAccessibility")
    private final View.OnTouchListener mDelayHideTouchListener = (view, motionEvent) -> {
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = SQLiteDatabaseHandler.getInstance(getApplicationContext());

        ActivityGameGridBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_game_grid);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(view -> toggle());

        dimension = getIntent().getIntExtra(getString(R.string.dimension), 2);
        shouldResumeGameFromDB = getIntent().getBooleanExtra(getString(R.string.resume_from_db_flag), false);
        shouldSetRandomOriginalStartState = getIntent().getBooleanExtra(getString(R.string.set_random_state_flag), false);

        gameData = shouldResumeGameFromDB ? dbHandler.getGameData(dimension) : null;
        byte[] originalStartState = getOriginalStartState(dimension, shouldResumeGameFromDB, shouldSetRandomOriginalStartState);
        byte[] toggledBulbs = shouldResumeGameFromDB ? getToggledBulbStates() : Arrays.copyOf(originalStartState, dimension*dimension);

        Stack<Integer> undoStack = shouldResumeGameFromDB ? getUndoStackFromDBValue(gameData) : new Stack<>();
        Stack<Integer> redoStack = shouldResumeGameFromDB ? getRedoStackFromDBValue(gameData) : new Stack<>();

        gameInstance = new GameInstance(this, dimension, originalStartState, toggledBulbs, undoStack, redoStack);

        binding.setGameinstance(gameInstance);

        gameTextHolder = findViewById(R.id.game_text_view_holder);
        gameTextHolder.setVisibility(View.VISIBLE);
        gridLayoutHolder = findViewById(R.id.game_grid_holder);
        gridLayoutHolder.addView(gameInstance.getGrid());
        gameButtonsHolder = findViewById(R.id.gameButtonsHolder);
        gameButtonsHolder.setVisibility(View.VISIBLE);

        createUndoButton();
        createRedoButton();
        createResetButton();
        createShowSolutionButton();
        createRandomizeButton();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(0);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        showDialogBoxAskingToSaveGame(gameInstance);
    }

    @Override
    public void onDestroy() {
        saveCurrentGameInstance(gameInstance);
        super.onDestroy();
    }

    private Stack<Integer> getRedoStackFromDBValue(GameData gameData) {
        return GameDataUtil.StringToIntegerStack(gameData.getRedoStackString());
    }

    private Stack<Integer> getUndoStackFromDBValue(GameData gameData) {
        return GameDataUtil.StringToIntegerStack(gameData.getUndoStackString());
    }

    private byte[] getToggledBulbStates() {
        return GameDataUtil.stringToByteArray(gameData.getToggledBulbsState());
    }

    private byte[] getOriginalStartState(int dimension, boolean shouldResumeGameFromDB, boolean shouldSetRandomOriginalStartState) {
        byte[] originalStartState = new byte[dimension * dimension];
        if (shouldResumeGameFromDB) {
            originalStartState = getOriginalStartStateFromGameData(gameData);
        } else {
            if (shouldSetRandomOriginalStartState) {
                originalStartState = getRandomOriginalStartState(dimension);
            } else {
                Arrays.fill(originalStartState, (byte)1);
            }
        }
        return originalStartState;
    }

    private byte[] getOriginalStartStateFromGameData(GameData gameData) {
        return GameDataUtil.stringToByteArray(gameData.getOriginalStartState());
    }

    private byte[] getRandomOriginalStartState(int dimension) {
        byte[] originalStartState = new byte[dimension * dimension];
        for (int i = 0; i < originalStartState.length; i++) {
            if (Math.random() <= 0.5) {
                originalStartState[i] = 0;
            } else {
                originalStartState[i] = 1;
            }
        }
        return originalStartState;
    }


    private void createUndoButton() {
        undo = (Button) findViewById(R.id.undo_button);
        undo.setOnClickListener(v -> handleUndoClick());
    }

    private void handleUndoClick() {
        gameInstance.removeFromUndoStack();
    }

    private void createRedoButton() {
        redo = (Button) findViewById(R.id.redo_button);
        redo.setOnClickListener(v -> handleRedoClick());
    }

    private void handleRedoClick() {
        gameInstance.removeFromRedoStack();
        redo.setEnabled(!gameInstance.getRedoStack().empty());
    }

    private void createShowSolutionButton() {
        showSolution = (Button) findViewById(R.id.solution);
        showSolution.setBackgroundColor(getResources().getColor(R.color.Transparent));
        showSolution.setOnClickListener(v -> handleShowSolution());
    }

    private void handleShowSolution() {
        try {
            if (gameInstance.getIsShowingSolution()) {
                gameInstance.unHighlightSolution(SolutionProvider.getSolution(gameInstance.getDimension(), gameInstance.getToggledBulbs()));
            } else {
                gameInstance.highlightSolution(SolutionProvider.getSolution(gameInstance.getDimension(), gameInstance.getToggledBulbs()));
            }

        } catch (UnknownSolutionException e) {
            Log.d(TAG, "UnknownSolutionFound: " + e.getMessage());
        }
        Log.d(TAG, "Button Alpha" + showSolution.getAlpha());

    }

    private void createResetButton() {
        reset = (Button) findViewById(R.id.reset_to_original_start_state);
        reset.setOnClickListener(v -> handleResetClick());
    }

    private void handleResetClick() {
        this.undoShowSolutionIfNeeded();
        gameInstance.resetBoardToState(gameInstance.getOriginalStartState());
    }


    private void createRandomizeButton() {
        randomize = (Button) findViewById(R.id.randomize_state);
        randomize.setOnClickListener(v -> handleRandomizeClick());
    }

    private void handleRandomizeClick() {
        this.undoShowSolutionIfNeeded();
        gameInstance.setOriginalStartState(this.getRandomOriginalStartState(gameInstance.getDimension()));
        gameInstance.resetBoardToState(gameInstance.getOriginalStartState());
    }

    private void undoShowSolutionIfNeeded() {
        if (gameInstance.getIsShowingSolution()) {
            showSolution.callOnClick();
        }
    }

    private void returnToLevelSelector() {
        Intent i = new Intent(GameGridActivity.this, LevelSelectorActivity.class);
        startActivity(i);
    }

    private void removeDataForCurrentDimensionFromDB(GameInstance gameInstance) {
        dbHandler.deleteRowForSpecificDimension(gameInstance.getDimension());
    }

    private void saveCurrentGameInstance(GameInstance gameInstance) {
        GameData gameData = getGameDataFromCurrentGameInstance(gameInstance);
        dbHandler.addGameData(gameData);
    }

    private GameData getGameDataFromCurrentGameInstance(GameInstance gameInstance) {
        int dimension = gameInstance.getDimension();
        String originalStartStateString = GameDataUtil.byteArrayToString(gameInstance.getOriginalStartState());
        String toggledBulbsStateString = GameDataUtil.byteArrayToString(gameInstance.getToggledBulbs());
        String undoStackString = GameDataUtil.IntegerStackToString(gameInstance.getUndoStack());
        String redoStackString = GameDataUtil.IntegerStackToString(gameInstance.getRedoStack());
        return new GameData(dimension, originalStartStateString, toggledBulbsStateString, undoStackString, redoStackString);
    }

    private void showDialogBoxAskingToSaveGame(GameInstance instance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameGridActivity.this);
        builder.setTitle(getString(R.string.request_to_save_game_title))
                .setMessage(String.format(getString(R.string.request_to_save_game_message), gameInstance.getDimension(), gameInstance.getDimension()))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveCurrentGameInstance(instance);
                        returnToLevelSelector();
                    }
                })
                .setNegativeButton(getString(R.string.no_do_not_save_current_game), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeDataForCurrentDimensionFromDB(instance);
                        returnToLevelSelector();
                    }
                });
        //Creating dialog box
        AlertDialog dialog  = builder.create();
        dialog.show();
    }


    private void toggle() {
        hide();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}