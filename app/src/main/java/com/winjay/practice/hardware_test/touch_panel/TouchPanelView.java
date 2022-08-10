package com.winjay.practice.hardware_test.touch_panel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.practice.utils.DisplayUtil;
import com.winjay.practice.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 触摸面板测试View
 *
 * @author Winjay
 * @date 2022-07-27
 */
public class TouchPanelView extends View {
    private static final String TAG = "TPTestView";

    private Paint paint = new Paint();
    private Path mPath = new Path();
    private Paint mPathPaint = new Paint();

    private int mScreenHeight;
    private int mScreenWidth;

    private final int mGridHeight = 40;
    private final int mGridWidth = 40;
    private final int mDiagonalWidth = mGridWidth / 2;

    private HashMap<Rect, Boolean> mRectTouchMap;

    public TouchPanelView(Context context) {
        this(context, null);
    }

    public TouchPanelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchPanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPathPaint.setColor(Color.RED);
        mPathPaint.setStrokeWidth(2);
        mPathPaint.setStyle(Paint.Style.STROKE);

        mScreenWidth = DisplayUtil.getScreenSize(context)[0];
        mScreenHeight = DisplayUtil.getScreenSize(context)[1];
        LogUtil.d(TAG, "mScreenWidth=" + mScreenWidth);
        LogUtil.d(TAG, "mScreenHeight=" + mScreenHeight);

        int colCount = mScreenHeight / mGridHeight;
        int rowCount = mScreenWidth / mGridWidth;
        int diagonalCount = (mScreenWidth - mGridWidth * 2) / mDiagonalWidth;
        float diagonalOffset = (float) (mScreenHeight - mGridHeight * 3) / (diagonalCount - 1);
        LogUtil.d(TAG, "colCount=" + colCount);
        LogUtil.d(TAG, "rowCount=" + rowCount);
        LogUtil.d(TAG, "diagonalCount=" + diagonalCount);
        LogUtil.d(TAG, "diagonalOffset=" + diagonalOffset);
        mRectTouchMap = new HashMap<>();

        // left
        for (int i = 0; i < colCount; i++) {
            Rect rect = new Rect(0, i * mGridHeight, mGridWidth, (i + 1) * mGridHeight);
            mRectTouchMap.put(rect, false);
        }
        // right
        for (int i = 0; i < colCount; i++) {
            Rect rect = new Rect(mScreenWidth - mGridWidth, i * mGridHeight, mScreenWidth, (i + 1) * mGridHeight);
            mRectTouchMap.put(rect, false);
        }
        // top
        for (int i = 0; i < rowCount; i++) {
            Rect rect = new Rect(i * mGridWidth, 0, (i + 1) * mGridWidth, mGridHeight);
            mRectTouchMap.put(rect, false);
        }
        // bottom
        for (int i = 0; i < rowCount; i++) {
            Rect rect = new Rect(i * mGridWidth, mScreenHeight - mGridHeight, (i + 1) * mGridWidth, mScreenHeight);
            mRectTouchMap.put(rect, false);
        }

        // left diagonal
        for (int i = 0; i < diagonalCount; i++) {
            Rect rect = new Rect(i * mDiagonalWidth + mGridWidth, (int) (i * diagonalOffset + mGridHeight),
                    i * mDiagonalWidth + mGridWidth + mDiagonalWidth, (int) (i * diagonalOffset + mGridHeight * 2));
            mRectTouchMap.put(rect, false);
        }
        // right diagonal
        for (int i = 0; i < diagonalCount; i++) {
            Rect rect = new Rect(i * mDiagonalWidth + mGridWidth, (int) (mScreenHeight - mGridHeight * 2 - i * diagonalOffset),
                    i * mDiagonalWidth + mGridWidth + mDiagonalWidth, (int) (mScreenHeight - mGridHeight - i * diagonalOffset));
            mRectTouchMap.put(rect, false);
        }
        LogUtil.d(TAG, "RectTouchMap size=" + mRectTouchMap.size());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.d(TAG);
        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);

        for (Map.Entry<Rect, Boolean> rectBooleanEntry : mRectTouchMap.entrySet()) {
            canvas.drawRect(rectBooleanEntry.getKey(), paint);
        }

        for (Map.Entry<Rect, Boolean> rectBooleanEntry : mRectTouchMap.entrySet()) {
            if (rectBooleanEntry.getValue()) {
                LogUtil.d(TAG, "rect touched.");
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(rectBooleanEntry.getKey(), paint);
            }
        }

        canvas.drawPath(mPath, mPathPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (allRectTouch()) {
            LogUtil.d(TAG, "all rect had changed.");
            mPath.reset();
            invalidate();
            if (mTestSuccessListener != null) {
                mTestSuccessListener.onSuccess();
                mTestSuccessListener = null;
            }
            return true;
        }
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();
        if (touchX == mScreenWidth) {
            touchX = touchX - 1;
        }
        if (touchY == mScreenHeight) {
            touchY = touchY - 1;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                mPath.reset();
                break;
        }
        touchWhere(touchX, touchY);
        invalidate();
        return true;
    }

    private boolean allRectTouch() {
        for (Map.Entry<Rect, Boolean> rectBooleanEntry : mRectTouchMap.entrySet()) {
            if (!rectBooleanEntry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private void touchWhere(int touchX, int touchY) {
        LogUtil.d(TAG, "touchX=" + touchX);
        LogUtil.d(TAG, "touchY=" + touchY);
        for (Map.Entry<Rect, Boolean> rectBooleanEntry : mRectTouchMap.entrySet()) {
            Rect rect = rectBooleanEntry.getKey();
//            LogUtil.d(TAG, "rect=" + rect.toString());
            if (rect.contains(touchX, touchY)) {
                rectBooleanEntry.setValue(true);
            }
        }
    }

    private TestSuccessListener mTestSuccessListener;

    public interface TestSuccessListener {
        void onSuccess();
    }

    public void setTestSuccessListener(TestSuccessListener testSuccessListener) {
        mTestSuccessListener = testSuccessListener;
    }
}
