package com.algorithmandblues.lightsout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    GameDataObjectDBHandler gameDataObjectDBHandler;
    LevelDBHandler levelDBHandler;

    private LinearLayout boardSizesContainer;

    private static final String TAG = LevelSelectorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_level_selector);

        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        gameDataObjectDBHandler = GameDataObjectDBHandler.getInstance(databaseHelper);
        levelDBHandler = LevelDBHandler.getInstance(databaseHelper);

        levelDBHandler.fetchLevelsForGameMode(GameMode.ARCADE);
        levelDBHandler.fetchLevelsForGameMode(GameMode.CLASSIC);

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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LevelSelectorActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
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
        int gameMode = setRandomStateFlag ? GameMode.ARCADE : GameMode.CLASSIC;
        if (!checkForExistingGame(dimension, gameMode)) {
            Log.d(TAG, "No existing game in DB");
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
        return gameDataObjectDBHandler.getMostRecentGameDataForGameType(dimension, gameMode) != null;
    }
}
