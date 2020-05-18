package com.algorithmandblues.lightsout;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;

public class Bulb extends AppCompatButton{

    private int bulbId;
    private boolean isOn;
    private static final int ON_COLOR = R.color.BULB_ON_COLOR;
    private static final int OFF_COLOR = R.color.BULB_OFF_COLOR;


    public Bulb (Context context, int bulbId) {
        super(context);
        this.bulbId = bulbId;
        this.isOn = true;
        this.setBackgroundColor(getResources().getColor(ON_COLOR));

    }

    public int getBulbId() {
        return this.bulbId;
    }

    public void setBulbId(int bulbId) {
        this.bulbId = bulbId;
    }

    public void toggle() {
        this.setBackgroundColor(this.isOn ? getResources().getColor(OFF_COLOR) : getResources().getColor(ON_COLOR));
        this.isOn = !this.isOn;
    }

    public boolean isOn() {
        return this.isOn;
    }

    public void setOn(boolean isOn) {
        this.isOn = isOn;
        if (isOn) {
            this.setBackgroundColor(getResources().getColor((ON_COLOR)));
        } else {
            this.setBackgroundColor(getResources().getColor(OFF_COLOR));
        }
    }

    @Override
    public String toString() {
        return "Bulb{" +
                "bulbId=" + bulbId +
                ", isOn=" + isOn +
                '}';
    }


}

