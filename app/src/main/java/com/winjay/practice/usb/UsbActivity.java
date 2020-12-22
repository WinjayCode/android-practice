package com.winjay.practice.usb;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.bean.VideoBean;
import com.winjay.practice.media.video.VideoActivity;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * USB多媒体文件扫描
 *
 * @author Winjay
 * @date 2020/12/4
 */
public class UsbActivity extends BaseActivity {
    private static final String TAG = UsbActivity.class.getSimpleName();
    private static final String INTERNAL_STORAGE_PATH = "/storage/emulated/0/";
    private MediaStoreChangeObserver mMediaStoreChangeObserver;

    @BindView(R.id.video_rv)
    RecyclerView mVideoRV;

    private MediaListAdapter mMediaListAdapter;
    private List<VideoBean> mListData;

    @Override
    protected int getLayoutId() {
        return R.layout.usb_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListData = new ArrayList<>();
        registerObserver();
        initView();
        scanFile();
    }

    private void initView() {
        mVideoRV.setLayoutManager(new GridLayoutManager(this, 3));
        mMediaListAdapter = new MediaListAdapter(this);
        mVideoRV.setAdapter(mMediaListAdapter);
        mMediaListAdapter.setOnItemClickListener(new MediaListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoBean videoBean) {
                Intent intent = new Intent(UsbActivity.this, VideoActivity.class);
                intent.putExtra("video", videoBean);
                startActivity(intent);
            }
        });
    }

    private void registerObserver() {
        if (mMediaStoreChangeObserver == null) {
            mMediaStoreChangeObserver = new MediaStoreChangeObserver();
            getContentResolver().registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, false, mMediaStoreChangeObserver);
        }
    }

    private void unRegisterObserver() {
        if (mMediaStoreChangeObserver != null) {
            getContentResolver().unregisterContentObserver(mMediaStoreChangeObserver);
            mMediaStoreChangeObserver = null;
        }
    }

    private class MediaStoreChangeObserver extends ContentObserver {

        public MediaStoreChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            LogUtil.d(TAG, "selfChange=" + selfChange + ", uri=" + uri.toString());
            if (uri.compareTo(MediaStore.Video.Media.EXTERNAL_CONTENT_URI) == 0) {
                scanFile();
            }
        }
    }

    private void scanFile() {
        String sortOrder;
        String[] cursorCols;

        cursorCols = new String[]{MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE};

        StringBuilder where = new StringBuilder();
        where.delete(0, where.length());
        where.append(MediaStore.Audio.Media.TITLE + " != ''");
        where.append(" AND (" + MediaStore.Audio.Media.MIME_TYPE + " LIKE '%video/%')");
        // 不读取内置sd卡
//            where.append(" AND (" + MediaStore.Audio.Media.DATA + " NOT LIKE '%" + INTERNAL_STORAGE_PATH + "%')");

        LogUtil.d(TAG, "where: " + where.toString());

        sortOrder = MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC, " + BaseColumns._ID + " DESC ";

        // video
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cursorCols, where.toString(), null, sortOrder);
        // audio
//        Cursor c = context.getContentResolver().query(uri, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//        while (c.moveToNext()) {
//            String[] columnNames = c.getColumnNames();
//            for (int i = 0; i < columnNames.length; i++) {
//                LogUtil.d(TAG, columnNames[i] + "======" + c.getString(i));
//            }
//            LogUtil.d(TAG, "----------------" + c.getString(0));
//        }
//        c.close();

        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));

                String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
                long dateModified = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
                int orientation = 0;//cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));

                LogUtil.d(TAG, "path=" + path);

                VideoBean videoBean = new VideoBean();
                videoBean.setPath(path);
                videoBean.setTitle(title);
                videoBean.setDuration(duration);
                mListData.add(videoBean);

                cursor.moveToNext();
            }
        } else {
            LogUtil.d(TAG, "cursor size is null or zero");
            mListData.clear();
        }
        cursor.close();
        mMediaListAdapter.setData(mListData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
        unRegisterObserver();
    }
}
