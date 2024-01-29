package com.winjay.mirrorcast.decode;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import com.winjay.mirrorcast.util.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ScreenDecoder {
    private static final String TAG = ScreenDecoder.class.getSimpleName();

    private MediaCodec mMediaCodec;

    public ScreenDecoder() {
    }

    public void startDecode(Surface surface, int videoWidth, int videoHeight) {
        try {
            mMediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, videoWidth, videoHeight);
            // 码率：每秒传输数据量大小
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, videoWidth * videoHeight / 2);
            // 帧率：帧/每秒
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            mediaFormat.setInteger(MediaFormat.KEY_MAX_FPS_TO_ENCODER, 30);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            mediaFormat.setInteger(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 100_000);

            mMediaCodec.configure(mediaFormat, surface, null, 0);
            mMediaCodec.start();
        } catch (IOException e) {
            LogUtil.e(TAG, "start decode error=" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void decodeData(byte[] data) {
        LogUtil.d(TAG, "data.length=" + data.length);
        if (mMediaCodec == null) {
            LogUtil.w(TAG, "mMediaCodec is null!");
            return;
        }
        try {
            int index = mMediaCodec.dequeueInputBuffer(100000);
//            LogUtil.d(TAG, "index=" + index);
            if (index >= 0) {
                ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(index);
                inputBuffer.clear();
                inputBuffer.put(data, 0, data.length);
                mMediaCodec.queueInputBuffer(index,
                        0, data.length, System.currentTimeMillis(), 0);
            }
            //  获取数据
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 100000);
//            LogUtil.d(TAG, "outputBufferIndex=" + outputBufferIndex);
            while (outputBufferIndex > 0) {
                // true 就是显示在surface上
                mMediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

//        try {
//            ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
//            int dequeueInputBuffer = mMediaCodec.dequeueInputBuffer(-1);
//            if (dequeueInputBuffer < 0) {
//                return;
//            }
//            ByteBuffer byteBuffer = inputBuffers[dequeueInputBuffer];
//            byteBuffer.clear();
//            byteBuffer.put(data, 0, data.length);
//            mMediaCodec.queueInputBuffer(dequeueInputBuffer, 0, data.length, System.currentTimeMillis(), 0);
//
//            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//            int dequeueOutputBuffer = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
//            while (dequeueOutputBuffer >= 0) {
//                mMediaCodec.releaseOutputBuffer(dequeueOutputBuffer, true);
//            }
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
    }

    public void stopDecode() {
        if (mMediaCodec != null) {
            try {
                mMediaCodec.stop();
            } catch (Exception e) {
                LogUtil.e(TAG, "stop decode error=" + e.getMessage());
                e.printStackTrace();
            } finally {
                mMediaCodec.release();
                mMediaCodec = null;
            }
        }
    }
}
