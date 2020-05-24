package com.algorithmandblues.lightsout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LevelSelectorActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;

    private CheckBox mCheckBox;

    GameDataObjectDao gameDataObjectDao;

    private LinearLayout boardSizesContainer;

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
    private LinearLayout mContentView;
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
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_level_selector);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(view -> toggle());
        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        gameDataObjectDao = GameDataObjectDao.getInstance(databaseHelper);
        mCheckBox = (CheckBox) findViewById(R.id.should_randomize_checkbox);
        mCheckBox.setChecked(true);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        boardSizesContainer = findViewById(R.id.board_sizes_container);

        prepareLevelSelectors();
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
    public void onBackPressed() {
        Intent intent = new Intent(LevelSelectorActivity.this, FullscreenActivity.class);
        startActivity(intent);
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


    public void prepareLevelSelectors() {
        int dim = 2;
        int numRows = boardSizesContainer.getChildCount();
        int numCols = ((LinearLayout) boardSizesContainer.getChildAt(0)).getChildCount();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int dimension = dim;
                String label = String.format(getString(R.string.level_chooser_button_label), dimension, dimension);
                ( (Button) ( (LinearLayout) boardSizesContainer.getChildAt(row) ).getChildAt(col) ).setText(label);
                ( (Button) ( (LinearLayout) boardSizesContainer.getChildAt(row) ).getChildAt(col) ).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectLevelLabel(dimension);
                    }
                });
                dim++;
            }
        }
    }

    public void selectLevelLabel(int dimension) {
        Log.d("Selected Level: ", Integer.toString(dimension));
        boolean setRandomStateFlag = ((CheckBox) findViewById(R.id.should_randomize_checkbox)).isChecked();
        if (!checkForExistingGame(dimension, GameMode.ARCADE)) {
            Log.d("Already Existing Game", "No existing game in DB");
            goToNewGameActivity(dimension, false, setRandomStateFlag);
        } else {
            buildDialogToRequestUserResponse(dimension, setRandomStateFlag);
        }
    }

    public void buildDialogToRequestUserResponse(int dimension, boolean setRandomStateFlag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LevelSelectorActivity.this);
        builder.setTitle(getString(R.string.level_picker_resume_or_restart_title))
                .setMessage(String.format(getString(R.string.level_picker_resume_or_restart_message_prompt), dimension, dimension))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToNewGameActivity(dimension, true, setRandomStateFlag);
                    }
                })
                .setNegativeButton(getString(R.string.restart_new_game), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToNewGameActivity(dimension, false, setRandomStateFlag);
                    }
                });
        //Creating dialog box
        AlertDialog dialog  = builder.create();
        dialog.show();
    }

    public void goToNewGameActivity(int dimension, boolean resumeGameFromDBFlag, boolean setRandomStateFlag) {
        Intent intent = new Intent(LevelSelectorActivity.this, GameGridActivity.class);
        intent.putExtra(getString(R.string.dimension), dimension);
        intent.putExtra(getString(R.string.resume_from_db_flag), resumeGameFromDBFlag);
        intent.putExtra(getString(R.string.set_random_state_flag), setRandomStateFlag);
        startActivity(intent);
        finish();
    }

    public boolean checkForExistingGame(int dimension, int gameMode) {
        return gameDataObjectDao.getMostRecentGameDataForGameType(dimension, gameMode) != null;
//        return dbHandler.getGameData(dimension) != null;
    }
}
