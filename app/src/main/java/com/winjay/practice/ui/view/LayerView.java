package com.winjay.practice.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.practice.utils.LogUtil;

/**
 * 图层学习
 *
 * @author Winjay
 * @date 2020/8/13
 */
public class LayerView extends View {
    private static final String TAG = LayerView.class.getSimpleName();

    private int measureWidth;
    private int measureHeight;

    private Paint mPaint;


    public LayerView(Context context) {
        this(context, null);
    }

    public LayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LogUtil.d(TAG, "onMeasure()");
        measureWidth = measureWidth(widthMeasureSpec);
        measureHeight = measureHeight(heightMeasureSpec);
        LogUtil.d(TAG, "measuredWidth=" + measureWidth);
        LogUtil.d(TAG, "measuredHeight=" + measureHeight);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        // 获得测量模式
        int specMode = MeasureSpec.getMode(measureSpec);
        // 获得大小
        int specSize = MeasureSpec.getSize(measureSpec);
        // 具体数值或者match_parent
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 200;
            // wrap_parent
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 200;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GRAY);

        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(150, 150, 100, mPaint);

        canvas.saveLayerAlpha(0, 0, 400, 400, 255);

        mPaint.setColor(Color.RED);
        canvas.drawCircle(200, 200, 100, mPaint);

        canvas.restore();
    }
}
