package com.winjay.practice.media.codec.decode.sync;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.view.Surface;

import com.winjay.practice.media.codec.decode.BaseDecode;

import java.nio.ByteBuffer;

/**
 * 同步解码
 *
 * @author Winjay
 * @date 2021-04-23
 */
public abstract class BaseSyncDecode extends BaseDecode implements Runnable {
    private static final String TAG = BaseSyncDecode.class.getSimpleName();
    protected static final int TIME_US = 10000;
    protected Surface surface;
    private boolean isDone;
    private MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

    public BaseSyncDecode(String sourcePath) {
        super(sourcePath);
        // 子类完成配置工作
        configure();
        // 编解码器开始工作
        mediaCodec.start();
    }

    public BaseSyncDecode(String sourcePath, SurfaceTexture surfaceTexture) {
        super(sourcePath);
        surface = new Surface(surfaceTexture);
        // 子类完成配置工作
        configure();
        // 编解码器开始工作
        mediaCodec.start();
    }

    @Override
    public void run() {
        try {
            while (!isDone) {
                // 从input缓冲区队列申请 empty buffer
                int bufferIndex = mediaCodec.dequeueInputBuffer(TIME_US);
                if (bufferIndex > 0) {
                    // 拿到可用的 empty buffer
                    ByteBuffer inputBuffer = mediaCodec.getInputBuffer(bufferIndex);
                    if (inputBuffer != null) {
                        // 把需要解码的数据拷贝到 empty buffer
                        int bufferSize = myMediaExtractor.readBuffer(inputBuffer);
                        if (bufferSize >= 0) {
                            mediaCodec.queueInputBuffer(
                                    bufferIndex,
                                    0,
                                    bufferSize,
                                    myMediaExtractor.getSampleTime(),
                                    myMediaExtractor.getSampleFlags()
                            );
                        } else {
                            mediaCodec.queueInputBuffer(
                                    bufferIndex,
                                    0,
                                    0,
                                    0,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            );
                            isDone = true;
                        }
                    }
                }
                // 解码后的数据交给子类 (TODO：release后会出现mediaCodec被释放的java.lang.IllegalStateException)
                boolean isFinish = handleOutputData(bufferInfo);
                if (isFinish) {
                    break;
                }
            }

            release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            isDone = true;
            mediaCodec.stop();
            mediaCodec.release();
            myMediaExtractor.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void configure();

    protected abstract boolean handleOutputData(MediaCodec.BufferInfo bufferInfo);
}
