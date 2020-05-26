package com.algorithmandblues.lightsout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;

@SuppressLint("ViewConstructor")
public class Bulb extends AppCompatButton{

    private int bulbId;
    private boolean isOn;
    private static final int ON_COLOR = R.color.bulb_on_color;
    private static final int OFF_COLOR = R.color.bulb_off_color;
    private static final int HINT_START_COLOR = R.color.hint_start_color;
    private static final int HINT_END_COLOR = R.color.hint_end_color;
    private static final int HINT_BORDER_COLOR = R.color.hint_border_color;
    private static final int SOLUTION_BORDER_COLOR = R.color.colorAccent;
    private static final int TRANSPARENT_COLOR = R.color.transparent;
    private static final int BULB_CORNER_RADIUS = 15;
    private static final int BORDER_WIDTH = 3;

    private int hintGradientStartColor;
    private int hintGradientEndColor;

    private GradientDrawable bulbBackground;
    private GradientDrawable hintBackGround;
    private boolean isBorderHighlighted;
    private boolean isHint;
    private boolean isHintUsed;
    private boolean isHintHighlighted;
    private boolean isHintBorderHighlighted;

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
        this.setBulbStroke(SOLUTION_BORDER_COLOR);
        this.setBackground(this.getBackground());
        this.isBorderHighlighted = true;
    }

    public void unHighlightBorder() {
        this.setIsBorderHighlighted(false);
        if(this.getIsHint() && this.getIsHintUsed()) {
            this.setBulbStroke(HINT_BORDER_COLOR);
            this.setIsHintBorderHighlighted(true);
        } else {
            this.setBulbStroke(TRANSPARENT_COLOR);
            this.setIsHintBorderHighlighted(false);
        }
        this.setBackground(this.getBackground());
    }

    public void highlightHint() {
        this.setIsHint(true);
        this.setBackground(this.getHintBackGround());
        this.setIsHintHighlighted(true);
        if (this.getIsBorderHighlighted()) {
            this.highlightBorder();
        }
    }

    public void unhighlightHint() {
        this.setBackground(this.getBulbBackground());
        this.setBulbStroke(HINT_BORDER_COLOR);
        this.setIsHintUsed(true);
        this.setIsHintHighlighted(false);
        if (this.getIsBorderHighlighted()) {
            this.highlightBorder();
        }

    }

    void highlightHintBorder() {
        if(!this.getIsHintUsed()) {
            this.setBulbStroke(HINT_BORDER_COLOR);
            this.setIsHintBorderHighlighted(true);
        }
    }

    public void unhighlightHintBorder() {
        ((GradientDrawable) this.getBackground()).setStroke(0, getResources().getColor(TRANSPARENT_COLOR));
        this.setIsHintBorderHighlighted(false);
    }

    private int getPixels(int value) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    private void setBulbStroke(int color) {
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

    public boolean getIsHintBorderHighlighted() {
        return this.isHintBorderHighlighted;
    }

    public void setIsHintBorderHighlighted(boolean hintBorderHighlighted) {
        this.isHintBorderHighlighted = hintBorderHighlighted;
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

