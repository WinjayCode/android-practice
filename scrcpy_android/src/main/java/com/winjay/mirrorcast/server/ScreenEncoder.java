package com.winjay.mirrorcast.server;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.view.Surface;

import com.winjay.mirrorcast.util.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ScreenEncoder extends Thread {
    private static final String TAG = "ScreenEncoder";
    private static final int VIDEO_WIDTH = 1080;
    private static final int VIDEO_HEIGHT = 2400;

    private final MediaProjection mMediaProjection;
    private final SocketManager mSocketManager;
    private MediaCodec mMediaCodec;
    private boolean mPlaying = true;

    public static int VIRTUAL_DISPLAY_ID;

    public ScreenEncoder(SocketManager socketManager, MediaProjection mediaProjection) {
        mSocketManager = socketManager;
        mMediaProjection = mediaProjection;
    }

    public void startEncode() {
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, VIDEO_WIDTH, VIDEO_HEIGHT);
        // 描述视频格式的内容的颜色格式
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        // 码率
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 8000000);
        // 帧率
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 60);
        // 设置 I 帧间隔
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        // repeat after 100ms
        mediaFormat.setInteger(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 100_000);
        try {
            // 创建编码MediaCodec 类型是video/avc
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            // 配置编码器
            mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            // 创建一个目的surface来存放输入数据
            Surface surface = mMediaCodec.createInputSurface();
            // 获取屏幕流
            VirtualDisplay screen = mMediaProjection.createVirtualDisplay(
                    "screen",
                    VIDEO_WIDTH,
                    VIDEO_HEIGHT,
                    1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    surface,
                    null,
                    null);
            VIRTUAL_DISPLAY_ID = screen.getDisplay().getDisplayId();
            LogUtil.d(TAG, "virtual display id=" + VIRTUAL_DISPLAY_ID);
        } catch (IOException e) {
            LogUtil.w(TAG, "startEncode exception=" + e);
            e.printStackTrace();
        }

        start();
    }

    @Override
    public void run() {
        mMediaCodec.start();

//        boolean eof = false;
//        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//
//        while (!eof) {
//            int outputBufferId = mMediaCodec.dequeueOutputBuffer(bufferInfo, -1);
//            LogUtil.d(TAG,"outputBufferId=" + outputBufferId);
//            eof = (bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
//            LogUtil.d(TAG, "eof=" + eof);
//            try {
//                if (outputBufferId >= 0) {
//                    ByteBuffer codecBuffer = mMediaCodec.getOutputBuffer(outputBufferId);
//
//                    byte[] b = new byte[codecBuffer.remaining()];
//                    codecBuffer.get(b, 0, b.length);
//                    LogUtil.d(TAG,"send buffer.size=" + b.length);
//                    mSocketManager.sendData(b);
//                }
//            } catch (Exception e) {
//                LogUtil.e(TAG,"encode error=" + e.getMessage());
//            } finally {
//                if (outputBufferId >= 0) {
//                    LogUtil.d(TAG,"releaseOutputBuffer");
//                    mMediaCodec.releaseOutputBuffer(outputBufferId, false);
//                }
//            }
//        }
//        mMediaCodec.stop();


        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (true) {
            try {
                int outputBufferId = mMediaCodec.dequeueOutputBuffer(bufferInfo, 10000);
                if (outputBufferId >= 0) {
                    ByteBuffer byteBuffer = mMediaCodec.getOutputBuffer(outputBufferId);
                    // 拿到每一帧 如果是I帧 则在I帧前面插入 sps pps
                    dealFrame(byteBuffer, bufferInfo);
                    mMediaCodec.releaseOutputBuffer(outputBufferId, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static final int NAL_I = 5;
    public static final int NAL_SPS = 7;
    private byte[] sps_pps_buf;

    /**
     * 绘制每一帧，因为录屏 只有第一帧有 sps 、pps 和 vps，所以我们需要在每一 I 帧 之前插入 sps 、pps 和 vps 的内容
     *
     * @param byteBuffer
     * @param bufferInfo
     */
    private void dealFrame(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        int offset = 4;
        if (byteBuffer.get(2) == 0x01) {
            offset = 3;
        }

        int type = byteBuffer.get(offset) & 0x1f;
        /////////////////////////////////// 如果是H265 这里type 要换 //////////////////////////////////////////////
//        int type = (byteBuffer.get(offset) & 0x7E) >> 1;
        // sps_pps_buf 帧记录下来
        if (type == NAL_SPS) {
            sps_pps_buf = new byte[bufferInfo.size];
            byteBuffer.get(sps_pps_buf);
        } else if (type == NAL_I) {
            // I 帧 ，把 vps_sps_pps 帧塞到 I帧之前一起发出去
            final byte[] bytes = new byte[bufferInfo.size];
            byteBuffer.get(bytes);

            byte[] newBuf = new byte[sps_pps_buf.length + bytes.length];
            System.arraycopy(sps_pps_buf, 0, newBuf, 0, sps_pps_buf.length);
            System.arraycopy(bytes, 0, newBuf, sps_pps_buf.length, bytes.length);
            mSocketManager.sendData(newBuf);
            LogUtil.d(TAG, "I帧 视频数据  " + bytes.length);
        } else {
            // B 帧 P 帧 直接发送
            final byte[] bytes = new byte[bufferInfo.size];
            byteBuffer.get(bytes);
            mSocketManager.sendData(bytes);
            LogUtil.d(TAG, "B帧 P帧 视频数据  " + bytes.length);
        }
    }

    public void stopEncode() {
        mPlaying = false;
        if (mMediaCodec != null) {
            mMediaCodec.release();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
    }
}
