package com.algorithmandblues.lightsout.home;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.algorithmandblues.lightsout.database.DatabaseHelper;
import com.algorithmandblues.lightsout.R;
import com.algorithmandblues.lightsout.levelselection.SelectLevelActivity;
import com.algorithmandblues.lightsout.utils.ActivityDrawingUtils;

import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class HomePageActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    private static final int GIF_PADDING_BOTTOM = 20;
    private static final int GIF_PADDING_TOP = 0;
    private static final int PLAY_FONT_SIZE = 24;
    private static final int PLAY_LAYOUT_PADDING_TOP = 120;
    private static final int PLAY_BUTTON_PADDING_VERTICAL = 30;
    private static final int PLAY_BUTTON_PADDING_HORIZONTAL = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) Objects.requireNonNull(this).getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int gifSize = (int) (displayMetrics.widthPixels * 0.4);


        LinearLayout homePageContentHolder = findViewById(R.id.play_and_home_gif_holder);
        TextView play = getButtonTextView(getResources().getString(R.string.play));
        homePageContentHolder.setOnClickListener(v -> goToLevelSelector());

        GifImageView homePageGif = ActivityDrawingUtils.createGif(this, gifSize, R.drawable.home_page_gif, GIF_PADDING_TOP, GIF_PADDING_BOTTOM);
        homePageContentHolder.addView(homePageGif);
        homePageContentHolder.addView(play);

//        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
//        databaseHelper.resetDatabase();
    }

    private TextView getButtonTextView(String text) {
        LinearLayout.LayoutParams playParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT );
        playParams.setMargins(0, PLAY_LAYOUT_PADDING_TOP, 0, 0);

        TextView textView = new TextView(this);
        textView.setLayoutParams(playParams);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, HomePageActivity.PLAY_FONT_SIZE);
        textView.setTextColor(getResources().getColor(R.color.custom_black));
        textView.setBackgroundColor(getResources().getColor(R.color.background_color));
        textView.setPadding(getPixels(PLAY_BUTTON_PADDING_VERTICAL), getPixels(PLAY_BUTTON_PADDING_HORIZONTAL), getPixels(PLAY_BUTTON_PADDING_VERTICAL), getPixels(PLAY_BUTTON_PADDING_HORIZONTAL));
        return textView;
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

    public int getPixels(int value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    public void rateUs(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + this.getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    public void contactUs(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","rohitandhosea@gmail.com", null));
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }

}