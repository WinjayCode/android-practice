package com.winjay.practice.media.media_list;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.bean.AudioBean;
import com.winjay.practice.media.music.MusicPlayActivity;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 音频列表
 *
 * @author Winjay
 * @date 2022/10/20
 */
public class MusicListActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = MusicListActivity.class.getSimpleName();
//    private static final String INTERNAL_STORAGE_PATH = "/storage/emulated/0/";
    private static final String INTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath();

    @BindView(R.id.music_rv)
    RecyclerView mMusicRV;

    private MusicListAdapter mMusicListAdapter;
    private List<AudioBean> mMusicListData;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_music_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMusicListData = new ArrayList<>();
        initView();

        HandlerManager.getInstance().postOnSubThread(new Runnable() {
            @Override
            public void run() {
                scanMusicFile();
            }
        });
    }

    private void initView() {
        mMusicRV.setLayoutManager(new LinearLayoutManager(this));
        mMusicListAdapter = new MusicListAdapter(this);
        mMusicRV.setAdapter(mMusicListAdapter);
        mMusicListAdapter.setOnItemClickListener(new MusicListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AudioBean audioBean) {
                Intent intent = new Intent(MusicListActivity.this, MusicPlayActivity.class);
                intent.putExtra("audio", audioBean);
                startActivity(intent);
            }
        });
    }


    private void scanMusicFile() {
        String sortOrder;
        String[] cursorCols;

        cursorCols = new String[]{MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE};

//        StringBuilder where = new StringBuilder();
//        where.delete(0, where.length());
//        where.append(MediaStore.Audio.Media.TITLE + " != ''");
//        where.append(" AND (" + MediaStore.Audio.Media.MIME_TYPE + " LIKE '%audio/%')");
//        // 不读取内置sd卡
//        where.append(" AND (" + MediaStore.Audio.Media.DATA + " NOT LIKE '%" + INTERNAL_STORAGE_PATH + "%')");
//        LogUtil.d(TAG, "where: " + where.toString());
//
//        sortOrder = MediaStore.Audio.AudioColumns.DATE_TAKEN + " DESC, " + BaseColumns._ID + " DESC ";

        // audio
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, cursorCols, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
                long dateModified = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                int orientation = 0;//cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));

                LogUtil.d(TAG, "path=" + path);

                AudioBean audioBean = new AudioBean();
                audioBean.setPath(path);
                audioBean.setTitle(title);
                audioBean.setDuration(duration);
                audioBean.setDisplayName(displayName);
                mMusicListData.add(audioBean);

                cursor.moveToNext();
            }
            cursor.close();
        } else {
            LogUtil.d(TAG, "cursor size is null or zero");
            mMusicListData.clear();
        }

        HandlerManager.getInstance().postOnMainThread(new Runnable() {
            @Override
            public void run() {
                mMusicListAdapter.setData(mMusicListData);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
    }

}
