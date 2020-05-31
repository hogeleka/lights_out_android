package com.algorithmandblues.lightsout;

import android.app.AlertDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import java.util.Arrays;

public class HomePageActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    GameWinStateDBHandler gameWinStateDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_home_page);

        //set onclick listener for "play button")
        Button button = (Button) findViewById(R.id.goToLevelSelector);
        button.setOnClickListener(v -> goToLevelSelector());

        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        gameWinStateDBHandler = GameWinStateDBHandler.getInstance(databaseHelper);
        databaseHelper.resetDatabase();
        spamDiviseWithGames();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ab = new AlertDialog.Builder(HomePageActivity.this, R.style.AlertDialogStyle);
        ab.setTitle(getResources().getString(R.string.exit_game_title));
        ab.setMessage(getResources().getString(R.string.exit_game_question));
        ab.setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
            super.onBackPressed();
        });
        ab.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss());
        ab.show();
    }


    public void goToLevelSelector() {
        Intent intent = new Intent(HomePageActivity.this, SelectLevelActivity.class);
        startActivity(intent);
        finish();
    }

    private void spamDiviseWithGames() {
        for (int i = 2; i <= 10; i++) {
            for (int j = 0; j < 150; j++) {
                gameWinStateDBHandler.insertGameWinStateObjectToDatabase(generateRandomGameWinStateForDimension(i));
            }
        }
    }

    private GameWinState generateRandomGameWinStateForDimension(int dimension) {
        byte[] randomByteArray = new byte[dimension * dimension];
        int[] randomIntArray = new int[dimension * dimension];
        Arrays.fill(randomByteArray, (byte)1);
        Arrays.fill(randomIntArray, 1);
        String testString = GameDataUtil.byteArrayToString(randomByteArray);
        return new GameWinState(){{
            setDimension(dimension);
            setOriginalStartState(testString);
            setToggledBulbs(testString);
            setToggledBulbs(testString);
            setOriginalBulbConfiguration(testString);
            setNumberOfMoves(dimension * dimension);
            setNumberOfHintsUsed(20);
            setNumberOfStars(3);
            setGameMode(0);
            setTimeStampMs(System.currentTimeMillis());
            setMoveCounterPerBulbString(GameDataUtil.integerArrayToString(randomIntArray));
            setOriginalBoardPower(dimension * dimension);
        }};
    }
}