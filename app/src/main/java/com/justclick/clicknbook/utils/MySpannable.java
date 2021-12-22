package com.justclick.clicknbook.utils;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by priyanshi on 10/6/2017.
 */


public class MySpannable extends ClickableSpan {

    private boolean isUnderline = true;

    /**
     * Constructor
     */
    public MySpannable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(isUnderline);
        ds.setColor(Color.parseColor("#D40A0A"));
    }

    @Override
    public void onClick(View widget) {


    }
}