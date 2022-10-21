package com.winjay.practice.media.media_list;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.bean.VideoBean;
import com.winjay.practice.media.video.VideoPlayActivity;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 视频列表
 *
 * @author Winjay
 * @date 2022/10/20
 */
public class VideoListActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "VideoListActivity";
    //    private static final String INTERNAL_STORAGE_PATH = "/storage/emulated/0/";
    private static final String INTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath();

    @BindView(R.id.video_rv)
    RecyclerView mVideoRV;

    private VideoListAdapter mVideoListAdapter;

    private List<VideoBean> mVideoListData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoListData = new ArrayList<>();
        initView();

        HandlerManager.getInstance().postOnSubThread(new Runnable() {
            @Override
            public void run() {
                scanVideoFile();
            }
        });
    }

    private void initView() {
        // video
        mVideoRV.setLayoutManager(new GridLayoutManager(this, 3));
        mVideoListAdapter = new VideoListAdapter(this);
        mVideoRV.setAdapter(mVideoListAdapter);
        mVideoListAdapter.setOnItemClickListener(new VideoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoBean videoBean) {
                Intent intent = new Intent(VideoListActivity.this, VideoPlayActivity.class);
                intent.putExtra("video", videoBean);
                startActivity(intent);
            }
        });
    }

    private void scanVideoFile() {
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

//        StringBuilder where = new StringBuilder();
//        where.delete(0, where.length());
//        where.append(MediaStore.Video.Media.TITLE + " != ''");
//        where.append(" AND (" + MediaStore.Video.Media.MIME_TYPE + " LIKE '%video/%')");
//        // 不读取内置sd卡
//        where.append(" AND (" + MediaStore.Video.Media.DATA + " NOT LIKE '%" + INTERNAL_STORAGE_PATH + "%')");
//        LogUtil.d(TAG, "where: " + where.toString());

        sortOrder = MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC, " + BaseColumns._ID + " DESC ";

        // video
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cursorCols, null, null, sortOrder);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                // 可针对特定光标项生成 URI：
//                Uri volumeAudioUri = MediaStore.Video.Media.getContentUri(volumeName);
//                Uri mediaUri = ContentUris.withAppendedId(volumeAudioUri, id);

                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                long dateModified = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));
                int orientation = 0;//cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));

                LogUtil.d(TAG, "path=" + path);

                VideoBean videoBean = new VideoBean();
                videoBean.setPath(path);
                videoBean.setTitle(title);
                videoBean.setDuration(duration);
                mVideoListData.add(videoBean);
            }
            cursor.close();
        } else {
            LogUtil.d(TAG, "cursor size is null or zero");
            mVideoListData.clear();
        }

        HandlerManager.getInstance().postOnMainThread(new Runnable() {
            @Override
            public void run() {
                mVideoListAdapter.setData(mVideoListData);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
    }
}
