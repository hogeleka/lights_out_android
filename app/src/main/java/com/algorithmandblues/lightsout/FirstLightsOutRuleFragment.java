package com.algorithmandblues.lightsout;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;
import java.util.Objects;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class FirstLightsOutRuleFragment extends Fragment {


    private static final int RULE_PADDING_TOP = 10;
    private static final int RULE_PADDING_BOTTOM = 20;
    private static final int RULE_PADDING_SIDE = 100;
    private static final int RULE_FONT_SIZE = 20;

    private GifImageView neighborToggleGif;
    private GifImageView turnOffAllTheLightsGif;

    public FirstLightsOutRuleFragment() {
        // Required empty public constructor
    }

    public static FirstLightsOutRuleFragment newInstance() {
        return new FirstLightsOutRuleFragment();
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

        TextView turnOffAllTheLights = RulesActivityUtil.createRuleTextView(getContext(), R.string.switch_off_all_the_bulbs_rule, RULE_FONT_SIZE, RULE_PADDING_TOP, RULE_PADDING_BOTTOM, RULE_PADDING_SIDE);
        TextView neighbourToggleRule = RulesActivityUtil.createRuleTextView(getContext(), R.string.neighbor_toggle_rule, RULE_FONT_SIZE, RULE_PADDING_TOP, RULE_PADDING_BOTTOM, RULE_PADDING_SIDE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) Objects.requireNonNull(getContext()).getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int gifSize = (int) (displayMetrics.widthPixels * 0.4);
        try {
            GifDrawable turnOffAllTheLightsDrawable = new GifDrawable(getResources(), R.drawable.turn_off_all_the_lights);
            GifDrawable neighborToggleDrawable = new GifDrawable(getResources(), R.drawable.bulb_neighbor_rule);
            turnOffAllTheLightsGif = RulesActivityUtil.createGif(getContext(), gifSize, turnOffAllTheLightsDrawable, RULE_PADDING_BOTTOM);
            neighborToggleGif = RulesActivityUtil.createGif(getContext(), gifSize, neighborToggleDrawable, RULE_PADDING_BOTTOM);
        } catch (IOException e) {
            e.printStackTrace();
        }

        firstRuleHolder.addView(turnOffAllTheLightsGif);
        firstRuleHolder.addView(turnOffAllTheLights);
        firstRuleHolder.addView(neighborToggleGif);
        firstRuleHolder.addView(neighbourToggleRule);
    }
}
