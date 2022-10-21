package com.winjay.practice.usb;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.bean.AudioBean;
import com.winjay.practice.media.bean.VideoBean;
import com.winjay.practice.media.media_list.MusicListAdapter;
import com.winjay.practice.media.media_list.VideoListAdapter;
import com.winjay.practice.media.music.MusicPlayActivity;
import com.winjay.practice.media.video.VideoPlayActivity;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.UsbUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * USB多媒体文件扫描
 *
 * @author Winjay
 * @date 2020/12/4
 */
public class UsbActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = UsbActivity.class.getSimpleName();
//    private static final String INTERNAL_STORAGE_PATH = "/storage/emulated/0/";
    private static final String INTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath();
    private MediaStoreChangeObserver mMediaStoreChangeObserver;
    private final int RC_PERMISSION = 100;

    @BindView(R.id.video_rv)
    RecyclerView mVideoRV;

    @BindView(R.id.music_rv)
    RecyclerView mMusicRV;

    private VideoListAdapter mVideoListAdapter;
    private MusicListAdapter mMusicListAdapter;
    private List<VideoBean> mVideoListData;
    private List<AudioBean> mMusicListData;

    private USBDeviceReceiver usbDeviceReceiver;

    @Override
    protected int getLayoutId() {
        return R.layout.usb_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoListData = new ArrayList<>();
        mMusicListData = new ArrayList<>();
        registerUSBReceiver();
        registerObserver();
        initView();
        requiresPermissions();

        // 系统应用可以监听已连接的卷发生的变化。每当卷的状态发生变化时（包括正在检查或移除卷以及装载或卸载卷时），StorageEventListener 都会收到通知。
        // 此方法只能在正在运行的进程中使用。
//        StorageEventListener mStorageEventListener = new StorageEventListener() {
//            @Override
//            public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
//                if (isResumed()) {
//                    // take action here
//                }
//            }
//        };
//
//        StorageManager mStorageManager = context.getSystemService(StorageManager.class);
//        mStorageManager.registerListener(mStorageEventListener);
    }

    private void initView() {
        // video
        mVideoRV.setLayoutManager(new GridLayoutManager(this, 3));
        mVideoListAdapter = new VideoListAdapter(this);
        mVideoRV.setAdapter(mVideoListAdapter);
        mVideoListAdapter.setOnItemClickListener(new VideoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoBean videoBean) {
                Intent intent = new Intent(UsbActivity.this, VideoPlayActivity.class);
                intent.putExtra("video", videoBean);
                startActivity(intent);
            }
        });

        // music
        mMusicRV.setLayoutManager(new LinearLayoutManager(this));
        mMusicListAdapter = new MusicListAdapter(this);
        mMusicRV.setAdapter(mMusicListAdapter);
        mMusicListAdapter.setOnItemClickListener(new MusicListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AudioBean audioBean) {
                Intent intent = new Intent(UsbActivity.this, MusicPlayActivity.class);
                intent.putExtra("audio", audioBean);
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
                HandlerManager.getInstance().postOnSubThread(new Runnable() {
                    @Override
                    public void run() {
                        scanVideoFile();
                        scanMusicFile();
                    }
                });
            }
        }
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

        StringBuilder where = new StringBuilder();
        where.delete(0, where.length());
        where.append(MediaStore.Video.Media.TITLE + " != ''");
        where.append(" AND (" + MediaStore.Video.Media.MIME_TYPE + " LIKE '%video/%')");
        // 不读取内置sd卡
        where.append(" AND (" + MediaStore.Video.Media.DATA + " NOT LIKE '%" + INTERNAL_STORAGE_PATH + "%')");
        LogUtil.d(TAG, "where: " + where.toString());

        sortOrder = MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC, " + BaseColumns._ID + " DESC ";

        // video
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cursorCols, where.toString(), null, sortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
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

                cursor.moveToNext();
            }
            cursor.close();
        } else {
            LogUtil.d(TAG, "cursor size is null or zero");
            mVideoListData.clear();
        }
        mVideoListAdapter.setData(mVideoListData);
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

        StringBuilder where = new StringBuilder();
        where.delete(0, where.length());
        where.append(MediaStore.Audio.Media.TITLE + " != ''");
        where.append(" AND (" + MediaStore.Audio.Media.MIME_TYPE + " LIKE '%audio/%')");
        // 不读取内置sd卡
        where.append(" AND (" + MediaStore.Audio.Media.DATA + " NOT LIKE '%" + INTERNAL_STORAGE_PATH + "%')");
        LogUtil.d(TAG, "where: " + where.toString());

        sortOrder = MediaStore.Audio.AudioColumns.DATE_TAKEN + " DESC, " + BaseColumns._ID + " DESC ";

        // audio
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorCols, where.toString(), null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
                long dateModified = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED));
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
        mMusicListAdapter.setData(mMusicListData);
    }

    private void registerUSBReceiver() {
        if (usbDeviceReceiver == null) {
            usbDeviceReceiver = new USBDeviceReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addAction(UsbUtil.ACTION_USB_PERMISSION);
        intentFilter.addAction(VolumeInfo.ACTION_VOLUME_STATE_CHANGED);
        registerReceiver(usbDeviceReceiver, intentFilter);
    }

    private void unregisterUSBReceiver() {
        if (usbDeviceReceiver != null) {
            unregisterReceiver(usbDeviceReceiver);
            usbDeviceReceiver = null;
        }
    }

    @AfterPermissionGranted(RC_PERMISSION)
    private void requiresPermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            LogUtil.d(TAG, "Already have permission, scan files!");
            // Already have permission, do the thing
            HandlerManager.getInstance().postOnSubThread(new Runnable() {
                @Override
                public void run() {
                    scanVideoFile();
                    scanMusicFile();
                }
            });
        } else {
            LogUtil.w(TAG, "Do not have permissions, request them now!");
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "请授予权限，否则影响部分使用功能。", RC_PERMISSION, perms);
            // 定制弹窗
//            EasyPermissions.requestPermissions(
//                    new PermissionRequest.Builder(this, RC_PERMISSION, perms)
//                            .setRationale("请授予权限，否则影响部分使用功能。1")
//                            .setPositiveButtonText("确定1")
//                            .setNegativeButtonText("取消1")
////                            .setTheme(R.style.my_fancy_style)
//                            .build());
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
        // Some permissions have been granted
        // ...
        // 已经实现注解方法，可以不用实现该方法
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        LogUtil.w(TAG, "Some permissions have been denied: " + perms.toString());
        // Some permissions have been denied
        // ...
        // 已经实现注解方法，可以不用实现该方法
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
        unRegisterObserver();
        unregisterUSBReceiver();
    }

}
