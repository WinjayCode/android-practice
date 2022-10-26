package com.winjay.practice.media;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.MainAdapter;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.audio_focus.AudioFocusTestActivity;
import com.winjay.practice.media.camera.CameraListActivity;
import com.winjay.practice.media.codec.MediaCodecActivity;
import com.winjay.practice.media.exoplayer.ExoPlayerActivity;
import com.winjay.practice.media.extractor_muxer.MediaExtractorAndMuxerActivity;
import com.winjay.practice.media.media_list.ImageListActivity;
import com.winjay.practice.media.media_list.MusicListActivity;
import com.winjay.practice.media.media_list.VideoListActivity;
import com.winjay.practice.media.music.MusicPlayActivity;
import com.winjay.practice.media.projection.MediaProjectionActivity;
import com.winjay.practice.media.audio_record.AudioRecordActivity;
import com.winjay.practice.media.video.VideoPlayActivity;
import com.winjay.practice.usb.UsbActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;

/**
 * 多媒体分类集合
 *
 * @author Winjay
 * @date 21/01/21
 */
public class MediaListActivity extends BaseActivity {
    @BindView(R.id.main_rv)
    RecyclerView main_rv;

    private LinkedHashMap<String, Class<?>> mainMap = new LinkedHashMap<String, Class<?>>() {
        {
            put("AudioRecorder", AudioRecordActivity.class);
            put("Camera", CameraListActivity.class);
            put("Image List", ImageListActivity.class);
            put("Music List", MusicListActivity.class);
            put("Video List", VideoListActivity.class);
            put("Play Music", MusicPlayActivity.class);
            put("Play Video", VideoPlayActivity.class);
            put("Exoplayer", ExoPlayerActivity.class);
            put("AudioFocusTest", AudioFocusTestActivity.class);
            put("USB", UsbActivity.class);
            put("MediaExtractor解析视频 And MediaMuxer封装视频", MediaExtractorAndMuxerActivity.class);
            put("MediaProjection截屏和录屏", MediaProjectionActivity.class);
            put("MediaCodec多媒体编解码器", MediaCodecActivity.class);
        }
    };

    @Override
    protected String[] permissions() {
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    protected int getLayoutId() {
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!hasPermissions()) {
            requestPermissions();
        }
        main_rv.setLayoutManager(new LinearLayoutManager(this));
        MainAdapter mainAdapter = new MainAdapter(new ArrayList<>(mainMap.keySet()));
        main_rv.setAdapter(mainAdapter);
        mainAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String key) {
                Intent intent = new Intent(MediaListActivity.this, mainMap.get(key));
                startActivity(intent);
            }
        });
    }
}