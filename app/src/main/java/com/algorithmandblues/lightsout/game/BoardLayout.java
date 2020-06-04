package com.algorithmandblues.lightsout.game;

import android.content.Context;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class BoardLayout extends GridLayout {
    public BoardLayout(Context context) {
        super(context);
    }

    public Bulb getBulbAt(int id) {
       return (Bulb) ((RelativeLayout) (this.getChildAt(id))).getChildAt(0);
    }

    public ImageView getHintIconAt(int id) {
        return (ImageView) ((RelativeLayout) (this.getChildAt(id))).getChildAt(1);
    }
}
