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

import java.util.Stack;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameGridActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    GameDataObjectDao gameDataObjectDao;

    private static final String TAG = GameGridActivity.class.getSimpleName();

    private int dimension;
    private boolean shouldResumeGameFromDB;
    private boolean shouldSetRandomOriginalStartState;
    public GameInstance gameInstance;
    GameDataObject gameDataObject;
    private RelativeLayout gridLayoutHolder;
    private Button undo;
    private Button redo;
    private Button reset;
    private Button showSolution;
    private Button randomize;
    private LinearLayout gameButtonsHolder;
    private RelativeLayout gameTextHolder;
    private RelativeLayout moveCounterTextHolder;
    private Stack<Integer> undoStack;
    private Stack<Integer> redoStack;
    private int moveCounter;

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

        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        gameDataObjectDao = GameDataObjectDao.getInstance(databaseHelper);

        ActivityGameGridBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_game_grid);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(view -> toggle());

        dimension = getIntent().getIntExtra(getString(R.string.dimension), 2);
        shouldResumeGameFromDB = getIntent().getBooleanExtra(getString(R.string.resume_from_db_flag), false);
        shouldSetRandomOriginalStartState = getIntent().getBooleanExtra(getString(R.string.set_random_state_flag), false);

        gameDataObject = shouldResumeGameFromDB ? gameDataObjectDao.getMostRecentGameDataForGameType(dimension, 1) : getDefaultGameDataObject(dimension, 1);
//        byte[] originalStartState = getOr(dimension, shouldResumeGameFromDB, shouldSetRandomOriginalStartState);
//        String originalStartState =
//        byte[] toggledBulbs = shouldResumeGameFromDB ? getToggledBulbStates(gameDataObject) : Arrays.copyOf(originalStartState, dimension*dimension);
//        undoStack = shouldResumeGameFromDB ? getUndoStackFromDBValue(gameDataObject) : new Stack<>();
//        redoStack = shouldResumeGameFromDB ? getRedoStackFromDBValue(gameDataObject) : new Stack<>();
//        moveCounter = shouldResumeGameFromDB ? getMoveCounterFromDBValue(gameDataObject) : 0; // Get from db eventually

        gameInstance = new GameInstance(this, gameDataObject);

        binding.setGameinstance(gameInstance);

        gameTextHolder = findViewById(R.id.game_text_view_holder);
        gameTextHolder.setVisibility(View.VISIBLE);
        moveCounterTextHolder = findViewById(R.id.moveCounter_text_view_holder);
        moveCounterTextHolder.setVisibility(View.VISIBLE);
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

    private GameDataObject getDefaultGameDataObject(int dimension, int gameMode) {
//        ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, LAST_SAVED_INSTANCE_STATE, UNDO_STACK_STRING,
//        REDO_STACK_STRING, GAME_MODE, HAS_SEEN_SOLUTION, MOVE_COUNTER, NUMBER_OF_HINTS_USED
        GameDataObject gameDataObject = new GameDataObject() {{
            setDimension(dimension);
            setOriginalStartState(getOriginalStartStateString(dimension));
            setToggledBulbsState(getOriginalStartState());
            setLastSavedState(getOriginalStartState());
            setUndoStackString(GameDataUtil.EMPTY_STRING);
            setRedoStackString(GameDataUtil.EMPTY_STRING);
            setGameMode(gameMode);
            setHasSeenSolution(false);
            setMoveCounter(0);
            setNumberOfHintsUsed(0);
        }};
        return gameDataObject;
    }

    private int getMoveCounterFromDBValue(GameDataObject gameDataObject) {
        return gameDataObject.getMoveCounter();
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
         if (gameInstance.getHasMadeAtLeastOneMove()) {
            saveCurrentGameInstance(gameInstance);
        } else {
            if (gameInstance.getGameMode() == GameMode.ARCADE) {
                saveCurrentGameInstance(gameInstance);
            }
        }
        Intent intent = new Intent(GameGridActivity.this, LevelSelectorActivity.class);
        startActivity(intent);
//        showDialogBoxAskingToSaveGame(gameInstance);
    }

    @Override
    public void onDestroy() {
//        saveCurrentGameInstance(gameInstance);
//        if (gameInstance.getHasMadeAtLeastOneMove()) {
//            saveCurrentGameInstance(gameInstance);
//        } else {
//            if (gameInstance.getGameMode() == GameMode.ARCADE) {
//                saveCurrentGameInstance(gameInstance);
//            }
//        }
        if (gameInstance.getHasMadeAtLeastOneMove()) {
            saveCurrentGameInstance(gameInstance);
        } else {
            if (gameInstance.getGameMode() == GameMode.ARCADE) {
                saveCurrentGameInstance(gameInstance);
            }
        }
        super.onDestroy();
    }




    private Stack<Integer> getRedoStackFromDBValue(GameDataObject gameDataObject) {
        return GameDataUtil.stringToIntegerStack(gameDataObject.getRedoStackString());
    }

    private Stack<Integer> getUndoStackFromDBValue(GameDataObject gameDataObject) {
        return GameDataUtil.stringToIntegerStack(gameDataObject.getUndoStackString());
    }

    private byte[] getToggledBulbStates(GameDataObject gameDataObject) {
        return GameDataUtil.stringToByteArray(gameDataObject.getToggledBulbsState());
    }

//    private byte[] getOriginalStartState(int dimension, boolean shouldResumeGameFromDB, boolean shouldSetRandomOriginalStartState) {
//        byte[] originalStartState = new byte[dimension * dimension];
//        if (shouldResumeGameFromDB) {
//            originalStartState = getOriginalStartStateFromGameData(gameDataObject);
//        } else {
//            if (shouldSetRandomOriginalStartState) {
//                originalStartState = getRandomOriginalStartState(dimension);
//            } else {
//                Arrays.fill(originalStartState, (byte)1);
//            }
//        }
//        return originalStartState;
//    }

    private byte[] getOriginalStartStateFromGameData(GameDataObject gameDataObject) {
        return GameDataUtil.stringToByteArray(gameDataObject.getOriginalStartState());
    }

    private byte[] getRandomOriginalStartStateByteArray(int dimension) {
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

    private String getOriginalStartStateString(int dimension) {
        StringBuilder originalStartState = new StringBuilder();
        for (int i = 0; i < dimension*dimension; i++) {
            if(!shouldSetRandomOriginalStartState) {
                originalStartState.append("1,");
            } else {
                if (Math.random() <= 0.5) {
                    originalStartState.append("0,");
                } else {
                    originalStartState.append("1,");
                }
            }
        }
        originalStartState.deleteCharAt(originalStartState.length() - 1);
        return originalStartState.toString();
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
        if(!gameInstance.getHasSeenSolution()) {
            gameInstance.setHasSeenSolution(true);
        }
        try {
            if (gameInstance.getIsShowingSolution()) {
                gameInstance.unHighlightSolution(SolutionProvider.getSolution(gameInstance.getDimension(), gameInstance.getCurrentToggledBulbs()));
            } else {
                gameInstance.highlightSolution(SolutionProvider.getSolution(gameInstance.getDimension(), gameInstance.getCurrentToggledBulbs()));
            }
            Log.d(TAG, "Showing Solution");

        } catch (UnknownSolutionException e) {
            Log.d(TAG, "UnknownSolutionFound: " + e.getMessage());
        }
    }

    private void createResetButton() {
        reset = (Button) findViewById(R.id.reset_to_original_start_state);
        reset.setOnClickListener(v -> handleResetClick());
    }

    private void handleResetClick() {
        this.undoShowSolutionIfNeeded();
        // currently move counter is 0 but this will need to be changed to moveCounter in gameData
        gameInstance.resetBoardToState(
                GameDataUtil.stringToByteArray(gameDataObject.getToggledBulbsState()),
                gameDataObject.getMoveCounter(),
                GameDataUtil.stringToIntegerStack(gameDataObject.getUndoStackString()),
                GameDataUtil.stringToIntegerStack(gameDataObject.getRedoStackString())
        );

        Log.d(TAG, "Resetting board to Last Saved Instance or Default state if there was no saved instance");

    }


    private void createRandomizeButton() {
        randomize = (Button) findViewById(R.id.randomize_state);
        randomize.setOnClickListener(v -> handleRandomizeClick());
    }

    private void handleRandomizeClick() {
        this.undoShowSolutionIfNeeded();
        gameDataObject = getDefaultGameDataObject(dimension, GameMode.ARCADE);
        undoStack = GameDataUtil.stringToIntegerStack(gameDataObject.getUndoStackString());
        redoStack = GameDataUtil.stringToIntegerStack(gameDataObject.getRedoStackString());
        moveCounter = gameDataObject.getMoveCounter();
        gameInstance.setOriginalStartState(GameDataUtil.stringToByteArray(gameDataObject.getOriginalStartState()));
        gameInstance.setHasSeenSolution(gameDataObject.getHasSeenSolution());
        gameInstance.resetBoardToState(gameInstance.getOriginalStartState(), moveCounter, undoStack, redoStack);

        Log.d(TAG, "Resetting Board to new Randomized State");
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
//        dbHandler.deleteRowForSpecificDimension(gameInstance.getDimension());
    }

    private void saveCurrentGameInstance(GameInstance gameInstance) {
        GameDataObject gameDataObject = getGameDataObjectFromCurrentGameInstance(gameInstance);
        gameDataObjectDao.addGameDataObjectToDatabase(gameDataObject);
//        dbHandler.addGameData(gameData);
    }

    private GameDataObject getGameDataObjectFromCurrentGameInstance(GameInstance gameInstance) {
//        int dimension = gameInstance.getDimension();

        //        ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, LAST_SAVED_INSTANCE_STATE, UNDO_STACK_STRING,
//        REDO_STACK_STRING, GAME_MODE, HAS_SEEN_SOLUTION, MOVE_COUNTER, NUMBER_OF_HINTS_USED
        GameDataObject gameDataObject = new GameDataObject(){{
            setDimension(gameInstance.getDimension());
            setOriginalStartState(GameDataUtil.byteArrayToString(gameInstance.getOriginalStartState()));
            setToggledBulbsState(GameDataUtil.byteArrayToString(gameInstance.getCurrentToggledBulbs()));
            setLastSavedState("1,0,1,0,1,0,1,0,0");
            setUndoStackString(GameDataUtil.IntegerStackToString(gameInstance.getUndoStack()));
            setRedoStackString(GameDataUtil.IntegerStackToString(gameInstance.getRedoStack()));
            setGameMode(GameMode.ARCADE); //TODO: replace with real data from level selector
            setHasSeenSolution(gameInstance.getHasSeenSolution());
            setMoveCounter(gameInstance.getMoveCounter());
            setNumberOfHintsUsed(10);

        }};

        return gameDataObject;
//        gameDataObject.setDimension(dimension);
//        String originalStartStateString = GameDataUtil.byteArrayToString(gameInstance.getOriginalStartState());
//        String toggledBulbsStateString = GameDataUtil.byteArrayToString(gameInstance.getToggledBulbs());
//        String lastSavedInstance = GameDataUtil.byteArrayToString(new byte[9]);
//        String undoStackString = GameDataUtil.IntegerStackToString(gameInstance.getUndoStack());
//        String redoStackString = GameDataUtil.IntegerStackToString(gameInstance.getRedoStack());
//
//        return new GameData(dimension, originalStartStateString, toggledBulbsStateString, undoStackString, redoStackString);
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