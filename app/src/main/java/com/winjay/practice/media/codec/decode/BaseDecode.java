package com.winjay.practice.media.codec.decode;

import android.media.MediaCodec;
import android.media.MediaFormat;

import com.winjay.practice.media.extractor_muxer.MyMediaExtractor;

import java.io.IOException;

/**
 * 解码器
 *
 * @author Winjay
 * @date 2021-04-23
 */
public abstract class BaseDecode {
    private static final String TAG = BaseDecode.class.getSimpleName();
    public static final int VIDEO = 1;
    public static final int AUDIO = 2;
    protected MyMediaExtractor myMediaExtractor;
    protected MediaFormat mediaFormat;
    protected MediaCodec mediaCodec;

    public BaseDecode(String sourcePath) {
        try {
            myMediaExtractor = new MyMediaExtractor(sourcePath);
            int type = decodeType();
            mediaFormat = (type == VIDEO ? myMediaExtractor.getVideoFormat() : myMediaExtractor.getAudioFormat());
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            myMediaExtractor.selectTrack(type == VIDEO ? myMediaExtractor.getVideoTrackId() : myMediaExtractor.getAudioTrackId());
            // 创建解码器（创建编码器MediaCodec.createEncoderByType()）
            mediaCodec = MediaCodec.createDecoderByType(mime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract int decodeType();
}
