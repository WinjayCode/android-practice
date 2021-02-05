package com.winjay.practice.media.record;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.audio_focus.AudioFocusManager;
import com.winjay.practice.media.interfaces.MediaType;
import com.winjay.practice.media.music.MusicActivity;
import com.winjay.practice.media.notification.MusicNotificationManager;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.PcmToWavUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 录音学习
 *
 * @author Winjay
 * @date 2020/4/1
 */
public class AudioRecordActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
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

    private final int RC_PERMISSION = 100;

    private String filePath;

    @BindView(R.id.recorder_save_path)
    TextView mSavePath;

    @BindView(R.id.record_btn)
    Button mRecordBtn;

    @BindView(R.id.play_wav_btn)
    Button mPlayWavBtn;

    @BindView(R.id.recording_tv)
    TextView recording_tv;

    private MediaPlayer mediaPlayer;
    private AudioFocusManager mAudioFocusManager;

    @Override
    protected int getLayoutId() {
        return R.layout.andio_recorder_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requiresPermissions();

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

    private void initPlayer() {
        mediaPlayer = new MediaPlayer();
        mAudioFocusManager = new AudioFocusManager(this, MediaType.MUSIC);
        mAudioFocusManager.setOnAudioFocusChangeListener(new AudioFocusManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        LogUtil.d(TAG, "media:AUDIOFOCUS_GAIN");
                        if (mediaPlayer != null) {
                            mediaPlayer.start();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        LogUtil.d(TAG, "media:AUDIOFOCUS_LOSS");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        LogUtil.d(TAG, "media:AUDIOFOCUS_LOSS_TRANSIENT");
                        if (mediaPlayer != null) {
                            mediaPlayer.pause();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        LogUtil.d(TAG, "media:AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        if (mediaPlayer != null) {
                            mediaPlayer.pause();
                        }
                        break;
                }
            }
        });
    }

    /**
     * 开始录音
     */
    @OnClick(R.id.start_record)
    void startRecord() {
        if (recording_tv.getVisibility() == View.GONE) {
            recording_tv.setVisibility(View.VISIBLE);
        }
        filePath = getExternalCacheDir().getPath() + File.separator + System.currentTimeMillis();
        pcmFile = new File(filePath + ".pcm");
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
                    addHeadData();
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
    @OnClick(R.id.stop_record)
    void stopRecord() {
        recording_tv.setVisibility(View.GONE);
        mWhetherRecord = false;
        mSavePath.setText("保存地址：" + filePath + ".pcm");
    }

    private void addHeadData() {
        pcmFile = new File(filePath + ".pcm");
        handlerWavFile = new File(filePath + ".wav");
        PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(mSampleRateInHz, mChannelConfig, mAudioFormat);
        pcmToWavUtil.pcmToWav(pcmFile.toString(), handlerWavFile.toString());
    }

    @OnClick(R.id.play_wav_btn)
    void playWav() {
        if (new File(filePath + ".wav").exists()) {
            if (mediaPlayer.isPlaying()) {
                return;
            }
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioFocusManager.requestFocus()) {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(filePath + ".wav");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        LogUtil.d(TAG, "duration=" + mediaPlayer.getDuration());
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        LogUtil.d(TAG);
                        mAudioFocusManager.releaseAudioFocus();
                    }
                });
            }
        }
    }

    ////////////////////////////////////////// permission //////////////////////////////////////////
    @AfterPermissionGranted(RC_PERMISSION)
    private void requiresPermissions() {
        String[] perms = {Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            LogUtil.d(TAG, "Already have permission, scan files!");
            // Already have permission, do the thing
            initAudioRecord();
            initPlayer();
        } else {
            LogUtil.w(TAG, "Do not have permissions, request them now!");
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "请授予权限，否则影响部分使用功能。", RC_PERMISSION, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.d(TAG);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LogUtil.d(TAG, "Some permissions have been granted: " + perms.toString());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        LogUtil.w(TAG, "Some permissions have been denied: " + perms.toString());
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mAudioFocusManager != null) {
            mAudioFocusManager.releaseAudioFocus();
            mAudioFocusManager = null;
        }
        FileUtil.delete(getExternalCacheDir().getPath());
    }
}
