package com.algorithmandblues.lightsout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class GameSummaryActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    GameDataObjectDao gameDataObjectDao;
    GameWinStateDao gameWinStateDao;
    GameWinState gameWinState;

    private static final String TAG = GameSummaryActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_summary);
        
        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        gameDataObjectDao = GameDataObjectDao.getInstance(databaseHelper);
        gameWinStateDao = GameWinStateDao.getInstance(databaseHelper);
        gameWinState = fetchGameWinStateFromLastCompletedGameGridActivity();
        
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GameSummaryActivity.this, LevelSelectorActivity.class);
        startActivity(intent);
        finish();
    }

    private GameWinState fetchGameWinStateFromLastCompletedGameGridActivity() {
        Intent intent = getIntent();
        GameWinState gameWinState = intent.getParcelableExtra(getString(R.string.game_win_state_label));
        Log.d(TAG, "Recieved game win state from previous activity: " + gameWinState.toString());
        gameWinStateDao.fetchAllGameWinStatesInReverseChronological(gameWinState.getGameMode());
        return gameWinState;
    }
}
