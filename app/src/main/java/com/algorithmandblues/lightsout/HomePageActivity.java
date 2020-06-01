package com.algorithmandblues.lightsout;

import android.app.AlertDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import java.util.Arrays;

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
        databaseHelper.resetDatabase();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }


    public void goToLevelSelector() {
        Intent intent = new Intent(HomePageActivity.this, SelectLevelActivity.class);
        startActivity(intent);
        finish();
    }

}