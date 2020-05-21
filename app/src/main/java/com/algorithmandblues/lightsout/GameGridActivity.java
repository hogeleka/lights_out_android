package com.algorithmandblues.lightsout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

    private int dimension;
    private boolean shouldResumeGameFromDB;
    private boolean shouldSetRandomOriginalStartState;
    public GameInstance gameInstance;
    private RelativeLayout gridLayoutHolder;
    private Button undo;
    private Button redo;
    private Button reset;
    private Button showSolution;
    private LinearLayout gameButtonsHolder;

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

        byte[] originalStartState = getOriginalStartState();
        Stack<Integer> undoStack = new Stack<>();
        Stack<Integer> redoStack = new Stack<>();
        byte[] toggledBulbs = Arrays.copyOf(originalStartState, originalStartState.length);
        gameInstance = new GameInstance(this, dimension, originalStartState, toggledBulbs, undoStack, redoStack);
        binding.setGameinstance(gameInstance);
        gridLayoutHolder = findViewById(R.id.game_grid_holder);
        gridLayoutHolder.addView(gameInstance.getGrid());
        gameButtonsHolder = findViewById(R.id.gameButtonsHolder);
        gameButtonsHolder.setVisibility(View.VISIBLE);


        createUndoButton();
        createRedoButton();
        createResetButton();
        createShowSolutionButton();
    }

    private byte[] getOriginalStartState() {
        byte[] originalStartState = new byte[dimension * dimension];
        if (shouldResumeGameFromDB) {
            Arrays.fill(originalStartState, (byte) 1);
//            originalStartState = getStartStateFromDB();
        } else {
            if (shouldSetRandomOriginalStartState) {
                originalStartState = getRandomOriginalStartState(dimension);
            } else {
                Arrays.fill(originalStartState, (byte)1);
            }
        }
        return originalStartState;
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

    private byte[] getStartStateFromDB() {
        byte[] originalStartState = new byte[dimension * dimension];
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
        showSolution.setOnClickListener(v -> handleShowSolution());
    }

    private void handleShowSolution() {
        try {
//            byte[] solution =
//            Log.d("SolutionFound", "Solution:" + Arrays.toString(solution));
            if (this.gameInstance.isShowingSolution()) {
                this.gameInstance.unHighlightSolution(SolutionProvider.getSolution(gameInstance.getDimension(), gameInstance.getToggledBulbs()));
            } else {
                this.gameInstance.highlightSolution(SolutionProvider.getSolution(gameInstance.getDimension(), gameInstance.getToggledBulbs()));
            }

        } catch (UnknownSolutionException e) {
            Log.d("UnknownSolutionFound", e.getMessage());
        }

    }

    private void createResetButton() {
        reset = (Button) findViewById(R.id.reset_to_original_start_state);
        reset.setOnClickListener(v -> handleResetClick());
    }

    private void handleResetClick() {
        if(gameInstance.isShowingSolution()) {
            showSolution.callOnClick();
        }
        gameInstance.resetBoardToOriginalStartState();
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
        Intent i = new Intent(GameGridActivity.this, LevelSelectorActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
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