package com.algorithmandblues.lightsout;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class RulesActivityUtil {

    static TextView createRuleTextView(Context context, int id, int fontSize, int paddingTop, int paddingBottom, int paddingSide) {
        TextView textView = new TextView(context);
        textView.setText(context.getResources().getString(id));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        textView.setPadding(getPixels(context, paddingSide), getPixels(context, paddingTop), getPixels(context, paddingSide), getPixels(context, paddingBottom));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return textView;
    }

    static GifImageView createGif(Context context, int gifSize, int id, int paddingTop, int paddingBottom) {
        return new GifImageView(context) {{
            setAdjustViewBounds(true);
            setMaxWidth(gifSize);
            setMaxHeight(gifSize);
            setImageDrawable(createGifDrawable(context, id));
            setPadding(0, getPixels(context, paddingTop), 0, paddingBottom);
        }};
    }

    static GifDrawable createGifDrawable(Context context, int id) {
        try {
            return new GifDrawable(context.getResources(), id);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int getPixels(Context context, int value) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }
}
