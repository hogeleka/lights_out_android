package com.algorithmandblues.lightsout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.algorithmandblues.lightsout.databinding.ActivityGameGridBinding;
import java.beans.PropertyChangeListener;
import java.util.Random;

public class GameGridActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    LevelDBHandler levelDBHandler;

    int currentBestScoreForDimensionAndGameType;

    GameDataObjectDBHandler gameDataObjectDBHandler;
    GameWinStateDBHandler gameWinStateDBHandler;

    private View mPendulum;
    private Animation mAnimation;

    private static final String TAG = GameGridActivity.class.getSimpleName();

    private int dimension;
    private boolean shouldSetRandomOriginalStartState;
    public GameInstance gameInstance;
    GameDataObject gameDataObject;
    public PropertyChangeListener listener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        gameDataObjectDBHandler = GameDataObjectDBHandler.getInstance(databaseHelper);
        gameWinStateDBHandler = GameWinStateDBHandler.getInstance(databaseHelper);

        levelDBHandler = LevelDBHandler.getInstance(databaseHelper);

        ActivityGameGridBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_game_grid);

        currentBestScoreForDimensionAndGameType = getIntent().getIntExtra(getString(R.string.best_score_level_gameType), 0);
        dimension = getIntent().getIntExtra(getString(R.string.dimension), 2);
        boolean shouldResumeGameFromDB = getIntent().getBooleanExtra(getString(R.string.resume_from_db_flag), false);
        shouldSetRandomOriginalStartState = getIntent().getBooleanExtra(getString(R.string.set_random_state_flag), false);
        int gameMode = shouldSetRandomOriginalStartState ? GameMode.ARCADE : GameMode.CLASSIC;
        gameDataObject = shouldResumeGameFromDB ? gameDataObjectDBHandler.getMostRecentGameDataForGameType(dimension, gameMode) : getDefaultGameDataObject(dimension, gameMode);


        gameInstance = new GameInstance(this, gameDataObject);

        listener = evt -> {
            if (gameInstance.getIsGameOver()){
                Log.d(TAG, "Game is Over!");
                GameWinState gameWinState = getGameWinStateFromGameInstance(gameInstance);
                removeCurrentGameFromTableOfCurrentGames(gameWinState.getDimension(), gameInstance.getGameMode());
                int insertedGameWinStateId = insertGameWinStateObjectIntoGameWinStateTable(gameWinState);
                gameWinState.setId(insertedGameWinStateId);
                updateDBForBestScorePerLevel(gameWinState);
                sendGameWinStateToNewActivity(gameWinState, gameInstance);

            }
        };

        gameInstance.gameOverChange.addPropertyChangeListener(listener);
        binding.setGameinstance(gameInstance);

        RelativeLayout gameTextHolder = findViewById(R.id.game_title_text_view_holder);
        gameTextHolder.setVisibility(View.VISIBLE);
        RelativeLayout powerConsumptionTextHolder = findViewById(R.id.power_text_view_holder);
        powerConsumptionTextHolder.setVisibility(View.VISIBLE);
        RelativeLayout hintsLeftTextHolder = findViewById(R.id.hints_left_text_view_holder);
        hintsLeftTextHolder.setVisibility(View.VISIBLE);
        RelativeLayout moveCounterTextHolder = findViewById(R.id.move_counter_text_view_holder);
        moveCounterTextHolder.setVisibility(View.VISIBLE);
        RelativeLayout gridLayoutHolder = findViewById(R.id.game_grid_holder);
        gridLayoutHolder.addView(gameInstance.getGrid());
        LinearLayout gameButtonsHolder = findViewById(R.id.gameButtonsHolder);
        gameButtonsHolder.setVisibility(View.VISIBLE);

        createUndoButton();
        createRedoButton();
        createResetButton();
        createHintButton();
        createShowSolutionButton();
        createNewGameButton();

        mPendulum = findViewById(R.id.pendulum);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.swinging);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPendulum.startAnimation(mAnimation);
    }

    @Override
    public void onPause() {
        mPendulum.clearAnimation();
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
        returnToLevelSelector();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateDBForBestScorePerLevel(GameWinState gameWinState) {
        if (gameWinState.getNumberOfStars() > currentBestScoreForDimensionAndGameType){
            Level level = new Level() {{
                setDimension(gameWinState.getDimension());
                setGameMode(gameWinState.getGameMode());
                setNumberOfStars(gameWinState.getNumberOfStars());
                setIsLocked(0);
            }};
            levelDBHandler.updateLevelWithNewNumberOfStars(level);
            currentBestScoreForDimensionAndGameType = gameWinState.getNumberOfStars();
        }
    }

    private void sendGameWinStateToNewActivity(GameWinState gameWinState, GameInstance gameInstance) {
        Intent intent = new Intent(GameGridActivity.this, GameSummaryActivity.class);
        intent.putExtra(getString(R.string.game_win_state_label), gameWinState);
        intent.putExtra(getString(R.string.initial_board_config), gameInstance.getOriginalIndividualBulbStatus());
        intent.putExtra(getString(R.string.moves_per_bulb), gameInstance.getMoveCounterPerBulb());
        intent.putExtra(getResources().getString(R.string.best_score_level_gameType), currentBestScoreForDimensionAndGameType);
        startActivity(intent);
        finish();
    }

    private int insertGameWinStateObjectIntoGameWinStateTable(GameWinState gameWinState) {
        return gameWinStateDBHandler.insertGameWinStateObjectToDatabase(gameWinState);
    }

    private void removeCurrentGameFromTableOfCurrentGames(int dimension, int gameMode) {
        gameDataObjectDBHandler.deleteMostRecentGameDataObjectForDimensionAndGameMode(dimension, gameMode);
    }

    private GameWinState getGameWinStateFromGameInstance(GameInstance gameInstance) {
        //ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, ORIGINAL_BULB_CONFIGURATION, NUMBER_OF_MOVES,
        //NUMBER_OF_HINTS_USED, NUMBER_OF_STARS, GAME_MODE, TIME_STAMP_MS
        GameWinState gameWinState = new GameWinState(){{
            setDimension(gameInstance.getDimension());
            setOriginalStartState(GameDataUtil.byteArrayToString(gameInstance.getOriginalStartState()));
            setToggledBulbs(GameDataUtil.byteArrayToString(gameInstance.getCurrentToggledBulbs()));
            setOriginalBulbConfiguration(GameDataUtil.byteArrayToString(gameInstance.getOriginalIndividualBulbStatus()));
            setMoveCounterPerBulbString(GameDataUtil.integerArrayToString(gameInstance.getMoveCounterPerBulb()));
            setOriginalBoardPower(gameInstance.getOriginalBoardPower());
            setNumberOfMoves(gameInstance.getMoveCounter());
            setNumberOfHintsUsed(gameInstance.getHintsUsed());
            setNumberOfStars(gameInstance.getStarCount());
            setGameMode(gameInstance.getGameMode());
            setTimeStampMs(System.currentTimeMillis());
        }};
        return gameWinState;
    }

    private GameDataObject getDefaultGameDataObject(int dimension, int gameMode) {
        // ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, UNDO_STACK_STRING,
        // REDO_STACK_STRING, GAME_MODE, HAS_SEEN_SOLUTION, MOVE_COUNTER, NUMBER_OF_HINTS_USED,
        // MOVE_COUNTER_PER_BULB_STRING, ORIGINAL_INDIVIDUAL_BULB_STATUS

        GameDataObject gameDataObject = new GameDataObject() {{
            setDimension(dimension);
            setOriginalStartState(getOriginalStartStateString(dimension));
            setToggledBulbsState(getOriginalStartState());
            setUndoStackString(GameDataUtil.EMPTY_STRING);
            setRedoStackString(GameDataUtil.EMPTY_STRING);
            setMoveCounterPerBulbString(getDefaultMoveCounterPerBulbArrayString(dimension));
            setOriginalIndividualBulbStatus(GameDataUtil.EMPTY_STRING);
            setGameMode(gameMode);
            setHasSeenSolution(false);
            setMoveCounter(0);
            setNumberOfHintsUsed(0);
        }};

        return gameDataObject;
    }

    private String getDefaultMoveCounterPerBulbArrayString(int dimension) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dimension*dimension; i++) {
            builder.append("0,");
        }
        builder.deleteCharAt(builder.lastIndexOf(","));

        return builder.toString();
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

        // To accommodate accidentally generating all off state.
        if(dimension <= 4) {
            originalStartState.deleteCharAt(originalStartState.length() - 1);
            Random random = new Random();
            if (!originalStartState.toString().contains("1")) {
                int odd = random.nextInt((dimension*2-1) / 2) * 2 + 1;
                originalStartState.replace(odd-1, odd, "1");
            }
        }

        return originalStartState.toString();
    }


    private void createUndoButton() {
        LinearLayout undo = findViewById(R.id.undo_button_holder);
        undo.setOnClickListener(v -> handleUndoClick());
    }

    private void handleUndoClick() {
        gameInstance.removeFromUndoStack();
    }

    private void createRedoButton() {
        LinearLayout redo = findViewById(R.id.redo_button_holder);
        redo.setOnClickListener(v -> handleRedoClick());
    }

    public void handleRedoClick() {
        gameInstance.removeFromRedoStack();
    }

    private void createHintButton() {
        LinearLayout hint = findViewById(R.id.hint_button_holder);
        hint.setOnClickListener(v -> handleHintClick());
    }

    private void handleHintClick() {
        boolean hintSuccess  = gameInstance.showHint();
        if(!hintSuccess) {
            Context context;
            Toast toast = new Toast(this);
            toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
            toast.makeText(this, R.string.no_hint_needed,
                    Toast.LENGTH_SHORT).show(); LayoutInflater myInflater = LayoutInflater.from(this);
        }
    }

    private void createShowSolutionButton() {
        LinearLayout showSolution = findViewById(R.id.solution_button_holder);
        showSolution.setBackgroundColor(getResources().getColor(R.color.transparent));
        showSolution.setOnClickListener(v -> {
            if (!gameInstance.getHasSeenSolution()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameGridActivity.this);
                builder.setTitle(getString(R.string.show_solution_title))
                        .setMessage(R.string.show_soltuion_confirmation)
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> handleShowSolution())
                        .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
                //Creating dialog box
                AlertDialog dialog  = builder.create();
                dialog.show();
            } else {
                handleShowSolution();
            }
        });
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
        LinearLayout reset = findViewById(R.id.reset_button_holder);
        reset.setOnClickListener(v -> handleResetClick());
    }

    private void handleResetClick() {
        // currently move counter is 0 but this will need to be changed to moveCounter in gameData
        gameInstance.resetBoardToState(
                GameDataUtil.stringToByteArray(gameDataObject.getToggledBulbsState()),
                GameDataUtil.stringToByteArray(gameDataObject.getOriginalIndividualBulbStatus()),
                GameDataUtil.stringToIntegerStack(gameDataObject.getUndoStackString()),
                GameDataUtil.stringToIntegerStack(gameDataObject.getRedoStackString()),
                GameDataUtil.stringToIntegerArray(gameDataObject.getMoveCounterPerBulbString()),
                gameDataObject.getMoveCounter(),
                // to prevent user from not resetting hints used
                gameInstance.getHintsUsed()
        );
        Log.d(TAG, "Resetting board to Last Saved Instance or Default state if there was no saved instance");
    }


    private void createNewGameButton() {
        LinearLayout randomize = findViewById(R.id.new_game_button_holder);
        randomize.setOnClickListener(v -> handleRandomizeClick());
    }

    private void handleRandomizeClick() {
        this.undoShowSolutionIfNeeded();
        gameDataObject = getDefaultGameDataObject(dimension, GameMode.ARCADE);
        gameInstance.setOriginalStartState(GameDataUtil.stringToByteArray(gameDataObject.getOriginalStartState()));
        gameInstance.setHasSeenSolution(gameDataObject.getHasSeenSolution());
        gameInstance.resetBoardToState(
                gameInstance.getOriginalStartState(),
                GameDataUtil.stringToByteArray(GameDataUtil.EMPTY_STRING),
                GameDataUtil.stringToIntegerStack(gameDataObject.getUndoStackString()),
                GameDataUtil.stringToIntegerStack(gameDataObject.getRedoStackString()),
                GameDataUtil.stringToIntegerArray(gameDataObject.getMoveCounterPerBulbString()),
                gameDataObject.getMoveCounter(),
                gameDataObject.getNumberOfHintsUsed()
        );

        Log.d(TAG, "Resetting Board to new Randomized State");
    }

    private void undoShowSolutionIfNeeded() {
        if (gameInstance.getIsShowingSolution()) {
            this.handleShowSolution();
        }
    }

    private void returnToLevelSelector() {
        Intent intent = new Intent(GameGridActivity.this, SelectLevelActivity.class);
        intent.putExtra(getString(R.string.selected_game_mode), gameDataObject.getGameMode());
        startActivity(intent);
        finish();
    }

    private void saveCurrentGameInstance(GameInstance gameInstance) {
        GameDataObject gameDataObject = getGameDataObjectFromCurrentGameInstance(gameInstance);
        gameDataObjectDBHandler.addGameDataObjectToDatabase(gameDataObject);
    }

    private GameDataObject getGameDataObjectFromCurrentGameInstance(GameInstance gameInstance) {
        // ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, UNDO_STACK_STRING,
        // REDO_STACK_STRING, GAME_MODE, HAS_SEEN_SOLUTION, MOVE_COUNTER, NUMBER_OF_HINTS_USED,
        // MOVE_COUNTER_PER_BULB_STRING, ORIGINAL_INDIVIDUAL_BULB_STATUS
        GameDataObject gameDataObject = new GameDataObject(){{
            setDimension(gameInstance.getDimension());
            setOriginalStartState(GameDataUtil.byteArrayToString(gameInstance.getOriginalStartState()));
            setOriginalIndividualBulbStatus(GameDataUtil.byteArrayToString(gameInstance.getOriginalIndividualBulbStatus()));
            setToggledBulbsState(GameDataUtil.byteArrayToString(gameInstance.getCurrentToggledBulbs()));
            setUndoStackString(GameDataUtil.integerStackToString(gameInstance.getUndoStack()));
            setRedoStackString(GameDataUtil.integerStackToString(gameInstance.getRedoStack()));
            setMoveCounterPerBulbString(GameDataUtil.integerArrayToString(gameInstance.getMoveCounterPerBulb()));
            setGameMode(gameInstance.getGameMode());
            setHasSeenSolution(gameInstance.getHasSeenSolution());
            setMoveCounter(gameInstance.getMoveCounter());
            setNumberOfHintsUsed(gameInstance.getHintsUsed());
        }};

        return gameDataObject;
    }

}