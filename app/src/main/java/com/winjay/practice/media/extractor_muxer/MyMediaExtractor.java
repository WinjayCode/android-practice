package com.winjay.practice.media.extractor_muxer;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.winjay.practice.utils.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 分离视频轨和音频轨
 *
 * @author Winjay
 * @date 2021-04-15
 */
public class MyMediaExtractor {
    private static final String TAG = MyMediaExtractor.class.getSimpleName();
    private MediaExtractor mMediaExtractor;
    private int mVideoTrackId;
    private int mAudioTrackId;
    private MediaFormat mVideoFormat;
    private MediaFormat mAudioFormat;
    private String[] mimeType;
    private int mTrackCount;
    private long mCurrentSampleTime;
    private int mCurrentSampleFlags;

    public MyMediaExtractor(String sourcePath) {
        mMediaExtractor = new MediaExtractor();
        try {
            mMediaExtractor.setDataSource(sourcePath);
            // 获取视频的轨道数（视频轨和音频轨）
            mTrackCount = mMediaExtractor.getTrackCount();
            LogUtil.d(TAG, "轨道数：" + mTrackCount);
            mimeType = new String[mTrackCount];
            for (int i = 0; i < mTrackCount; i++) {
                // 媒体格式类，用于描述媒体的格式参数，如视频帧率、音频采样率等
                MediaFormat trackFormat = mMediaExtractor.getTrackFormat(i);
                // 获取 mime 类型
                String mime = trackFormat.getString(MediaFormat.KEY_MIME);
                LogUtil.d(TAG, "mime=" + mime);
                mimeType[i] = mime;
                // 视频轨
                if (mime.startsWith("video")) {
                    mVideoTrackId = i;
                    mVideoFormat = trackFormat;
                }
                // 音频轨
                else if (mime.startsWith("audio")) {
                    mAudioTrackId = i;
                    mAudioFormat = trackFormat;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTrackCount() {
        return mTrackCount;
    }

    public void selectTrack(int trackId) {
        mMediaExtractor.selectTrack(trackId);
    }

    public String[] getMimeType() {
        return mimeType;
    }

    public int[] getVideoWH() {
        int[] size = new int[2];
        if (mVideoFormat != null) {
            int width = mVideoFormat.getInteger(MediaFormat.KEY_WIDTH);
            int height = mVideoFormat.getInteger(MediaFormat.KEY_HEIGHT);
            LogUtil.d(TAG, "视频宽高：" + width + "x" + height);
            size[0] = width;
            size[1] = height;
        }
        return size;
    }

    public long getVideoDuration() {
        long duration = 0;
        if (mVideoFormat != null) {
            duration = mVideoFormat.getLong(MediaFormat.KEY_DURATION);
            duration = duration / 1000 / 1000 / 60;
        }
        LogUtil.d(TAG, "视频时间：" + duration + "分钟");
        return duration;
    }

    public int getVideoFrameRate() {
        int frameRate = 0;
        if (mVideoFormat != null) {
            frameRate = mVideoFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
        }
        LogUtil.d(TAG, "视频帧率：" + frameRate + "fps");
        return frameRate;
    }

    public int getVideoTrackId() {
        return mVideoTrackId;
    }

    public int getAudioTrackId() {
        return mAudioTrackId;
    }

    public MediaFormat getVideoFormat() {
        return mVideoFormat;
    }

    public MediaFormat getAudioFormat() {
        return mAudioFormat;
    }

    /**
     * 读取一帧的数据
     *
     * @param buffer
     * @return
     */
    public int readBuffer(ByteBuffer buffer) {
        buffer.clear();
        int bufferCount = mMediaExtractor.readSampleData(buffer, 0);
        if (bufferCount < 0) {
            return -1;
        }
        // 记录当前时间戳
        mCurrentSampleTime = mMediaExtractor.getSampleTime();
        // 记录当前帧的标志位
        mCurrentSampleFlags = mMediaExtractor.getSampleFlags();
        // 进入下一帧
        mMediaExtractor.advance();
        return bufferCount;
    }

    public long getSampleTime() {
        return mCurrentSampleTime;
    }

    public int getSampleFlags() {
        return mCurrentSampleFlags;
    }

    public void release() {
        mMediaExtractor.release();
    }
}
