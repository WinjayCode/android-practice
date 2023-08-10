package com.winjay.practice.hardware_test.mic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioRecord;
import android.util.AttributeSet;
import android.view.View;

import com.winjay.practice.R;


public class VUMeter extends View {
    static final long ANIMATION_INTERVAL = 70;
    static final float DROPOFF_STEP = 0.18f;
    static final float PIVOT_RADIUS = 3.5f;
    static final float PIVOT_Y_OFFSET = 10.0f;
    static final float SHADOW_OFFSET = 2.0f;
    static final float SURGE_STEP = 0.35f;
    float mCurrentAngle;
    Paint mPaint;
    AudioRecord mRecorder;
    Paint mShadow;
    private int progress = 0;

    public VUMeter(Context context) {
        super(context);
        init(context);
    }

    public VUMeter(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    /* access modifiers changed from: package-private */
    public void init(Context context) {
        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.vumeter));
        this.mPaint = new Paint(1);
        this.mPaint.setColor(-1);
        this.mShadow = new Paint(1);
        this.mShadow.setColor(Color.argb(60, 0, 0, 0));
        this.mRecorder = null;
        this.mCurrentAngle = 0.0f;
    }

    public void setRecorder(AudioRecord audioRecord) {
        this.mRecorder = audioRecord;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        float f = 0.3926991f;
        if (!(this.mRecorder == null || Integer.MIN_VALUE == this.progress)) {
            f = 0.3926991f + ((((float) this.progress) * 2.3561947f) / 200.0f);
        }
        if (f > this.mCurrentAngle) {
            this.mCurrentAngle = f;
        } else {
            this.mCurrentAngle = Math.max(f, this.mCurrentAngle - DROPOFF_STEP);
        }
        this.mCurrentAngle = Math.min(2.7488937f, this.mCurrentAngle);
        float height = (float) getHeight();
        float width = ((float) getWidth()) / SHADOW_OFFSET;
        float f2 = (height - PIVOT_RADIUS) - PIVOT_Y_OFFSET;
        float f3 = (height * 4.0f) / 5.0f;
        float sin = (float) Math.sin((double) this.mCurrentAngle);
        float cos = width - (((float) Math.cos((double) this.mCurrentAngle)) * f3);
        float f4 = f2 - (f3 * sin);
        float f5 = cos + SHADOW_OFFSET;
        float f6 = f4 + SHADOW_OFFSET;
        float f7 = width + SHADOW_OFFSET;
        float f8 = f2 + SHADOW_OFFSET;
        canvas.drawLine(f5, f6, f7, f8, this.mShadow);
        canvas2.drawCircle(f7, f8, PIVOT_RADIUS, this.mShadow);
        canvas.drawLine(cos, f4, width, f2, this.mPaint);
        canvas2.drawCircle(width, f2, PIVOT_RADIUS, this.mPaint);
        if (this.mRecorder != null && this.mRecorder.getRecordingState() == 3) {
            postInvalidateDelayed(ANIMATION_INTERVAL);
        }
    }

    public void setProgress(int i) {
        this.progress = i;
        postInvalidate();
    }
}
