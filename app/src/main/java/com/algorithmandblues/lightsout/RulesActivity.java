package com.algorithmandblues.lightsout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RulesActivity extends AppCompatActivity {

    private GifImageView neighborToggleGif;
    private GifImageView turnOffAllTheLightsGif;
    private GifImageView hintGif;
    private GifImageView solutionGif;

    private static final int RULE_PADDING_TOP = 10;
    private static final int RULE_PADDING_BOTTOM = 20;
    private static final int RULE_PADDING_SIDE = 100;
    private static final int RULE_FONT_SIZE = 20;
    private static final int RULE_TITLE_FONT_SIZE = 40;
    private static final int RULE_TITLE_PADDING_BOTTOM = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_rules);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        ScrollView pageContents = findViewById(R.id.rules_scroll_view);

        LinearLayout rulesLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rulesLayout.setLayoutParams(params);
        rulesLayout.setOrientation(LinearLayout.VERTICAL);
        rulesLayout.setGravity(Gravity.CENTER);

        TextView rulesTitle = createRuleTextView(R.string.rules_title, RULE_TITLE_FONT_SIZE, RULE_PADDING_TOP, RULE_TITLE_PADDING_BOTTOM, RULE_PADDING_SIDE);
        TextView turnOffAllTheLights = createRuleTextView(R.string.switch_off_all_the_bulbs_rule, RULE_FONT_SIZE, RULE_PADDING_TOP, RULE_PADDING_BOTTOM, RULE_PADDING_SIDE);
        TextView neighbourToggleRule = createRuleTextView(R.string.neighbor_toggle_rule, RULE_FONT_SIZE, RULE_PADDING_TOP, RULE_PADDING_BOTTOM, RULE_PADDING_SIDE);
        TextView hintRule = createRuleTextView(R.string.hint_rule, RULE_FONT_SIZE, RULE_PADDING_TOP, RULE_PADDING_BOTTOM, RULE_PADDING_SIDE);
        TextView solutionRule = createRuleTextView(R.string.solution_rule, RULE_FONT_SIZE, RULE_PADDING_TOP, RULE_PADDING_BOTTOM, RULE_PADDING_SIDE);

        int gifSize = (int) (displayMetrics.widthPixels * 0.4);
        try {
            GifDrawable turnOffAllTheLightsDrawable = new GifDrawable(getResources(), R.drawable.turn_off_all_the_lights);
            GifDrawable neighborToggleDrawable = new GifDrawable(getResources(), R.drawable.bulb_neighbor_rule);
            GifDrawable hintRuleDrawable = new GifDrawable(getResources(), R.drawable.hint_gif);
            GifDrawable solutionDrawable = new GifDrawable(getResources(), R.drawable.solution_gif);
            turnOffAllTheLightsGif = createGif(gifSize, turnOffAllTheLightsDrawable);
            neighborToggleGif = createGif(gifSize, neighborToggleDrawable);
            hintGif = createGif(gifSize, hintRuleDrawable);
            solutionGif = createGif(gifSize, solutionDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        rulesLayout.addView(rulesTitle);
        rulesLayout.addView(turnOffAllTheLightsGif);
        rulesLayout.addView(turnOffAllTheLights);
        rulesLayout.addView(neighborToggleGif);
        rulesLayout.addView(neighbourToggleRule);
        rulesLayout.addView(hintGif);
        rulesLayout.addView(hintRule);
        rulesLayout.addView(solutionGif);
        rulesLayout.addView(solutionRule);

        pageContents.addView(rulesLayout);
    }

    private TextView createRuleTextView(int id, int fontSize, int paddingTop, int paddingBottom, int paddingSide) {
        TextView textView = new TextView(this);
        textView.setText(getResources().getString(id));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        textView.setPadding(getPixels(paddingSide), getPixels(paddingTop), getPixels(paddingSide), getPixels(paddingBottom));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return textView;
    }

    private GifImageView createGif(int gifSize, GifDrawable drawable) {
        return new GifImageView(this) {{
            setAdjustViewBounds(true);
            setMaxWidth(gifSize);
            setMaxHeight(gifSize);
            setImageDrawable(drawable);
            setPadding(0, getPixels(RULE_PADDING_BOTTOM), 0, 0);
        }};
    }

    public int getPixels(int value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
