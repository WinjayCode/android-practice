package com.winjay.practice.media.codec.decode.sync;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;

import com.winjay.practice.media.codec.decode.BaseDecode;
import com.winjay.practice.utils.LogUtil;

import java.nio.ByteBuffer;

/**
 * 同步解码音频
 *
 * @author Winjay
 * @date 2021-04-27
 */
public class SyncAudioDecode extends BaseSyncDecode {
    private static final String TAG = SyncAudioDecode.class.getSimpleName();
    private AudioTrack audioTrack;

    public SyncAudioDecode(String sourcePath) {
        super(sourcePath);

        // 音频格式
        int audioFormat;
        if (mediaFormat.containsKey(MediaFormat.KEY_PCM_ENCODING)) {
            audioFormat = mediaFormat.getInteger(MediaFormat.KEY_PCM_ENCODING);
        } else {
            audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        }
        LogUtil.d(TAG, "audioFormat=" + audioFormat);

        // 音频采样率
        int sampleRateInHz = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        LogUtil.d(TAG, "sampleRateInHz=" + sampleRateInHz);
        // 声道数
        int channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        LogUtil.d(TAG, "channelCount=" + channelCount);
        // 声道配置（单声道或双声道）
        int channelConfig = channelCount == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
        LogUtil.d(TAG, "channelConfig=" + channelConfig);

        //拿到一帧的最小buffer大小
        int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

        /**
         * 设置音频信息属性
         * 1.设置支持多媒体属性，比如audio，video
         * 2.设置音频格式，比如 music
         */
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        /**
         * 设置音频哥特式
         * 1. 设置采样率
         * 2. 设置采样位数
         * 3. 设置声道
         */
        AudioFormat format = new AudioFormat.Builder()
                .setSampleRate(sampleRateInHz)
                .setEncoding(audioFormat)
                .setChannelMask(channelConfig)
                .build();

        audioTrack = new AudioTrack(
                attributes,
                format,
                bufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
        );

        audioTrack.play();
    }

    @Override
    protected void configure() {
        mediaCodec.configure(mediaFormat, null, null, 0);
    }

    @Override
    protected boolean handleOutputData(MediaCodec.BufferInfo bufferInfo) {
        // 从output缓冲区队列申请解码后的buffer
        int bufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIME_US);
        ByteBuffer outputBuffer;
        while (bufferIndex >= 0) {
            outputBuffer = mediaCodec.getOutputBuffer(bufferIndex);
            if (outputBuffer != null) {
                // 写数据到audioTrack，实现音频播放
                audioTrack.write(outputBuffer, bufferInfo.size, AudioTrack.WRITE_BLOCKING);
                mediaCodec.releaseOutputBuffer(bufferIndex, false);
            }
            bufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIME_US);
        }
        // 所有的解码后的数据都被渲染后，就可以停止播放了（bufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM）
        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            LogUtil.d(TAG, "MediaCodec.BUFFER_FLAG_END_OF_STREAM");
            return true;
        }
        return false;
    }

    @Override
    protected int decodeType() {
        return BaseDecode.AUDIO;
    }

    @Override
    public void release() {
        super.release();
        if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
            audioTrack.stop();
            audioTrack.release();
        }
    }
}
