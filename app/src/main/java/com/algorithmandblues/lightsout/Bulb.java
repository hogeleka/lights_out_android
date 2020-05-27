package com.algorithmandblues.lightsout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ViewConstructor")
public class Bulb extends AppCompatButton{

    private int bulbId;
    private boolean isOn;
    private static final int ON_COLOR = R.color.bulb_on_color;
    private static final int OFF_COLOR = R.color.bulb_off_color;
    private static final int HINT_START_COLOR = R.color.hint_start_color;
    private static final int HINT_END_COLOR = R.color.hint_end_color;
    private static final int SOLUTION_BORDER_COLOR = R.color.colorAccent;
    private static final int TRANSPARENT_COLOR = R.color.transparent;
    private static final int BULB_CORNER_RADIUS = 15;
    private static final int BORDER_WIDTH = 3;
    private static final int NO_BORDER_WIDTH = 0;

    private int hintGradientStartColor;
    private int hintGradientEndColor;

    private GradientDrawable bulbBackground;
    private GradientDrawable hintBackGround;
    private Animation bounce;
    private boolean isBorderHighlighted;
    private boolean isHint;
    private boolean isHintUsed;
    private boolean isHintHighlighted;
    private boolean isHintEmojiAdded;

    private static final Map<Integer, Float> HINT_ICON_SCALE_FACTOR_MAP = new HashMap<Integer, Float>() {{
        put(2, (float) 0.70);
        put(3, (float) 0.70);
        put(4, (float) 0.70);
        put(5, (float) 0.70);
        put(6, (float) 0.85);
        put(7, (float) 0.85);
        put(8, (float) 0.85);
        put(9, (float) 0.85);
        put(10,(float) 0.85);
    }};


    public Bulb (Context context, int bulbId) {
        super(context, null, R.style.AppTheme);
        this.bulbId = bulbId;
        this.isOn = true;
        this.isHint = false;
        this.isBorderHighlighted = false;
        this.isHintHighlighted = false;
        this.isBorderHighlighted = false;

        hintGradientStartColor = ContextCompat.getColor(context, HINT_START_COLOR);
        hintGradientEndColor = ContextCompat.getColor(context, HINT_END_COLOR);

        bounce = AnimationUtils.loadAnimation(context, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceInterpolator interpolator = new BounceInterpolator(0.15, 15);
        bounce.setInterpolator(interpolator);

        this.createBulbBackground();
        this.createHintBackGround();
        this.setBackground(bulbBackground);
    }

    public void createHintBackGround() {
        this.hintBackGround = new GradientDrawable();
        this.hintBackGround.setShape(GradientDrawable.RECTANGLE);
        this.hintBackGround.setOrientation(Orientation.TL_BR);

        this.hintBackGround.setColors(new int[] {hintGradientStartColor, hintGradientEndColor});
        this.hintBackGround.setCornerRadius(BULB_CORNER_RADIUS);
    }

    private void createBulbBackground() {
        this.bulbBackground = new GradientDrawable();
        this.bulbBackground.setShape(GradientDrawable.RECTANGLE);
        this.bulbBackground.setCornerRadius(BULB_CORNER_RADIUS);
        this.bulbBackground.setColor(getResources().getColor(ON_COLOR));
    }

    public void toggle() {
        this.bulbBackground.setColor(this.isOn ? getResources().getColor(OFF_COLOR) : getResources().getColor(ON_COLOR));
        if(this.getIsHintHighlighted()) {
            this.setBackground(this.getHintBackGround());
        } else {
            this.setBackground(this.getBulbBackground());
        }
        this.isOn = !this.isOn;
    }

    public void setOn(boolean isOn) {
        this.isOn = isOn;
        if (isOn) {
            this.bulbBackground.setColor(getResources().getColor((ON_COLOR)));
        } else {
            this.bulbBackground.setColor(getResources().getColor(OFF_COLOR));
        }
        this.setBackground(bulbBackground);
    }

    public void highlightBorder() {
        this.setBulbStroke(BORDER_WIDTH, SOLUTION_BORDER_COLOR);
        this.setBackground(this.getBackground());
        this.isBorderHighlighted = true;
    }

    public void unHighlightBorder() {
        this.setIsBorderHighlighted(false);
        if(this.getIsHint() && this.getIsHintUsed()) {
            this.addHintEmoji();
            this.setIsHintEmojiAdded(true);
        } else {
            this.setIsHintEmojiAdded(false);
        }
        this.setBulbStroke(BORDER_WIDTH, TRANSPARENT_COLOR);
        this.setBackground(this.getBackground());
    }

    public void highlightHint() {
        this.setIsHint(true);
        this.setBackground(this.getHintBackGround());
        this.setIsHintHighlighted(true);
        if (this.getIsBorderHighlighted()) {
            this.highlightBorder();
        }
        this.startAnimation(this.bounce);
    }

    public void unhighlightHint() {
        this.setBackground(this.getBulbBackground());
        this.setIsHintUsed(true);
        this.addHintEmoji();
        this.setIsHintHighlighted(false);
        if (this.getIsBorderHighlighted()) {
            this.highlightBorder();
        }

    }

    private int getPixels(int value) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    void addHintEmoji() {
        if (this.getIsHintUsed()) {
            this.setText(this.getHintEmoji());
            this.setGravity(Gravity.TOP);
            this.setTextAlignment(TEXT_ALIGNMENT_TEXT_END);
        }
    }

    void removeHintEmoji() {
        this.setText(GameDataUtil.EMPTY_STRING);
    }

    private String getHintEmoji() {
        return "\uD83E\uDD2F";
    }

    private void setBulbStroke(int size, int color) {
        ((GradientDrawable) this.getBackground()).setStroke(this.getPixels(BORDER_WIDTH), getResources().getColor(color));
    }

    @Override
    public String toString() {
        return "Bulb{" +
                "bulbId=" + this.bulbId +
                ", isOn=" + this.isOn +
                ", isBorderHighlighted=" + this.isBorderHighlighted +
                ", isHintHighlighted=" + this.isHintHighlighted +
                '}';
    }

    public boolean isOn() {
        return this.isOn;
    }

    public byte isOnOrOff() {
        return this.isOn() ? (byte) 1 : (byte) 0;
    }

    public boolean getIsBorderHighlighted() {
        return isBorderHighlighted;
    }

    public void setIsBorderHighlighted(boolean borderHighlighted) {
        isBorderHighlighted = borderHighlighted;
    }

    public int getBulbId() {
        return this.bulbId;
    }

    public void setBulbId(int bulbId) {
        this.bulbId = bulbId;
    }

    public GradientDrawable getBulbBackground() {
        return this.bulbBackground;
    }

    public void setBulbBackground(GradientDrawable bulbBackground) {
        this.bulbBackground = bulbBackground;
    }

    public GradientDrawable getHintBackGround() {
        return this.hintBackGround;
    }

    public void setHintBackGround(GradientDrawable hintBackGround) {
        this.hintBackGround = hintBackGround;
    }

    public boolean getIsHintHighlighted() {
        return this.isHintHighlighted;
    }

    public void setIsHintHighlighted(boolean hintHighlighted) {
        this.isHintHighlighted = hintHighlighted;
    }

    public boolean getIsHintEmojiAdded() {
        return this.isHintEmojiAdded;
    }

    public void setIsHintEmojiAdded(boolean isHintEmojieAdded) {
        this.isHintEmojiAdded = isHintEmojieAdded;
    }

    public boolean getIsHint() {
        return this.isHint;
    }

    public void setIsHint(boolean hint) {
        this.isHint = hint;
    }

    public boolean getIsHintUsed() {
        return this.isHintUsed;
    }

    public void setIsHintUsed(boolean hintUsed) {
        this.isHintUsed = hintUsed;
    }
}

