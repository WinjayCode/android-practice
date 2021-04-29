package com.winjay.practice.media.codec.decode.sync;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;

import com.winjay.practice.media.codec.decode.BaseDecode;
import com.winjay.practice.utils.LogUtil;

/**
 * 同步解码视频
 *
 * @author Winjay
 * @date 2021-04-23
 */
public class SyncVideoDecode extends BaseSyncDecode {
    private static final String TAG = SyncVideoDecode.class.getSimpleName();
    // 用于对准视频的时间戳
    private long mStartMS = -1;

    public SyncVideoDecode(String sourcePath) {
        super(sourcePath);
    }

    public SyncVideoDecode(String sourcePath, SurfaceTexture surfaceTexture) {
        super(sourcePath, surfaceTexture);
    }

    @Override
    protected int decodeType() {
        return BaseDecode.VIDEO;
    }

    @Override
    protected void configure() {
        mediaCodec.configure(mediaFormat, surface, null, 0);
    }

    @Override
    protected boolean handleOutputData(MediaCodec.BufferInfo bufferInfo) {
        // 从output缓冲区队列申请解码后的buffer
        int bufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIME_US);
        if (mStartMS == -1) {
            mStartMS = System.currentTimeMillis();
        }
        while (bufferIndex >= 0) {
            // 校准PTS(否则视频播放会出现倍速的效果)
            sleepRender(bufferInfo, mStartMS);

            mediaCodec.releaseOutputBuffer(bufferIndex, true);

            // 所有的解码后的数据都被渲染后，就可以停止播放了（bufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM）
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                LogUtil.d(TAG, "MediaCodec.BUFFER_FLAG_END_OF_STREAM");
                return true;
            }

            bufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIME_US);
        }
        return false;
    }

    /**
     * 校准PTS
     *
     * @param bufferInfo
     * @param startMS
     */
    private void sleepRender(MediaCodec.BufferInfo bufferInfo, long startMS) {
        long ptsTime = bufferInfo.presentationTimeUs / 1000;
        long systemTime = System.currentTimeMillis() - startMS;
        long timeDifference = ptsTime - systemTime;
        // 如果当前帧比系统时间差快了，则延时下
        if (timeDifference > 0) {
            try {
                Thread.sleep(timeDifference);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
