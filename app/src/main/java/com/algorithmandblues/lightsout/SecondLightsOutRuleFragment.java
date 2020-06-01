package com.algorithmandblues.lightsout;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.Objects;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class SecondLightsOutRuleFragment extends Fragment {


    private static final int RULE_PADDING_TOP = 10;
    private static final int RULE_PADDING_BOTTOM = 20;
    private static final int RULE_PADDING_SIDE = 100;
    private static final int RULE_FONT_SIZE = 20;

    private GifImageView hintGif;
    private GifImageView solutionGif;

    public SecondLightsOutRuleFragment() {
        // Required empty public constructor
    }

    public static SecondLightsOutRuleFragment newInstance() {
        return new SecondLightsOutRuleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first_lights_out_rule, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LinearLayout firstRuleHolder = view.findViewById(R.id.firstRuleContainer);

        TextView hintRule = RulesActivityUtil.createRuleTextView(getContext(), R.string.hint_rule, RULE_FONT_SIZE, RULE_PADDING_TOP, RULE_PADDING_BOTTOM, RULE_PADDING_SIDE);
        TextView solutionRule = RulesActivityUtil.createRuleTextView(getContext(), R.string.solution_rule, RULE_FONT_SIZE, RULE_PADDING_TOP, RULE_PADDING_BOTTOM, RULE_PADDING_SIDE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) Objects.requireNonNull(getContext()).getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int gifSize = (int) (displayMetrics.widthPixels * 0.4);
        try {
            GifDrawable hintDrawable = new GifDrawable(getResources(), R.drawable.hint_gif);
            GifDrawable solutionDrawable = new GifDrawable(getResources(), R.drawable.solution_gif);
            hintGif = RulesActivityUtil.createGif(getContext(), gifSize, hintDrawable, RULE_PADDING_BOTTOM);
            solutionGif = RulesActivityUtil.createGif(getContext(), gifSize, solutionDrawable, RULE_PADDING_BOTTOM);
        } catch (IOException e) {
            e.printStackTrace();
        }

        firstRuleHolder.addView(hintGif);
        firstRuleHolder.addView(hintRule);
        firstRuleHolder.addView(solutionGif);
        firstRuleHolder.addView(solutionRule);
    }
}