package com.winjay.practice.media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.MainAdapter;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.record.AudioRecordActivity;
import com.winjay.practice.media.music.MusicActivity;
import com.winjay.practice.media.audio_focus.AudioFocusTestActivity;
import com.winjay.practice.media.camera.CameraActivity;
import com.winjay.practice.media.exoplayer.ExoPlayerActivity;
import com.winjay.practice.media.video.VideoActivity;
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
public class ModuleMediaListActivity extends BaseActivity {
    @BindView(R.id.main_rv)
    RecyclerView main_rv;

    private LinkedHashMap<String, Class<?>> mainMap = new LinkedHashMap<String, Class<?>>() {
        {
            put("Music", MusicActivity.class);
            put("Video", VideoActivity.class);
            put("AudioRecorder", AudioRecordActivity.class);
            put("Exoplayer", ExoPlayerActivity.class);
            put("AudioFocusTest", AudioFocusTestActivity.class);
            put("Camera", CameraActivity.class);
            put("USB", UsbActivity.class);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_rv.setLayoutManager(new LinearLayoutManager(this));
        MainAdapter mainAdapter = new MainAdapter(new ArrayList<>(mainMap.keySet()));
        main_rv.setAdapter(mainAdapter);
        mainAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String key) {
                Intent intent = new Intent(ModuleMediaListActivity.this, mainMap.get(key));
                startActivity(intent);
            }
        });
    }
}