package com.algorithmandblues.lightsout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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
    private static final int BORDER_WIDTH = 1;

    private int hintGradientStartColor;
    private int hintGradientEndColor;

    private GradientDrawable bulbBackground;
    private GradientDrawable hintBackground;
    private Animation bounceAnimation;
    private boolean isBorderHighlighted;
    private boolean isHintHighlighted;

    // Use bounce interpolator with amplitude 0.05 and frequency 15
    private static final BounceInterpolator BOUNCE_INTERPOLATOR = new BounceInterpolator(0.05, 15);

    public Bulb (Context context, int bulbId) {
        super(context, null, R.style.AppTheme);
        this.bulbId = bulbId;
        this.isOn = true;
        this.isBorderHighlighted = false;
        this.isHintHighlighted = false;

        // GradientDrawable takes in colors in 0xffffff format.
        this.hintGradientStartColor = ContextCompat.getColor(context, HINT_START_COLOR);
        this.hintGradientEndColor = ContextCompat.getColor(context, HINT_END_COLOR);

        bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.bounce);
        bounceAnimation.setInterpolator(BOUNCE_INTERPOLATOR);

        this.createNewBulbBackground();
        this.setBackground(bulbBackground);
        this.bulbBackground.setColor(this.getOnColor());
        this.createNewHintBackground();

    }

    private void createNewHintBackground() {
        this.hintBackground = new GradientDrawable();
        this.hintBackground.setShape(GradientDrawable.RECTANGLE);
        this.hintBackground.setColors(this.getHintHighlightColors());
        this.hintBackground.setOrientation(Orientation.TL_BR);
        this.hintBackground.setCornerRadius(BULB_CORNER_RADIUS);
    }
    private void createNewBulbBackground() {
        this.bulbBackground = new GradientDrawable();
        this.bulbBackground.setShape(GradientDrawable.RECTANGLE);
        this.bulbBackground.setCornerRadius(BULB_CORNER_RADIUS);
    }

    public void toggle() {

        // Set update the default background object with the correct on or off color.
        if (this.isOn()) {
            this.bulbBackground.setColor(this.getOffColor());
        } else {
            this.bulbBackground.setColor(this.getOnColor());
        }

        // Keep the hint highlighted if it is already highlighted.
        // The logic for un-highlighting the bulb if needed is handled in click bulb.
        if(this.getIsHintHighlighted()) {
            this.highlightHint();

            // Keep the Border highlighted if the solution is showing.
            // The logic for removing the border is also handled in click bulb.
            if(this.getIsBorderHighlighted()) {
                this.highlightBorder();
            }
        } else {
            // If it is not highlighted then set the background to be the default bulb background.
            this.setBackground(this.getBulbBackground());
        }


        this.isOn = !this.isOn;
    }

    public void setOn(boolean isOn) {
        this.isOn = isOn;
        if (isOn) {
            this.bulbBackground.setColor(this.getOnColor());
        } else {
            this.bulbBackground.setColor(this.getOffColor());
        }
        this.setBackground(this.getBulbBackground());
    }

    public void highlightBorder() {
        this.setIsBorderHighlighted(true);
        this.setBulbStroke(getPixels(BORDER_WIDTH), SOLUTION_BORDER_COLOR);
    }

    public void unHighlightBorder() {
        this.setIsBorderHighlighted(false);
        this.setBulbStroke(getPixels(BORDER_WIDTH), TRANSPARENT_COLOR);
    }

    public void highlightHint() {
        this.setBackground(this.getHintBackground());
        this.setIsHintHighlighted(true);
    }

    public void unhighlightHint() {
        this.setBackground(this.getBulbBackground());
        this.setIsHintHighlighted(false);
    }

    void startBounceAnimation() {
        this.startAnimation(this.getBounceAnimation());
    }

    private int getPixels(int value) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    private void setBulbStroke(int size, int color) {
        ((GradientDrawable) this.getBackground()).setStroke(this.getPixels(size), this.getResourcesColor(color));
    }

    private int[] getHintHighlightColors() {
        return new int[] {this.hintGradientStartColor, this.hintGradientEndColor};
    }


    private int getResourcesColor(int color) {
        return getResources().getColor(color);
    }

    private int getOnColor() {
        return this.getResourcesColor(ON_COLOR);
    }

    private int getOffColor() {
        return this.getResourcesColor(OFF_COLOR);
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

    public GradientDrawable getHintBackground() {
        return this.hintBackground;
    }

    public void setHintBackground(GradientDrawable hintBackground) {
        this.hintBackground = hintBackground;
    }

    public boolean getIsHintHighlighted() {
        return this.isHintHighlighted;
    }

    public void setIsHintHighlighted(boolean hintHighlighted) {
        this.isHintHighlighted = hintHighlighted;
    }

    public Animation getBounceAnimation() {
        return this.bounceAnimation;
    }

    public void setBounceAnimation(Animation bounceAnimation) {
        this.bounceAnimation = bounceAnimation;
    }
}

