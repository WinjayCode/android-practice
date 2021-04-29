package com.winjay.practice.media.codec.decode.async;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;

import com.winjay.practice.utils.LogUtil;

import java.nio.ByteBuffer;

/**
 * 异步解码音频
 *
 * @author Winjay
 * @date 2021-04-29
 */
public class AsyncAudioDecode extends BaseAsyncDecode {
    private static final String TAG = AsyncAudioDecode.class.getSimpleName();
    private AudioTrack audioTrack;

    public AsyncAudioDecode(String sourcePath) {
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
    public void start() {
        super.start();
        mediaCodec.setCallback(new MediaCodec.Callback() {
            @Override
            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                Message message = new Message();
                message.what = MSG_AUDIO_INPUT;
                Bundle bundle = new Bundle();
                bundle.putInt("index", index);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            @Override
            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                Message message = new Message();
                message.what = MSG_AUDIO_OUTPUT;
                Bundle bundle = new Bundle();
                bundle.putInt("index", index);
                bundle.putInt("size", info.size);
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
        mediaCodec.configure(mediaFormat, null, null, 0);
        mediaCodec.start();
    }

    @Override
    public void release() {
        super.release();
        if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
            audioTrack.stop();
            audioTrack.release();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_AUDIO_INPUT:
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
            case MSG_AUDIO_OUTPUT:
                try {
                    Bundle outputBundle = msg.getData();
                    int outputIndex = outputBundle.getInt("index");
                    int size = outputBundle.getInt("size");

                    ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outputIndex);
                    if (outputBuffer != null) {
                        // 写数据到audioTrack，实现音频播放
                        audioTrack.write(outputBuffer, size, AudioTrack.WRITE_BLOCKING);
                        mediaCodec.releaseOutputBuffer(outputIndex, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected int decodeType() {
        return AUDIO;
    }
}
