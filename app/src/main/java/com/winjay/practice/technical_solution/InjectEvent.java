package com.winjay.practice.technical_solution;

import android.os.SystemClock;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 模拟事件
 *
 * @author Winjay
 * @date 2023-05-22
 */
public class InjectEvent extends AppCompatActivity {

    /**
     * window.injectInputEvent(inputEvent);
     *
     * @param inputEvent
     */
    public void injectInputEvent(InputEvent inputEvent) {
        Window window = getWindow();
        window.setLocalFocus(true, true);
        window.injectInputEvent(inputEvent);
    }

    private void injectEvent() {
        // 构造 MotionEvent 对象
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int action = MotionEvent.ACTION_DOWN;
        int x = 200;
        int y = 400;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                action,
                x,
                y,
                metaState
        );
        injectInputEvent(motionEvent);

        long downTime2 = SystemClock.uptimeMillis();
        long eventTime2 = SystemClock.uptimeMillis();
        int action2 = MotionEvent.ACTION_UP;
        int x2 = 200;
        int y2 = 400;
        int metaState2 = 0;
        MotionEvent motionEvent2 = MotionEvent.obtain(
                downTime2,
                eventTime2,
                action2,
                x2,
                y2,
                metaState2
        );
        injectInputEvent(motionEvent2);
    }
}
