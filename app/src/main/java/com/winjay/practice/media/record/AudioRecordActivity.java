package com.winjay.practice.media.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.PcmToWavUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;

/**
 * 录音学习
 *
 * @author Winjay
 * @date 2020/4/1
 */
public class AudioRecordActivity extends BaseActivity {
    public static final String TAG = AudioRecordActivity.class.getSimpleName();

    private AudioRecord mAudioRecord;

    /**
     * 音频源 这里选择使用麦克风：MediaRecorder.AudioSource.MIC
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

    private File pcmFile;
    private File handlerWavFile;

    @BindView(R.id.recorder_save_path)
    TextView mSavePath;

    @BindView(R.id.record_btn)
    Button mRecordBtn;

    @BindView(R.id.play_wav_btn)
    Button mPlayWavBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.andio_recorder_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAudioRecord();

        mRecordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startRecord();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        stopRecord();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
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
    private void startRecord() {
        pcmFile = new File(getExternalCacheDir().getPath(), System.currentTimeMillis() + ".pcm");
        mWhetherRecord = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 开始录制
                mAudioRecord.startRecording();
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(pcmFile);
                    byte[] bytes = new byte[mRecordBufferSize];
                    while (mWhetherRecord) {
                        // 读取流
                        mAudioRecord.read(bytes, 0, bytes.length);
                        fileOutputStream.write(bytes);
                        fileOutputStream.flush();

                    }
                    LogUtil.d(TAG, "run: 暂停录制");
                    // 停止录制
                    mAudioRecord.stop();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    // 添加音频头部信息并且转成wav格式
//                    addHeadData();
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
    private void stopRecord() {
        mWhetherRecord = false;
    }

    private void addHeadData() {
        pcmFile = new File(AudioRecordActivity.this.getExternalCacheDir().getPath(), "audioRecord.pcm");
        handlerWavFile = new File(AudioRecordActivity.this.getExternalCacheDir().getPath(), "audioRecord_handler.wav");
        PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(mSampleRateInHz, mChannelConfig, mAudioFormat);
        pcmToWavUtil.pcmToWav(pcmFile.toString(), handlerWavFile.toString());
    }

}
