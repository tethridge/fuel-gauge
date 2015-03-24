/*
 * Copyright (C) 2015 Unchained Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.unchained.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * This is a custom fuel gauge view.
 *
 * Created by tale on 3/1/15.
 */
public class FuelGaugeView extends View {

    private final static int TICK_STROKE_WIDTH_PX = 4;

    private float mDensity;
    private float mLevel;

    private float mNumDegreesToShow;
    private float mDegreesStart;
    private float mDegreesEnd;
    private Point mNeedlePivot;

    private Paint mPaint;
    private Path mNeedlePath;
    private int mNeedleColor;
    private int mLineColor;
    private int mTextColor;
    private int mLabelColor;
    private int mEmptyColor;
    private String mLabel;

    private float mShortTickLength;
    private float mLongTickLength;
    private float mTickStart;

    public FuelGaugeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FuelGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.FuelGaugeView, defStyleAttr, 0);

        mLevel = attributes.getFloat(R.styleable.FuelGaugeView_level, 1);
        mNeedleColor = attributes.getColor(R.styleable.FuelGaugeView_needleColor, Color.BLUE);
        mTextColor = attributes.getColor(R.styleable.FuelGaugeView_textColor, Color.BLACK);
        mLineColor = attributes.getColor(R.styleable.FuelGaugeView_lineColor, Color.BLACK);
        mLabelColor = attributes.getColor(R.styleable.FuelGaugeView_labelColor, Color.BLACK);
        mEmptyColor = attributes.getColor(R.styleable.FuelGaugeView_emptyColor, Color.RED);
        mLabel = attributes.getString(R.styleable.FuelGaugeView_label);

        attributes.recycle();

        mNeedlePath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDensity = getResources().getDisplayMetrics().density;

        mNumDegreesToShow = 90;
        mDegreesStart = -(mNumDegreesToShow / 2);
        mDegreesEnd = mDegreesStart + mNumDegreesToShow;

        mShortTickLength = 10 * mDensity;
        mLongTickLength = 20 * mDensity;

        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Force to be a square.
        int widthPixels = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightPixels = View.MeasureSpec.getSize(heightMeasureSpec);
        int minLength = Math.min(widthPixels, heightPixels);
        setMeasuredDimension(minLength, minLength);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mNeedlePivot == null) {
            int height = getHeight();
            mNeedlePivot = new Point((height / 2), (height / 4) * 3);
            mTickStart = (height / 2);

            mNeedlePath.reset();
            float needleBottom = mNeedlePivot.y + (40 * mDensity);
            float needleTop = mNeedlePivot.y - (180 * mDensity);
            float topWidth = 3 * mDensity;
            float bottomWidth = 8 * mDensity;
            mNeedlePath.moveTo(mNeedlePivot.x - (bottomWidth / 2), needleBottom);
            mNeedlePath.lineTo(mNeedlePivot.x - (topWidth / 2), needleTop);
            mNeedlePath.lineTo(mNeedlePivot.x + (topWidth / 2), needleTop);
            mNeedlePath.lineTo(mNeedlePivot.x + (bottomWidth / 2), needleBottom);
            mNeedlePath.close();
            mNeedlePath.lineTo(mNeedlePivot.x - (bottomWidth / 2), needleBottom);
            mNeedlePath.addCircle(mNeedlePivot.x, mNeedlePivot.y, (15 * mDensity), Path.Direction.CW);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawGrid(canvas, mNeedlePivot);
        drawGridLabels(canvas, mNeedlePivot);
        drawNeedle(canvas, mNeedlePivot);

        if (!TextUtils.isEmpty(mLabel)) {
            drawLabel(canvas);
        }
    }

    public void setFuelLevel(float level) {
        level = constrain(level, 0, 1f);

        if (level != this.mLevel) {
            this.mLevel = level;
            invalidate();
        }
    }

    public float getFuelLevel() {
        return mLevel;
    }

    protected void drawGrid(Canvas canvas, Point pivot) {
        float longTickIncrement = mNumDegreesToShow / 4f;
        float tickIncrement = longTickIncrement / 2f;

        float tickStartX = pivot.x;
        float tickStartY = pivot.y - mTickStart;
        float tickStopX = pivot.x;
        float tickStopY = tickStartY - mLongTickLength;
        float shortTickStopY = tickStartY - mShortTickLength;

        mPaint.setColor(mEmptyColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(TICK_STROKE_WIDTH_PX * mDensity);

        canvas.save();
        canvas.rotate(mDegreesStart, pivot.x, pivot.y);
        boolean drawLongTick = true;
        for (int i = 0; i < 9; i++) {
            canvas.drawLine(tickStartX, tickStartY, tickStopX,
                    drawLongTick ? tickStopY : shortTickStopY, mPaint);

            if (i == 0) {
                // Draw the empty "red" arc.
                float angleStart = 225 - mDegreesStart;
                float strokeWidth = tickStartY - shortTickStopY;
                mPaint.setStrokeWidth(strokeWidth);
                float radius = Math.round(mNeedlePivot.y - shortTickStopY - (strokeWidth / 2));
                float left = mNeedlePivot.x - radius;
                float top = mNeedlePivot.y - radius;
                float right = mNeedlePivot.x + radius;
                float bottom = mNeedlePivot.y + radius;
                RectF rect = new RectF(left, top, right, bottom);
                canvas.drawArc(rect,
                        Math.round(angleStart),
                        Math.round(tickIncrement - 1),
                        false,
                        mPaint);
                mPaint.setColor(mLineColor);
                mPaint.setStrokeWidth(TICK_STROKE_WIDTH_PX * mDensity);
            }

            canvas.rotate(tickIncrement, pivot.x, pivot.y);
            drawLongTick = !drawLongTick;

        }
        canvas.restore();
    }

    protected void drawGridLabels(Canvas canvas, Point pivot) {
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(15 * mDensity);
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        String oneHalf = getResources().getString(R.string.half);
        float textWidth = mPaint.measureText(oneHalf);
        float textX = pivot.x - (textWidth / 2);
        float textY = pivot.y - mTickStart - mLongTickLength - mShortTickLength;
        canvas.drawText(oneHalf, textX, textY, mPaint);

        canvas.save();
        canvas.translate(pivot.x, pivot.y);
        float radius = pivot.y - (pivot.y - mTickStart - mLongTickLength - mShortTickLength);

        String empty = getResources().getString(R.string.empty);
        textWidth = mPaint.measureText(empty);
        double radians = Math.toRadians(mDegreesStart - 90);
        textX = (float) (radius * Math.cos(radians)) - (textWidth/2);
        textY = (float) (radius * Math.sin(radians));
        canvas.drawText(empty, textX, textY, mPaint);

        String full = getResources().getString(R.string.full);
        textWidth = mPaint.measureText(full);
        radians = Math.toRadians(mDegreesEnd - 90);
        textX = (float) (radius * Math.cos(radians)) - (textWidth/2);
        textY = (float) (radius * Math.sin(radians));
        canvas.drawText(full, textX, textY, mPaint);

        canvas.restore();
    }

    protected void drawNeedle(Canvas canvas, Point pivot) {
        mPaint.setColor(mNeedleColor);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.save();
        canvas.rotate(mDegreesStart, pivot.x, pivot.y);
        float degreesToAdd = constrain(Math.round(mNumDegreesToShow * mLevel), 0, mNumDegreesToShow);
        canvas.rotate(degreesToAdd, pivot.x, pivot.y);
        canvas.drawPath(mNeedlePath, mPaint);
        canvas.restore();
    }

    protected void drawLabel(Canvas canvas) {
        mPaint.setColor(mLabelColor);
        mPaint.setTextSize(mDensity * 12);
        float textWidth = mPaint.measureText(mLabel);
        canvas.drawText(mLabel, (getWidth()/2) - (textWidth/2), getHeight()/2, mPaint);
    }

    protected float constrain(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
