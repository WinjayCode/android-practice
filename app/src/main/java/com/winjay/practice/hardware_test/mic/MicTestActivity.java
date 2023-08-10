package com.winjay.practice.hardware_test.mic;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.audio_focus.AudioFocusManager;
import com.winjay.practice.media.interfaces.MediaType;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Mic Test
 */
public class MicTestActivity extends BaseActivity {
    private static final String TAG = MicTestActivity.class.getSimpleName();

    private AudioRecord mAudioRecord;

    /**
     * 音频源
     */
    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    /**
     * 采样率（赫兹）与初始化获取每一帧流的Size保持一致
     */
    private int mSampleRateInHz = 44100;
    /**
     * 声道配置  描述音频声道的配置,例如左声道/右声道/前声道/后声道。与初始化获取每一帧流的Size保持一致
     */
    private int mChannelConfig = AudioFormat.CHANNEL_IN_STEREO;
    /**
     * 音频格式  表示音频数据的格式。与初始化获取每一帧流的Size保持一致
     */
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * 每一帧的字节流大小
     */
    private int mRecordBufferSize;
    /**
     * 录音状态
     */
    private boolean mWhetherRecord = false;

    @BindView(R.id.uvMeter)
    VUMeter mVUMeter;

    private File pcmFile;
    private File leftPcmFile;
    private File rightPcmFile;

    private int mMicType = 1;

    private AudioTrack mAudioTrack;
    private AudioFocusManager audioFocusManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mic_test;
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("mic_type")) {
            mMicType = getIntent().getIntExtra("mic_type", 1);
        }
        initAudioRecord();

        mVUMeter.setRecorder(mAudioRecord);

        audioFocusManager = new AudioFocusManager(this, MediaType.MUSIC);
    }

    /**
     * 初始化录音机
     */
    private void initAudioRecord() {
        LogUtil.d(TAG, "mAudioSource=" + mAudioSource);
        LogUtil.d(TAG, "mSampleRateInHz=" + mSampleRateInHz);
        LogUtil.d(TAG, "mChannelConfig=" + mChannelConfig);
        LogUtil.d(TAG, "mAudioFormat=" + mAudioFormat);
        // 获取每一帧的字节流大小
        mRecordBufferSize = AudioRecord.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat);
        LogUtil.d(TAG, "mRecordBufferSize=" + mRecordBufferSize);
        mAudioRecord = new AudioRecord(mAudioSource, mSampleRateInHz, mChannelConfig, mAudioFormat, mRecordBufferSize);
    }

    /**
     * 开始录音
     */
    @OnClick(R.id.recordButton)
    void startRecord() {
        LogUtil.d(TAG);
        stopPlay();
        pcmFile = new File(getExternalCacheDir().getPath(), "audioRecord.pcm");
        if (pcmFile.exists()) {
            pcmFile.delete();
        }

        leftPcmFile = new File(getExternalCacheDir().getPath(), "left.pcm");
        if (leftPcmFile.exists()) {
            leftPcmFile.delete();
        }

        rightPcmFile = new File(getExternalCacheDir().getPath(), "right.pcm");
        if (rightPcmFile.exists()) {
            rightPcmFile.delete();
        }

        mWhetherRecord = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "start record!");
                // 开始录制
                mAudioRecord.startRecording();
                FileOutputStream fileOutputStream = null;
                FileOutputStream leftFileOutputStream = null;
                FileOutputStream rightFileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(pcmFile);
                    leftFileOutputStream = new FileOutputStream(leftPcmFile);
                    rightFileOutputStream = new FileOutputStream(rightPcmFile);
                    byte[] bytes = new byte[mRecordBufferSize];
                    while (mWhetherRecord) {
                        // 读取流
                        int read = mAudioRecord.read(bytes, 0, bytes.length);
                        fileOutputStream.write(bytes);
                        fileOutputStream.flush();

                        // 分离立体声为左右单声道
                        int monoLength = bytes.length / 2;
                        byte[] leftData = new byte[monoLength];
                        byte[] rightData = new byte[monoLength];
                        for (int i = 0; i < monoLength; i++) {
                            if (i % 2 == 0) {
                                System.arraycopy(bytes, i * 2, leftData, i, 2);
                            } else {
                                System.arraycopy(bytes, i * 2, rightData, i - 1, 2);
                            }
                        }
                        // 分别保存左右声道音频
                        leftFileOutputStream.write(leftData);
                        leftFileOutputStream.flush();

                        rightFileOutputStream.write(rightData);
                        rightFileOutputStream.flush();


                        // 录音db
                        long j = 0;
                        try {
                            short[] bytesToShort = bytesToShort(bytes);
                            for (int i = 0; i < bytesToShort.length; i++) {
                                j += (long) (bytesToShort[i] * bytesToShort[i]);
                            }
                            double d = ((double) j) / ((double) read);
                            double progress = Math.log10(d) * 10.0d;
                            if (progress > 30.0d) {
//                                LogUtil.d(TAG, "progress=" + progress);
                            }
                            mVUMeter.setProgress((int)progress);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        //
                    }
                    LogUtil.d(TAG, "stop record!");
                    // 停止录制
                    mAudioRecord.stop();
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    leftFileOutputStream.flush();
                    leftFileOutputStream.close();

                    rightFileOutputStream.flush();
                    rightFileOutputStream.close();

                    mVUMeter.setProgress(0);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    mAudioRecord.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 结束录音
     */
    @OnClick(R.id.stopButton)
    void stopRecord() {
        mWhetherRecord = false;
    }

    // AudioTrack静态模式播放
    @OnClick(R.id.playButton)
    public void playPcmStatic() {
        LogUtil.d(TAG);
        stopPlay();
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioFocusManager.requestFocus()) {
            File file;
            LogUtil.d(TAG, "mMicType=" + mMicType);
            if (mMicType == 1) {
                file = leftPcmFile;
            } else {
                file = rightPcmFile;
            }
            if (file != null && file.exists()) {
                try {
                    InputStream is = new FileInputStream(file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int len;
                    //创建一个数组
                    byte[] buffer = new byte[1024];
                    while ((len = is.read(buffer)) > 0) {
                        //把数据存到ByteArrayOutputStream中
                        baos.write(buffer, 0, len);
                    }
                    //拿到音频数据
                    byte[] bytes = baos.toByteArray();

                    /**
                     * 设置音频哥特式
                     * 1. 设置采样率
                     * 2. 设置采样位数
                     * 3. 设置声道
                     */
                    AudioFormat format = new AudioFormat.Builder()
                            .setSampleRate(mSampleRateInHz)
                            .setEncoding(mAudioFormat)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build();
                    //注意 bufferSizeInBytes 使用音频的大小
                    mAudioTrack = new AudioTrack(
                            audioFocusManager.getAudioAttributes(),
                            format,
                            bytes.length,
                            AudioTrack.MODE_STATIC, //设置为静态模式
                            AudioManager.AUDIO_SESSION_ID_GENERATE //音频识别id
                    );
                    //一次性写入
                    mAudioTrack.write(bytes, 0, bytes.length);
                    //开始播放
                    mAudioTrack.play();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "AudioTrack method static e=" + e);
                }
            }
        }
    }

    private void stopPlay() {
        if (mAudioTrack != null && mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.stop();
        }
    }

    private short[] bytesToShort(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        short[] sArr = new short[(bArr.length / 2)];
        ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(sArr);
        return sArr;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
        }
        if (audioFocusManager != null) {
            audioFocusManager.releaseAudioFocus();
        }
        if (pcmFile != null && pcmFile.exists()) {
            pcmFile.delete();
        }
        if (leftPcmFile != null && leftPcmFile.exists()) {
            leftPcmFile.delete();
        }
        if (rightPcmFile != null && rightPcmFile.exists()) {
            rightPcmFile.delete();
        }
        FileUtil.delete(getExternalCacheDir().getPath());
    }
}
