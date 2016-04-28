package com.practice.myapplication.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by hagtfms on 2016-04-28.
 * no used
 */
public class SquareButton extends Button {
    public SquareButton(Context context) {
        super(context);
    }
    public SquareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SquareButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if(width > height) width = height;
        else height = width;

        setMeasuredDimension(width, height);
    }
}
