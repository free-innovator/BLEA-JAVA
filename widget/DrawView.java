package com.practice.myapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hagtfms on 2016-06-27.
 */
public class DrawView extends View {
    private final String TAG = "DrawView";
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float initX, initY, radius;
    private boolean drawing = false;

    private int mWindowWidth, mWindowHeight;
    private float mPosX, mPosY;

    public final static double BLUE_X = 0.45, BLUE_Y = 0.35;
    public final static double RED_X = 0.7, RED_Y = 0.85;

    public DrawView(Context context) {
        super(context);
        init(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);

        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        mWindowWidth = dm.widthPixels;
        mWindowHeight = dm.heightPixels;

        mPosX = -1000;
        mPosY = -1000;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                View.MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.BLUE);
        canvas.drawCircle((float)(mWindowWidth*BLUE_X), (float)(mWindowHeight*BLUE_Y), 50, paint);

        paint.setColor(Color.RED);
        canvas.drawCircle((float)(mWindowWidth*RED_X), (float)(mWindowHeight*RED_Y), 50, paint);

        paint.setColor(Color.GREEN);
        canvas.drawCircle(mPosX, mPosY, 50, paint);

        if (drawing) {
            paint.setColor(Color.WHITE);
            canvas.drawCircle(initX, initY, 50, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if (action == MotionEvent.ACTION_MOVE) {
            initX = event.getX();
            initY = event.getY();
        } else if (action == MotionEvent.ACTION_DOWN) {
            initX = event.getX();
            initY = event.getY();
            drawing = true;
        } else if (action == MotionEvent.ACTION_UP) {
            drawing = false;
            performClick();
        }
        invalidate();
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void setPos(float x, float y){
        mPosX = x;
        mPosY = y;
    }

    public int getWindowWidth() { return mWindowWidth; }
    public int getWindowHeight() { return mWindowHeight; }
}
