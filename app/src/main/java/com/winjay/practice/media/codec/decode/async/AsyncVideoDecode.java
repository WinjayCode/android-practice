package com.winjay.practice.media.codec.decode.async;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.winjay.practice.utils.LogUtil;

import java.nio.ByteBuffer;

/**
 * 异步视频解码
 *
 * @author Winjay
 * @date 2021-04-27
 */
public class AsyncVideoDecode extends BaseAsyncDecode {
    private static final String TAG = AsyncVideoDecode.class.getSimpleName();
    protected Surface surface;
    // 用于对准视频的时间戳
    private long mStartMS = -1;

    public AsyncVideoDecode(String sourcePath, SurfaceTexture surfaceTexture) {
        super(sourcePath);
        surface = new Surface(surfaceTexture);
    }

    @Override
    public void start() {
        super.start();
        mediaCodec.setCallback(new MediaCodec.Callback() {
            @Override
            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                Message message = new Message();
                message.what = MSG_VIDEO_INPUT;
                Bundle bundle = new Bundle();
                bundle.putInt("index", index);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            @Override
            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                Message message = new Message();
                message.what = MSG_VIDEO_OUTPUT;
                Bundle bundle = new Bundle();
                bundle.putInt("index", index);
                bundle.putLong("ptsTime", info.presentationTimeUs);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            @Override
            public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                LogUtil.e(TAG);
                codec.stop();
            }

            @Override
            public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

            }
        });
        mediaCodec.configure(mediaFormat, surface, null, 0);
        mediaCodec.start();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_VIDEO_INPUT:
                Bundle bundle = msg.getData();
                int index = bundle.getInt("index");
                // 拿到可用的 empty buffer
                ByteBuffer inputBuffer = mediaCodec.getInputBuffer(index);
                if (inputBuffer != null) {
                    // 把需要解码的数据拷贝到 empty buffer
                    int bufferSize = myMediaExtractor.readBuffer(inputBuffer);
                    if (bufferSize >= 0) {
                        mediaCodec.queueInputBuffer(
                                index,
                                0,
                                bufferSize,
                                myMediaExtractor.getSampleTime(),
                                myMediaExtractor.getSampleFlags()
                        );
                    } else {
                        mediaCodec.queueInputBuffer(
                                index,
                                0,
                                0,
                                0,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        );
                    }
                }
                break;
            case MSG_VIDEO_OUTPUT:
                try {
                    Bundle outputBundle = msg.getData();
                    int outputIndex = outputBundle.getInt("index");
                    long ptsTime = outputBundle.getLong("ptsTime");
                    if (mStartMS == -1) {
                        mStartMS = System.currentTimeMillis();
                    }
                    sleepRender(ptsTime, mStartMS);
                    mediaCodec.releaseOutputBuffer(outputIndex, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    protected int decodeType() {
        return VIDEO;
    }

    /**
     * 校准PTS
     *
     * @param ptsTime
     * @param startMS
     */
    private void sleepRender(long ptsTime, long startMS) {
        ptsTime = ptsTime / 1000;
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
