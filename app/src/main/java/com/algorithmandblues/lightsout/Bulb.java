package com.algorithmandblues.lightsout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
    private static final int BULB_CORNER_RADIUS = 15;
    private static final int BORDER_WIDTH = 3;

    private int hintGradientStartColor;
    private int hintGradientEndColor;

    private GradientDrawable bulbBackground;
    private GradientDrawable hintBackGround;
    private boolean isBorderHighlighted;
    private boolean isHintHighlighted;
    private boolean isHintBorderHighlighted;

    public Bulb (Context context, int bulbId) {
        super(context, null, R.style.AppTheme);
        this.bulbId = bulbId;
        this.isOn = true;
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
        ((GradientDrawable) this.getBackground()).setStroke(this.getBorderWidthInPixels(), getResources().getColor(SOLUTION_BORDER_COLOR));
        this.setBackground(this.getBackground());
        this.isBorderHighlighted = true;
    }

    public void unHighlightBorder() {
        if(this.getIsHintBorderHighlighted()) {
            ((GradientDrawable) this.getBackground()).setStroke(this.getBorderWidthInPixels(), getResources().getColor(HINT_BORDER_COLOR));

        } else {
            ((GradientDrawable) this.getBackground()).setStroke(0, getResources().getColor(R.color.transparent));
        }
        this.setBackground(this.getBackground());
        this.isBorderHighlighted = false;
    }

    public void highlightHint() {
        this.setBackground(this.getHintBackGround());
        this.setIsHintHighlighted(true);
        this.setIsHintBorderHighlighted(true);
        if (this.isBorderHighlighted()) {
            this.highlightBorder();
        }

    }

    public void unhighlightHint() {

        int pixels = this.getBorderWidthInPixels();
        this.getBulbBackground().setStroke(pixels, getResources().getColor(HINT_BORDER_COLOR));
        this.setBackground(this.getBulbBackground());
        this.setIsHintHighlighted(false);
        if (this.isBorderHighlighted()) {
            this.highlightBorder();
        }

    }

    private int getBorderWidthInPixels() {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (BORDER_WIDTH * scale + 0.5f);
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

    public boolean isBorderHighlighted() {
        return isBorderHighlighted;
    }

    public void setBorderHighlighted(boolean borderHighlighted) {
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
}

