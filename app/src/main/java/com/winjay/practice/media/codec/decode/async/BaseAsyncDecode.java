package com.winjay.practice.media.codec.decode.async;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.winjay.practice.media.codec.decode.BaseDecode;

/**
 * 异步解码
 *
 * @author Winjay
 * @date 2021-04-27
 */
public abstract class BaseAsyncDecode extends BaseDecode implements Handler.Callback {
    protected static final int MSG_VIDEO_INPUT = 1;
    protected static final int MSG_VIDEO_OUTPUT = 2;
    protected static final int MSG_AUDIO_INPUT = 3;
    protected static final int MSG_AUDIO_OUTPUT = 4;
    private HandlerThread mHandlerThread;
    protected Handler mHandler;

    public BaseAsyncDecode(String sourcePath) {
        super(sourcePath);
        mHandlerThread = new HandlerThread(decodeType() == VIDEO ? "asyncVideoDecodeThread" : "asyncAudioDecodeThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), this);
    }

    public void start() {
    }

    public void release() {
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if (mHandler != null) {
            mHandler.removeMessages(MSG_VIDEO_INPUT);
            mHandler.removeMessages(MSG_VIDEO_OUTPUT);
            mHandler.removeMessages(MSG_AUDIO_INPUT);
            mHandler.removeMessages(MSG_AUDIO_OUTPUT);
        }
        try {
            mediaCodec.stop();
            mediaCodec.release();
            myMediaExtractor.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
