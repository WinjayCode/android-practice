package com.winjay.practice.media.audio_record.ktv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

/**
 * @author Winjay
 * @date 2023-03-28
 */
public class KTVActivity extends BaseActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord mAudioRecord;
    private AudioTrack mAudioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_activity);

        // 请求权限
        if (!hasPermissions1()) {
            requestPermissions1();
        } else {
            startRecordAndPlay();
        }
    }

    public void startRecordAndPlay() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // 初始化 AudioRecord
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);

        // 初始化 AudioTrack
        int trackBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AUDIO_FORMAT);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AUDIO_FORMAT, trackBufferSize, AudioTrack.MODE_STREAM);

        // 开始录制和播放
        mAudioRecord.startRecording();
        mAudioTrack.play();

        // 开启线程读取录制的数据并写入播放器和文件
        new Thread(() -> {
            byte[] buffer = new byte[bufferSize];
            while (true) {
                // 从麦克风读取数据
                int readSize = mAudioRecord.read(buffer, 0, bufferSize);
                // 将录制的数据写入播放器和文件
                mAudioTrack.write(buffer, 0, readSize);
//                mAudioTrack.play();
                if (readSize == -1) {
                    // 录制结束，停止播放
                    stopPlay();
                    break;
                }
            }
        }).start();
    }

    public void stopPlay() {
        // 停止录制和播放
        mAudioRecord.stop();
        mAudioRecord.release();
        mAudioTrack.stop();
        mAudioTrack.release();
    }

    // 判断是否有需要的权限
    private boolean hasPermissions1() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // 请求权限
    private void requestPermissions1() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecordAndPlay();
            } else {
                // 权限请求失败，提示用户需要权限才能正常使用应用
                Toast.makeText(this, "需要录音和存储权限才能正常使用应用", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();
    }
}
