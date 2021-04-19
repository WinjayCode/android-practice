package com.winjay.practice.media.extractor_muxer;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.text.TextUtils;

import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 合成新视频
 * 可以合成单独的音频或者单独的视频
 * 音频+音频？
 * 视频+视频？
 * 音频格式只支持：AAC，m4a
 *
 * @author Winjay
 * @date 2021-04-16
 */
public class MyMediaMuxer {
    private static final String TAG = MyMediaMuxer.class.getSimpleName();

    public static final int VIDEO_TRACK_IN_VIDEO = 1;
    public static final int AUDIO_TRACK_IN_VIDEO = 2;
    public static final int MIX_TRACK_IN_VIDEO = 3;
    public static final int MIX_TRACK_IN_VIDEO_AND_AUDIO = 4;

    private MediaMuxer mMediaMuxer;
    private MuxerListener mMuxerListener;
    private MyMediaExtractor mVideoMediaExtractor;
    private MyMediaExtractor mAudioMediaExtractor;
    private String mVideoSourcePath;
    private String mAudioSourcePath;
    private String fileName = "muxer.mp4";
    private String destPath;
    private int mMuxerType;

//    /**
//     * @param sourcePath 原始视频资源
//     * @param destPath   合成完成的视频资源
//     */
//    public MyMediaMuxer(String sourcePath, String destPath) {
//        LogUtil.d(TAG, "sourcePath=" + sourcePath);
//        LogUtil.d(TAG, "destPath=" + destPath);
//        this.destPath = destPath;
//        File dir = new File(destPath);
//        if (!dir.exists()) {
//            dir.mkdir();
//        }
//        File destFile = new File(destPath, fileName);
//        if (destFile.exists()) {
//            destFile.delete();
//        }
//        try {
//            mMyMediaExtractor = new MyMediaExtractor(sourcePath);
//            mMediaMuxer = new MediaMuxer(destFile.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private MyMediaMuxer(Builder builder) {
        mVideoSourcePath = builder.mVideoSourcePath;
        mAudioSourcePath = builder.mAudioSourcePath;
        destPath = builder.mDestPath;
        mMuxerType = builder.mMuxerType;
        LogUtil.d(TAG, "mVideoSourcePath=" + mVideoSourcePath);
        LogUtil.d(TAG, "mAudioSourcePath=" + mAudioSourcePath);
        if (!TextUtils.isEmpty(destPath)) {
            File dir = new File(destPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File destFile = new File(destPath, fileName);
            if (destFile.exists()) {
                destFile.delete();
            }
            try {
                if (!TextUtils.isEmpty(mVideoSourcePath)) {
                    mVideoMediaExtractor = new MyMediaExtractor(mVideoSourcePath);
                }
                if (!TextUtils.isEmpty(mAudioSourcePath)) {
                    mAudioMediaExtractor = new MyMediaExtractor(mAudioSourcePath);
                }
                mMediaMuxer = new MediaMuxer(destFile.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        HandlerManager.getInstance().postOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mMuxerListener != null) {
                        mMuxerListener.onStart();
                    }

                    // 视频中提取视频轨道
                    if (mMuxerType == VIDEO_TRACK_IN_VIDEO) {
                        if (mVideoMediaExtractor == null) {
                            if (mMuxerListener != null) {
                                mMuxerListener.onFail("源视频资源异常！");
                            }
                            return;
                        }
                        // 添加视频轨
                        int videoId = mMediaMuxer.addTrack(mVideoMediaExtractor.getVideoFormat());
                        // 开始混合，等待写入
                        mMediaMuxer.start();

                        ByteBuffer buffer = ByteBuffer.allocate(500 * 1024);
                        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

                        // 混合视频
                        int videoSize;
                        // 读取视频帧数据，直到结束
                        mVideoMediaExtractor.selectTrack(mVideoMediaExtractor.getVideoTrackId());
                        while ((videoSize = mVideoMediaExtractor.readBuffer(buffer)) > 0) {
                            // 数据开始的位置
                            info.offset = 0;
                            // 需要写入数据的大小
                            info.size = videoSize;
                            // 缓冲区的时间戳(微妙)
                            info.presentationTimeUs = mVideoMediaExtractor.getSampleTime();
                            // 缓冲区的标志位
                            info.flags = mVideoMediaExtractor.getSampleFlags();
                            mMediaMuxer.writeSampleData(videoId, buffer, info);
                        }
                    }
                    // 视频中提取音频轨道
                    if (mMuxerType == AUDIO_TRACK_IN_VIDEO) {
                        if (mVideoMediaExtractor == null) {
                            if (mMuxerListener != null) {
                                mMuxerListener.onFail("源视频资源异常！");
                            }
                            return;
                        }
                        // 添加音频轨
                        int audioId = mMediaMuxer.addTrack(mVideoMediaExtractor.getAudioFormat());
                        // 开始混合，等待写入
                        mMediaMuxer.start();

                        ByteBuffer buffer = ByteBuffer.allocate(500 * 1024);
                        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

                        // 混合音频
                        int audioSize;
                        // 读取音频帧数据，直到结束
                        mVideoMediaExtractor.selectTrack(mVideoMediaExtractor.getAudioTrackId());
                        while ((audioSize = mVideoMediaExtractor.readBuffer(buffer)) > 0) {
                            // 数据开始的位置
                            info.offset = 0;
                            // 需要写入数据的大小
                            info.size = audioSize;
                            // 缓冲区的时间戳(微妙)
                            info.presentationTimeUs = mVideoMediaExtractor.getSampleTime();
                            // 缓冲区的标志位
                            info.flags = mVideoMediaExtractor.getSampleFlags();
                            mMediaMuxer.writeSampleData(audioId, buffer, info);
                        }
                    }
                    // 视频重新混合
                    if (mMuxerType == MIX_TRACK_IN_VIDEO) {
                        if (mVideoMediaExtractor == null) {
                            if (mMuxerListener != null) {
                                mMuxerListener.onFail("源视频资源异常！");
                            }
                            return;
                        }
                        // 添加音频轨
                        int audioId = mMediaMuxer.addTrack(mVideoMediaExtractor.getAudioFormat());
                        // 添加视频轨
                        int videoId = mMediaMuxer.addTrack(mVideoMediaExtractor.getVideoFormat());
                        // 开始混合，等待写入
                        mMediaMuxer.start();

                        ByteBuffer buffer = ByteBuffer.allocate(500 * 1024);
                        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

                        // 混合视频
                        int videoSize;
                        // 读取视频帧数据，直到结束
                        mVideoMediaExtractor.selectTrack(mVideoMediaExtractor.getVideoTrackId());
                        while ((videoSize = mVideoMediaExtractor.readBuffer(buffer)) > 0) {
                            // 数据开始的位置
                            info.offset = 0;
                            // 需要写入数据的大小
                            info.size = videoSize;
                            // 缓冲区的时间戳(微妙)
                            info.presentationTimeUs = mVideoMediaExtractor.getSampleTime();
                            // 缓冲区的标志位
                            info.flags = mVideoMediaExtractor.getSampleFlags();
                            mMediaMuxer.writeSampleData(videoId, buffer, info);
                        }

                        // 混合音频
                        int audioSize;
                        // 读取音频帧数据，直到结束
                        mVideoMediaExtractor.selectTrack(mVideoMediaExtractor.getAudioTrackId());
                        while ((audioSize = mVideoMediaExtractor.readBuffer(buffer)) > 0) {
                            // 数据开始的位置
                            info.offset = 0;
                            // 需要写入数据的大小
                            info.size = audioSize;
                            // 缓冲区的时间戳(微妙)
                            info.presentationTimeUs = mVideoMediaExtractor.getSampleTime();
                            // 缓冲区的标志位
                            info.flags = mVideoMediaExtractor.getSampleFlags();
                            mMediaMuxer.writeSampleData(audioId, buffer, info);
                        }
                    }
                    // 视频中混合新音频轨道
                    if (mMuxerType == MIX_TRACK_IN_VIDEO_AND_AUDIO) {
                        if (mVideoMediaExtractor == null) {
                            if (mMuxerListener != null) {
                                mMuxerListener.onFail("源视频资源异常！");
                            }
                            return;
                        }
                        if (mAudioMediaExtractor == null) {
                            if (mMuxerListener != null) {
                                mMuxerListener.onFail("源音频资源异常！");
                            }
                            return;
                        }

                        // 添加音频轨
                        int audioId = mMediaMuxer.addTrack(mAudioMediaExtractor.getAudioFormat());
                        // 添加视频轨
                        int videoId = mMediaMuxer.addTrack(mVideoMediaExtractor.getVideoFormat());
                        // 开始混合，等待写入
                        mMediaMuxer.start();

                        ByteBuffer buffer = ByteBuffer.allocate(500 * 1024);
                        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

                        // 混合视频
                        int videoSize;
                        // 读取视频帧数据，直到结束
                        mVideoMediaExtractor.selectTrack(mVideoMediaExtractor.getVideoTrackId());
                        while ((videoSize = mVideoMediaExtractor.readBuffer(buffer)) > 0) {
                            // 数据开始的位置
                            info.offset = 0;
                            // 需要写入数据的大小
                            info.size = videoSize;
                            // 缓冲区的时间戳(微妙)
                            info.presentationTimeUs = mVideoMediaExtractor.getSampleTime();
                            // 缓冲区的标志位
                            info.flags = mVideoMediaExtractor.getSampleFlags();
                            mMediaMuxer.writeSampleData(videoId, buffer, info);
                        }

                        // 混合音频
                        int audioSize;
                        // 读取音频帧数据，直到结束
                        mAudioMediaExtractor.selectTrack(mAudioMediaExtractor.getAudioTrackId());
                        while ((audioSize = mAudioMediaExtractor.readBuffer(buffer)) > 0) {
                            // 数据开始的位置
                            info.offset = 0;
                            // 需要写入数据的大小
                            info.size = audioSize;
                            // 缓冲区的时间戳(微妙)
                            info.presentationTimeUs = mAudioMediaExtractor.getSampleTime();
                            // 缓冲区的标志位
                            info.flags = mAudioMediaExtractor.getSampleFlags();
                            mMediaMuxer.writeSampleData(audioId, buffer, info);
                        }
                    }

                    // 释放资源
                    if (mVideoMediaExtractor != null) {
                        mVideoMediaExtractor.release();
                    }
                    if (mAudioMediaExtractor != null) {
                        mAudioMediaExtractor.release();
                    }
                    mMediaMuxer.stop();
                    mMediaMuxer.release();
                    if (mMuxerListener != null) {
                        mMuxerListener.onSuccess(destPath + File.separator + fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mMuxerListener != null) {
                        mMuxerListener.onFail(e.getMessage());
                    }
                }
            }
        });
    }

    public static class Builder {
        private String mVideoSourcePath;
        private String mAudioSourcePath;
        private String mDestPath;
        private int mMuxerType;

        public Builder() {
        }

        public MyMediaMuxer build() {
            return new MyMediaMuxer(this);
        }

        public Builder setVideoSource(String videoSourcePath) {
            mVideoSourcePath = videoSourcePath;
            return this;
        }

        public Builder setAudioSource(String audioSourcePath) {
            mAudioSourcePath = audioSourcePath;
            return this;
        }

        public Builder setDestPath(String destPath) {
            mDestPath = destPath;
            return this;
        }

        public Builder setMuxerType(int muxerType) {
            mMuxerType = muxerType;
            return this;
        }
    }

    public void setMuxerListener(MuxerListener mMuxerListener) {
        this.mMuxerListener = mMuxerListener;
    }

    interface MuxerListener {
        void onStart();

        void onSuccess(String filePath);

        void onFail(String errorMsg);
    }
}
