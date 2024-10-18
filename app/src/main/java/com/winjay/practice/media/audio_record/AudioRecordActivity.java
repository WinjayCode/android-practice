package com.winjay.practice.media.audio_record;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.audio_focus.AudioFocusManager;
import com.winjay.practice.media.interfaces.AudioType;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.PcmToWavUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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

    private AudioTrackThread mAudioTrackThread;

    @Override
    protected int getLayoutId() {
        return R.layout.audio_recorder_activity;
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mAudioRecord = new AudioRecord(mAudioSource, mSampleRateInHz, mChannelConfig, mAudioFormat, mRecordBufferSize);
    }

    private void initPlayer() {
        mediaPlayer = new MediaPlayer();
        mAudioFocusManager = new AudioFocusManager(this);
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

    // 分离左右声道
    private void splitStereoPcm(byte[] data) {
        int monoLength = data.length / 2;
        byte[] leftData = new byte[monoLength];
        byte[] rightData = new byte[monoLength];
        for (int i = 0; i < monoLength; i++) {
            if (i % 2 == 0) {
                System.arraycopy(data, i * 2, leftData, i, 2);
            } else {
                System.arraycopy(data, i * 2, rightData, i - 1, 2);
            }
        }
        //TODO 使用leftData、rightData 进行其他处理
    }

    /**
     * 左右声道进行反转
     * @param data
     * @return
     *      反转后的数据
     */
    private byte[] getReversedData(byte[] data) {
        byte[] reversed = new byte[data.length];
        for (int i = 0; i < data.length - 3; i = i + 4) {
            reversed[i] = data[i+2];
            reversed[i+1] = data[i+3];
            reversed[i+2] = data[i];
            reversed[i+3] = data[i+1];
        }
        return reversed;
    }

    @OnClick(R.id.play_wav_btn)
    void playWav() {
        if (handlerWavFile != null && handlerWavFile.exists()) {
            if (mediaPlayer.isPlaying()) {
                return;
            }
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioFocusManager.requestAudioFocus(AudioType.MEDIA)) {
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
                        mAudioFocusManager.abandonAudioFocus();
                    }
                });
            }
        }
    }

    @OnClick(R.id.system_play_wav_btn)
    public void playWavBySystem() {
        if (handlerWavFile != null && handlerWavFile.exists()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri;
            //Android 7.0 以上，需要使用 FileProvider
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, "com.winjay.practice.fileprovider", handlerWavFile);
            } else {
                uri = Uri.fromFile(handlerWavFile.getAbsoluteFile());
            }
            intent.setDataAndType(uri, "audio");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.play_pcm_static_btn)
    public void playPcmStatic() {
        if (pcmFile != null && pcmFile.exists()) {
            try {
                InputStream is = new FileInputStream(pcmFile);
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
                        .setSampleRate(mSampleRateInHz)
                        .setEncoding(mAudioFormat)
                        .setChannelMask(mChannelConfig)
                        .build();
                //注意 bufferSizeInBytes 使用音频的大小
                AudioTrack audioTrack = new AudioTrack(
                        attributes,
                        format,
                        bytes.length,
                        AudioTrack.MODE_STATIC, //设置为静态模式
                        AudioManager.AUDIO_SESSION_ID_GENERATE //音频识别id
                );
                //一次性写入
                audioTrack.write(bytes, 0, bytes.length);
                //开始播放
                audioTrack.play();

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "AudioTrack method static e=" + e);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.play_pcm_stream_btn)
    public void playPcmStream() {
        if (pcmFile != null && pcmFile.exists()) {
            if (mAudioTrackThread != null) {
                mAudioTrackThread.down();
                mAudioTrackThread = null;
            }
            //播放pcm文件
            mAudioTrackThread = new AudioTrackThread();
            mAudioTrackThread.start();
        }
    }

    class AudioTrackThread extends Thread {
        AudioTrack audioTrack;
        private final int bufferSize;
        private boolean isDone;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public AudioTrackThread() {
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
                    .setSampleRate(mSampleRateInHz)
                    .setEncoding(mAudioFormat)
                    .setChannelMask(mChannelConfig)
                    .build();
            //拿到一帧的最小buffer大小
            bufferSize = AudioTrack.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat);
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
        public void run() {
            super.run();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(pcmFile);
                byte[] buffer = new byte[bufferSize];
                int len;
                while (!isDone && (len = fis.read(buffer)) > 0) {
                    audioTrack.write(buffer, 0, len);
                }

                audioTrack.stop();
                audioTrack.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void down() {
            isDone = true;
        }
    }

    @OnClick(R.id.get_mic_btn)
    void getMic() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] devices = mAudioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        LogUtil.d(TAG, "devices num=" + devices.length);
        if (devices != null && devices.length > 0) {
            for (int i = 0; i < devices.length; i++) {
                if (devices[i].getType() == AudioDeviceInfo.TYPE_BUILTIN_MIC) {
                    LogUtil.d(TAG, "mic_product_name" + devices[i].getProductName());
                    LogUtil.d(TAG, "mic_id" + devices[i].getId());
                    LogUtil.d(TAG, "mic_channel_counts" + Arrays.toString(devices[i].getChannelCounts()));
//                    this.micRecorddev = devices[i];
//                    audioRecord.setPreferredDevice(devices[i]);
                }
            }
        }
    }

    ////////////////////////////////////////// permission //////////////////////////////////////////
    @AfterPermissionGranted(RC_PERMISSION)
    private void requiresPermissions() {
        String[] perms = {Manifest.permission.RECORD_AUDIO};
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
    ////////////////////////////////////////// permission end //////////////////////////////////////////

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
            mAudioFocusManager.abandonAudioFocus();
            mAudioFocusManager = null;
        }
        FileUtil.delete(getExternalCacheDir().getPath());
    }
}
