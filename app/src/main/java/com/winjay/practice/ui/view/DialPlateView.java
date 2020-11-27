package com.winjay.practice.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.practice.utils.LogUtil;

/**
 * 表盘view
 *
 * @author Winjay
 * @date 2020/8/13
 */
public class DialPlateView extends View {
    private static final String TAG = DialPlateView.class.getSimpleName();

    private int measureWidth;
    private int measureHeight;

    private Paint mPaintCircle;
    private Paint mPaintDegree;
    private Paint mPaintHour;
    private Paint mPaintMinute;

    public DialPlateView(Context context) {
        this(context, null);
    }

    public DialPlateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialPlateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaintCircle = new Paint();
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStrokeWidth(5);

        mPaintDegree = new Paint();
        mPaintDegree.setStrokeWidth(3);

        mPaintHour = new Paint();
        mPaintHour.setStrokeWidth(20);

        mPaintMinute = new Paint();
        mPaintHour.setStrokeWidth(10);
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
        // 画外圆
        canvas.drawCircle(measureWidth / 2, measureHeight / 2, measureWidth / 2, mPaintCircle);

        // 画刻度
        for (int i = 0; i < 24; i++) {
            if (i == 0 || i == 6 || i == 12 || i == 18) {
                mPaintDegree.setStrokeWidth(5);
                mPaintDegree.setTextSize(30);
                canvas.drawLine(
                        measureWidth / 2,
                        measureHeight / 2 - measureWidth / 2,
                        measureWidth / 2,
                        measureHeight / 2 - measureWidth / 2 + 60,
                        mPaintDegree);
                String degree = String.valueOf(i);
                canvas.drawText(
                        degree,
                        measureWidth / 2 - mPaintDegree.measureText(degree) / 2,
                        measureHeight / 2 - measureWidth / 2 + 90,
                        mPaintDegree);
            } else {
                mPaintDegree.setStrokeWidth(3);
                mPaintDegree.setTextSize(15);
                canvas.drawLine(
                        measureWidth / 2,
                        measureHeight / 2 - measureWidth / 2,
                        measureWidth / 2,
                        measureHeight / 2 - measureWidth / 2 + 30,
                        mPaintDegree);
                String degree = String.valueOf(i);
                canvas.drawText(
                        degree,
                        measureWidth / 2 - mPaintDegree.measureText(degree) / 2,
                        measureHeight / 2 - measureWidth / 2 + 60,
                        mPaintDegree);
            }
            // 以圆心为中心旋转画布
            canvas.rotate(15, measureWidth / 2, measureWidth / 2);
        }

        // 画指针
        canvas.save();
        canvas.translate(measureWidth / 2, measureWidth / 2);
        canvas.drawLine(0, 0, 100, 50, mPaintHour);
        canvas.drawLine(0, 0, 100, 100, mPaintMinute);
        canvas.restore();
    }
}
