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


public class BasicRuleFragment extends Fragment {

    private static final int RULE_PADDING_TOP = 10;
    private static final int RULE_PADDING_BOTTOM = 20;
    private static final int RULE_PADDING_SIDE = 100;
    private static final int RULE_FONT_SIZE = 20;
    private static final int GIF_PADDING_TOP = 10;
    private static final int GIF_PADDING_BOTTOM = 0;

    private GifImageView neighborToggleGif;
    private GifImageView turnOffAllTheLightsGif;

    public BasicRuleFragment() {
        // Required empty public constructor
    }

    public static BasicRuleFragment newInstance() {
        return new BasicRuleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lights_out_rule, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LinearLayout basicRulesHolder = view.findViewById(R.id.RuleContainer);

        TextView turnOffAllTheLights = getRuleTextView(R.string.switch_off_all_the_bulbs_rule);
        TextView neighbourToggleRule = getRuleTextView(R.string.neighbor_toggle_rule);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) Objects.requireNonNull(getContext()).getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int gifSize = (int) (displayMetrics.widthPixels * 0.4);

        turnOffAllTheLightsGif = RulesActivityUtil.createGif(getContext(), gifSize,  R.drawable.turn_off_all_the_lights, GIF_PADDING_TOP, GIF_PADDING_BOTTOM);
        neighborToggleGif = RulesActivityUtil.createGif(getContext(), gifSize, R.drawable.bulb_neighbor_rule, GIF_PADDING_TOP, GIF_PADDING_BOTTOM);

        basicRulesHolder.addView(turnOffAllTheLightsGif);
        basicRulesHolder.addView(turnOffAllTheLights);
        basicRulesHolder.addView(neighborToggleGif);
        basicRulesHolder.addView(neighbourToggleRule);
    }

    private TextView getRuleTextView(int text) {
        return RulesActivityUtil.createRuleTextView(getContext(), text, RULE_FONT_SIZE, RULE_PADDING_TOP, RULE_PADDING_BOTTOM, RULE_PADDING_SIDE);
    }
}
