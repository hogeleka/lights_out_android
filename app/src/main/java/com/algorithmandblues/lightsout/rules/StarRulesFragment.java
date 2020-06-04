package com.algorithmandblues.lightsout.rules;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.algorithmandblues.lightsout.utils.ActivityDrawingUtils;
import com.algorithmandblues.lightsout.utils.BounceInterpolator;
import com.algorithmandblues.lightsout.R;

public class StarRulesFragment extends Fragment {


    private static final int STAR_IMAGE_SIZE_PX = 40;
    private static final int ROW_OF_STARS_LEFT_RIGHT_PADDING = 10;
    private static final int ROW_OF_STARS_TOP_BOTTOM_PADDING = 5;
    private static final int RULE_PADDING_TOP = 5;
    private static final int RULE_PADDING_BOTTOM = 20;
    private static final int RULE_PADDING_SIDE = 60;
    private static final int RULE_FONT_SIZE = 20;
    private static final int ROW_OF_STARS_ANIMATION_TIME_OFFSET = 1000;


    // 750 is duration of each star animation. Defined in staranimation.xml
    private static final BounceInterpolator BOUNCE_INTERPOLATOR = new BounceInterpolator(0.2, 20);

    public StarRulesFragment() {
        // Required empty public constructor
    }

    public static StarRulesFragment newInstance() {
        return new StarRulesFragment();
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
        LinearLayout starRuleHolder = view.findViewById(R.id.RuleContainer);
        LinearLayout rowOf3Stars, rowOf2Stars, rowOf1Star, rowOf0Stars;
        LayoutAnimationController rowOfStarsAnimationController = createRowOfStarsAnimationController();
        rowOfStarsAnimationController.setDelay(1);

        TextView starRulePrefix = getRuleTextView(R.string.star_rule_prefix);
        starRuleHolder.addView(starRulePrefix);

        rowOf3Stars = getRowOfStars(3);
        rowOf3Stars.setLayoutAnimation(rowOfStarsAnimationController);
        TextView ruleFor3Stars = getRuleTextView(R.string.rule_for_3_stars);

        rowOf2Stars = getRowOfStars(2);
        rowOf2Stars.setLayoutAnimation(rowOfStarsAnimationController);
        TextView ruleFor2Stars = getRuleTextView(R.string.rule_for_2_stars);

        rowOf1Star = getRowOfStars(1);
        rowOf1Star.setLayoutAnimation(rowOfStarsAnimationController);
        TextView ruleFor1Star = getRuleTextView(R.string.rule_for_1_star);

        rowOf0Stars = getRowOfStars(0);
        rowOf0Stars.setLayoutAnimation(rowOfStarsAnimationController);
        TextView ruleFor0Stars = getRuleTextView(R.string.rule_for_0_stars);

        starRuleHolder.addView(rowOf3Stars);
        starRuleHolder.addView(ruleFor3Stars);
        starRuleHolder.addView(rowOf2Stars);
        starRuleHolder.addView(ruleFor2Stars);
        starRuleHolder.addView(rowOf1Star);
        starRuleHolder.addView(ruleFor1Star);
        starRuleHolder.addView(rowOf0Stars);
        starRuleHolder.addView(ruleFor0Stars);
        rowOfStarsAnimationController.start();
    }

    private TextView getRuleTextView(int text) {
        return ActivityDrawingUtils.createRuleTextView(getContext(), text, RULE_FONT_SIZE, RULE_PADDING_TOP, RULE_PADDING_BOTTOM, RULE_PADDING_SIDE);
    }

    private LinearLayout getRowOfStars(int numberOfStars) {
        return ActivityDrawingUtils.makeRowOfStars(getContext(), numberOfStars, STAR_IMAGE_SIZE_PX, ROW_OF_STARS_LEFT_RIGHT_PADDING, ROW_OF_STARS_TOP_BOTTOM_PADDING);
    }

    private LayoutAnimationController createRowOfStarsAnimationController() {
        Animation starAnimation  = AnimationUtils.loadAnimation(getContext(), R.anim.staranimation);
        starAnimation.setInterpolator(BOUNCE_INTERPOLATOR);
        starAnimation.setStartOffset(ROW_OF_STARS_ANIMATION_TIME_OFFSET);
        return new LayoutAnimationController(starAnimation) {{
            setOrder(LayoutAnimationController.ORDER_NORMAL);
        }};
    }
}
