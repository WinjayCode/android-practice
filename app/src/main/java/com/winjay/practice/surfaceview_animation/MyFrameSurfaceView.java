package com.winjay.practice.surfaceview_animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.winjay.practice.utils.LogUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyFrameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = MyFrameSurfaceView.class.getSimpleName();

    public static final int INFINITE = -1;
    public static final int DEFAULT_INTERVAL = 50;
    private int interval = DEFAULT_INTERVAL;
    private HandlerThread drawHandlerThread;
    private Handler drawHandler;
    private DrawRunnable drawRunnable;
    private List<Integer> bitmapIds = new ArrayList<>();
    private Paint paint = new Paint();
    private BitmapFactory.Options options;
    private Rect srcRect;
    private Rect dstRect = new Rect();
    private Canvas canvas;
    private int bitmapIdIndex = 0;
    private int repeatTimes = INFINITE;
    private int repeatedCount = 0;
    private boolean shouldStart = false;

    public MyFrameSurfaceView(Context context) {
        super(context);
        init();
    }

    public MyFrameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyFrameSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.d(TAG, "surfaceCreated()");
        if (drawHandlerThread == null) {
            drawHandlerThread = new HandlerThread("SurfaceViewThread");
        }
        if (!drawHandlerThread.isAlive()) {
            drawHandlerThread.start();
        }
        if (drawHandler == null) {
            drawHandler = new Handler(drawHandlerThread.getLooper());
        }
        if (drawRunnable == null) {
            drawRunnable = new DrawRunnable();
        }
        if (shouldStart) {
            shouldStart = false;
            start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.d(TAG, "surfaceDestroyed()");
        bitmapIdIndex = 0;
        shouldStart = true;
        if (drawHandlerThread != null) {
            drawHandlerThread.quit();
            drawHandlerThread = null;
        }
        if (drawHandler != null) {
            drawHandler = null;
        }
        if (drawRunnable != null) {
            drawRunnable = null;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        dstRect.set(0, 0, getWidth(), getHeight());
    }

    private void init() {
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);

        options = new BitmapFactory.Options();
        options.inMutable = true;
    }

    private class DrawRunnable implements Runnable {

        @Override
        public void run() {
            if (bitmapIdIndex < bitmapIds.size()) {
                LogUtil.d(TAG, "bitmapIdIndex=" + bitmapIdIndex);
                // 获取画布
                canvas = getHolder().lockCanvas();
                if (canvas != null) {
                    try {
                        // 清空画布
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        canvas.drawPaint(paint);
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                        Bitmap bitmap = decodeBitmap(bitmapIds.get(bitmapIdIndex), options);
                        // 绘制图片资源
                        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
                        options.inBitmap = bitmap;
                        bitmapIdIndex++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (canvas != null) {
                            getHolder().unlockCanvasAndPost(canvas);
                        }
                    }
                }
                drawHandler.postDelayed(drawRunnable, interval);
            } else {
                if (repeatTimes == INFINITE) {
                    LogUtil.d(TAG, "animation is infinite loop!");
                    bitmapIdIndex = 0;
                    drawHandler.postDelayed(drawRunnable, interval);
                } else if (repeatedCount < repeatTimes - 1) {
                    repeatedCount++;
                    LogUtil.d(TAG, "repeatedCount=" + repeatedCount);
                    bitmapIdIndex = 0;
                    drawHandler.postDelayed(drawRunnable, interval);
                } else {
                    repeatedCount = 0;
                    LogUtil.d(TAG, "animation done!");
                }
            }
        }
    }

    private Bitmap decodeBitmap(int resId, BitmapFactory.Options options) {
        options.inScaled = false;
        InputStream inputStream = getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    public void setBitmapIds(List<Integer> bitmapIds) {
        if (bitmapIds == null || bitmapIds.size() == 0) {
            return;
        }
        this.bitmapIds = bitmapIds;
        getBitmapDimension(bitmapIds.get(0));
    }

    private void getBitmapDimension(int bitmapId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(this.getResources(), bitmapId, options);
        srcRect = new Rect(0, 0, options.outWidth, options.outHeight);
        // we have to re-measure to make defaultWidth in use in onMeasure()
        requestLayout();
    }

    public void start() {
        LogUtil.d(TAG, "start()");
        if (drawHandler != null && drawRunnable != null) {
            drawHandler.post(drawRunnable);
        } else {
            shouldStart = true;
        }
    }

    public void stop() {
        if (drawHandler != null && drawRunnable != null) {
            drawHandler.removeCallbacks(drawRunnable);
        }
        bitmapIdIndex = 0;
        repeatedCount = 0;
    }

    public void pause() {
        LogUtil.d(TAG, "pause()");
        if (drawHandler != null && drawRunnable != null) {
            drawHandler.removeCallbacks(drawRunnable);
        }
    }

    public void resume() {
        LogUtil.d(TAG, "resume()");
        if (drawHandler != null && drawRunnable != null) {
            drawHandler.removeCallbacks(drawRunnable);
            drawHandler.post(drawRunnable);
        }
    }

    public void setInterval(int interval) {
        if (interval < DEFAULT_INTERVAL) {
            return;
        }
        this.interval = interval;
    }

    public void setRepeatTimes(int repeatTimes) {
        if (repeatTimes < -1 || repeatTimes == 0) {
            LogUtil.e(TAG, "repeat times is invalid!");
            return;
        }
        this.repeatTimes = repeatTimes;
    }
}
