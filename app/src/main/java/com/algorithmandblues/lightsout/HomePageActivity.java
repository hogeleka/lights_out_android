package com.algorithmandblues.lightsout;

import android.app.AlertDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class HomePageActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_home_page);

        //set onclick listener for "play button")
        Button button = (Button) findViewById(R.id.goToLevelSelector);
        button.setOnClickListener(v -> goToLevelSelector());

        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
//        databaseHelper.resetDatabase();
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
}