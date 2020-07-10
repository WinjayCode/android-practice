package com.winjay.practice.surfaceview_animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * SurfaceView实现帧动画
 *
 * @author Winjay
 * @date 2020/7/9
 */
public class FrameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = FrameSurfaceView.class.getSimpleName();
    /**
     * 动画无限循环
     */
    public static final int INFINITE = -1;
    /**
     * 默认动画刷新间隔（ms）
     */
    public static final int DEFAULT_INTERVAL = 50;
    /**
     * 动画刷新间隔（ms）
     */
    private int interval = DEFAULT_INTERVAL;
    private HandlerThread drawHandlerThread;
    private Handler drawHandler;
    private DrawRunnable drawRunnable;
    /**
     * 绘制资源集合
     */
    private List<Integer> bitmapIds = new ArrayList<>();
    private Paint paint = new Paint();
    private BitmapFactory.Options options;
    private Rect srcRect;
    private Rect dstRect = new Rect();
    private Canvas canvas;
    /**
     * 绘制资源index
     */
    private int bitmapIdIndex = 0;
    /**
     * 动画重复次数
     * The number of times the animation must repeat. By default, an animation repeats infinite.
     */
    private int repeatCount = INFINITE;
    /**
     * 动画已播放次数
     * Indicates how many times the animation was repeated.
     */
    private int mRepeated = 0;
    /**
     * 是否在之后的某个时刻启动
     */
    private boolean shouldStart = false;
    private Bitmap bitmap;

    private List<FrameAnimation> mFrameAnimationList;

    private FrameAnimation mCurrentFrameAnimation;

    public FrameSurfaceView(Context context) {
        super(context);
        init();
    }

    public FrameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrameSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated()");
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
        Log.d(TAG, "surfaceDestroyed()");
        if (drawHandler != null && drawRunnable != null) {
            drawHandler.removeCallbacks(drawRunnable);
        }
        bitmapIdIndex = 0;
        shouldStart = true;
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
        // bitmap可修改，需要bitmap复用时options.inBitmap，需要配置
        options.inMutable = true;

        drawHandlerThread = new HandlerThread("SurfaceViewThread");
        drawHandlerThread.start();
        drawHandler = new Handler(drawHandlerThread.getLooper());
        drawRunnable = new DrawRunnable();
    }

    private class DrawRunnable implements Runnable {

        @Override
        public void run() {
            if (bitmapIdIndex < bitmapIds.size()) {
//                Log.d(TAG, "bitmapIdIndex=" + bitmapIdIndex);
                // 获取画布
                canvas = getHolder().lockCanvas();
                if (canvas != null) {
                    try {
                        // 清空画布
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        canvas.drawPaint(paint);
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                        // 抗锯齿
                        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                        bitmap = decodeBitmap(bitmapIds.get(bitmapIdIndex), options);
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
                // TODO 此处可能有空指针风险！
                drawHandler.postDelayed(drawRunnable, interval);
            } else {
                bitmapIdIndex = 0;
                if (mCurrentFrameAnimation == null) {
                    if (bitmapIds.size() == 1) {
                        Log.d(TAG, "there is only one bitmap!");
                        mRepeated = 0;
                        return;
                    }
                }

                if (repeatCount == INFINITE) {
                    Log.d(TAG, "animation is infinite loop!");
                    drawHandler.postDelayed(drawRunnable, interval);
                } else if (mRepeated < repeatCount - 1) {
                    mRepeated++;
                    Log.d(TAG, "mRepeated=" + mRepeated);
                    drawHandler.postDelayed(drawRunnable, interval);
                } else {
                    mRepeated = 0;
                    if (mCurrentFrameAnimation != null) {
                        mFrameAnimationList.remove(mCurrentFrameAnimation);
                        if (!mFrameAnimationList.isEmpty()) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    start();
                                }
                            });
                        } else {
                            mCurrentFrameAnimation = null;
                            Log.d(TAG, "all animation done!");
                        }
                    } else {
                        Log.d(TAG, "animation done!");
                    }
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
        // 不会真正返回bitmap，只会返回宽高信息
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(this.getResources(), bitmapId, options);
        srcRect = new Rect(0, 0, options.outWidth, options.outHeight);
        requestLayout();
    }

    public void start() {
        Log.d(TAG, "start()");
        if (mFrameAnimationList != null && !mFrameAnimationList.isEmpty()) {
            if (mFrameAnimationList.get(0).getBitmapIds().isEmpty()) {
                return;
            }
            mCurrentFrameAnimation = mFrameAnimationList.get(0);
            setBitmapIds(mCurrentFrameAnimation.getBitmapIds());
            setRepeatCount(mCurrentFrameAnimation.getRepeatCount());
        }
        drawHandler.post(drawRunnable);
    }

    public void reset() {
        if (drawHandler != null && drawRunnable != null) {
            drawHandler.removeCallbacks(drawRunnable);
        }
        bitmapIdIndex = 0;
        mRepeated = 0;
        if (mFrameAnimationList != null) {
            mFrameAnimationList.clear();
        }
        mCurrentFrameAnimation = null;
    }

    public void pause() {
        Log.d(TAG, "pause()");
        if (drawHandler != null && drawRunnable != null) {
            drawHandler.removeCallbacks(drawRunnable);
        }
    }

    public void resume() {
        Log.d(TAG, "resume()");
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

    public void setRepeatCount(int repeatCount) {
        if (this.repeatCount < -1 || this.repeatCount == 0) {
            Log.e(TAG, "repeat times is invalid!");
            return;
        }
        this.repeatCount = repeatCount;
    }

    public void destroy() {
        Log.d(TAG, "destroy()");
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
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public static class FrameAnimation {
        private List<Integer> bitmapIds = new ArrayList<>();

        private int repeatCount = INFINITE;

        public FrameAnimation(List<Integer> bitmapIds, int repeatCount) {
            setBitmapIds(bitmapIds);
            setRepeatCount(repeatCount);
        }

        public int getRepeatCount() {
            return repeatCount;
        }

        public void setRepeatCount(int repeatCount) {
            if (this.repeatCount < -1 || this.repeatCount == 0) {
                Log.e(TAG, "repeat times is invalid!");
                return;
            }
            this.repeatCount = repeatCount;
        }

        public void setBitmapIds(List<Integer> bitmapIds) {
            if (bitmapIds == null || bitmapIds.size() == 0) {
                return;
            }
            this.bitmapIds = bitmapIds;
        }

        public List<Integer> getBitmapIds() {
            return bitmapIds;
        }
    }

    public void playSequentially(List<FrameAnimation> frameAnimations) {
        if (frameAnimations != null && !frameAnimations.isEmpty()) {
            mFrameAnimationList = frameAnimations;
        }
    }
}
