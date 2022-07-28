package com.winjay.practice.hardware_test.multi_touch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;


import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TouchView extends View {
    private static final String TAG = "TouchView";

    //定义个圆的集合
    private List<TouchCircleView> circles = new ArrayList<>();

    public TouchView(Context context) {
        super(context);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        for (TouchCircleView circle : circles) {
            circle.drawSelf(canvas, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取手指的行为
        int action = event.getAction();
        int action_code = action & 0xff;
        //手指的下标index
        int pointIndex = action >> 8;
        //获取手值的坐标
        float x = event.getX(pointIndex);
        float y = event.getY(pointIndex);
        //获取手值的名字（ID）
        int pointId = event.getPointerId(pointIndex);
        LogUtil.d(TAG, "pointId=" + pointId);
        if (action_code >= 5) {
            action_code -= 5;
        }


        //单点触摸时用action判断
        //多点触摸时用action_code判断
        switch (action_code) {
            case MotionEvent.ACTION_DOWN: //按下
                //实例化圆
                TouchCircleView circle = new TouchCircleView(x, y, pointId);
                //将圆添加到集合中
                circles.add(circle);

                if (mPointerCountListener != null) {
                    mPointerCountListener.pointerCountChanged(pointId);
                }
                break;
            case MotionEvent.ACTION_UP:   //抬起
                //找到具体的圆将它从集合中移除即可
                circles.remove(get(pointId));
                break;
            case MotionEvent.ACTION_MOVE: //移动
                //找到具体的圆，时时修改圆心点坐标即可
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int id = event.getPointerId(i);//得打具体圆的id
                    //从新给圆赋值圆心点坐标
                    get(id).x = event.getX(i);
                    get(id).y = event.getY(i);
                }
                break;
        }

        //重新调用onDraw 重绘
        invalidate();
        //子线程中重新绘制 postInvalidate();
        return true;
    }

    //定义一个方法，通过pointId返回具体的圆
    public TouchCircleView get(int pointId) {
        for (TouchCircleView circle : circles) {
            if (circle.pointId == pointId) {
                return circle;
            }
        }
        return null;
    }

    private PointerCountListener mPointerCountListener;

    public interface PointerCountListener {
        void pointerCountChanged(int pointId);
    }

    public void setPointerCountListener(PointerCountListener pointerCountListener) {
        mPointerCountListener = pointerCountListener;
    }
}
