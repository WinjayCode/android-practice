package com.winjay.practice.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.practice.utils.LogUtil;

/**
 * 自定义view学习
 * 注意点：
 * 1.让View支持wrap_content
 * 2.如果有必要，让你的View支持padding，如果不在draw方法中处理padding，那么padding属性无法起作用
 * 3.尽量不要再View中使用Handler，没必要
 * 4.View中如果有动画或者线程，需要及时停止，参考View#onDetchedFromWindow
 * 5.View带有滑动嵌套情形时，需要处理好滑动冲突
 *
 * @author Winjay
 * @date 2020/5/26
 */
public class MyView extends View {
    private static final String TAG = MyView.class.getSimpleName();

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 布局中android:visibility="gone"的时候，还是会初始化对象，但是不会走绘制流程onMeasure、onLayout、onDraw
        LogUtil.d(TAG, "init()");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LogUtil.d(TAG, "onMeasure()");
        int measuredWidth = measureWidth(widthMeasureSpec);
        int measuredHeight = measureHeight(heightMeasureSpec);
        LogUtil.d(TAG, "measuredWidth=" + measuredWidth);
        LogUtil.d(TAG, "measuredHeight=" + measuredHeight);
        setMeasuredDimension(measuredWidth, measuredHeight);
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

    // layout过程
    // 1.通过setFrame方法设定View的四个顶点的位置
    // 2.调用onLayout方法
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        LogUtil.d(TAG, "onLayout()");
    }

    // draw过程
    // 1.绘制背景 background.draw(canvas)
    // 2.绘制自己 (onDraw)
    // 3.绘制children (dispatchDraw:遍历调用所有子元素的draw方法，这样draw事件就一层层传递下去了)
    // 4.绘制装饰 (onDrawScrollBars)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.d(TAG, "onDraw()");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 从XML加载组件后回调
        LogUtil.d(TAG, "onFinishInflate()");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 组件大小改变时回调
        LogUtil.d(TAG, "onSizeChanged():w=" + w + ", h=" + h + ", oldw=" + oldw + ", oldh=" + oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 监听到触摸事件时回调
        LogUtil.d(TAG, "onTouchEvent:event=" + event.getAction());
        return super.onTouchEvent(event);
    }
}
