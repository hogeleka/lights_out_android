package com.algorithmandblues.lightsout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatButton;

public class Bulb extends AppCompatButton{

    private int bulbId;
    private boolean isOn;
    private static final int ON_COLOR = R.color.BULB_ON_COLOR;
    private static final int OFF_COLOR = R.color.BULB_OFF_COLOR;
    private GradientDrawable background;
    private boolean isBorderHighlighted;

    public Bulb (Context context, int bulbId) {
        super(context, null, R.style.AppTheme);
        this.bulbId = bulbId;
        this.isOn = true;
        this.isBorderHighlighted = false;
        this.createBulbBackground();
        this.setBackground(background);
    }

    private void createBulbBackground() {
        this.background = new GradientDrawable();
        this.background.setShape(GradientDrawable.RECTANGLE);
        this.background.setCornerRadius(15);
        this.background.setColor(getResources().getColor(ON_COLOR));
    }

    public void toggle() {
        this.background.setColor(this.isOn ? getResources().getColor(OFF_COLOR) : getResources().getColor(ON_COLOR));
        this.setBackground(background);
        this.isOn = !this.isOn;
    }

    public void setOn(boolean isOn) {
        this.isOn = isOn;
        if (isOn) {
            this.background.setColor(getResources().getColor((ON_COLOR)));
        } else {
            this.background.setColor(getResources().getColor(OFF_COLOR));
        }
        this.setBackground(background);
    }

    public void highlightBorder() {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (3 * scale + 0.5f);

        this.background.setStroke(pixels, Color.RED);
        this.setBackground(background);
        this.isBorderHighlighted = true;
    }

    public void unHighlightBorder() {
        this.background.setStroke(0, Color.RED);
        this.setBackground(background);
        this.isBorderHighlighted = false;
    }

    @Override
    public String toString() {
        return "Bulb{" +
                "bulbId=" + bulbId +
                ", isOn=" + isOn +
                '}';
    }

    public boolean isOn() {
        return this.isOn;
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

}

