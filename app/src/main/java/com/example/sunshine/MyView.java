package com.example.sunshine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

/**
 * Author: Anatol Salanevich
 * Date: 02.08.2016
 */
public class MyView extends View {

    Paint mCirclePaint;
    Paint mTextPaint;
    Paint mLinePaint;
    Paint mLinePaint2;
    final float TEXT_SIZE = 32;
    private float mDegrees;

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.BLACK);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(6f);
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(TEXT_SIZE);
        mTextPaint.setStrokeWidth(2f);
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.GREEN);
        mLinePaint.setStrokeWidth(3);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint2 = new Paint();
        mLinePaint2.setColor(Color.RED);
        mLinePaint2.setStrokeWidth(10);
        mLinePaint2.setStyle(Paint.Style.STROKE);
    }

    public void updateView(float degrees) {
        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        mDegrees = degrees;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int myHeight = 200;
        if (hSpecMode == MeasureSpec.EXACTLY) {
            myHeight = hSpecSize;
        } else {
            if (hSpecMode == MeasureSpec.AT_MOST) {
                myHeight = 100;
            }
        }
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myWidth = 200;
        if (wSpecMode == MeasureSpec.EXACTLY) {
            myWidth = wSpecSize;
        } else {
            if (wSpecSize == MeasureSpec.AT_MOST) {
                myWidth = 100;
            }
        }
        setMeasuredDimension(myWidth, myHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("MyView", "onDraw: Degrees = " + mDegrees);
        float radius;
        float height, width;
        float centerX, centerY;
        height = getHeight();
        width = getWidth();
        centerX = width/2;
        centerY = height/2;
        if (height > width) {
            radius = width/2 - 5;
        } else {
            radius = height/2 - 5;
        }
        canvas.drawCircle(centerX, centerY, radius, mCirclePaint);
        canvas.drawText("N", centerX + 7, TEXT_SIZE, mTextPaint);
        canvas.drawText("S", centerX + 7, height - 11, mTextPaint);
        canvas.drawText("W", 11, centerY - 3, mTextPaint);
        canvas.drawText("E", width - TEXT_SIZE, centerY - 7, mTextPaint);
        canvas.drawLine(0, centerY, width, centerY, mLinePaint);
        canvas.drawLine(centerX, height, centerX, 0, mLinePaint);
        canvas.drawLine(centerX, centerY,
                centerX + radius*(float) Math.sin(mDegrees*3.14/180),
                centerY - radius*(float) Math.cos(mDegrees*3.14/180),
                mLinePaint2);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.getText().add(String.valueOf(mDegrees));
        return true;
    }
}
