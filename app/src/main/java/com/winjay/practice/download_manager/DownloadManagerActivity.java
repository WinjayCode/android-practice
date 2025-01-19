package com.winjay.practice.download_manager;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * DownloadManager学习
 * <p>
 * 1.后台执行网络操作，开发者无需关注网络切换、存储文件失败等问题
 * 2.在通知栏中显示下载进度，不需要开发者自己实现通知栏中的下载进度条
 * 3.可以很方便地进行查询和删除任务的功能
 * <p>
 *
 * @author Winjay
 * @date 2019-12-26
 */
public class DownloadManagerActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private final String TAG = DownloadManagerActivity.class.getSimpleName();

    private DownloadManager mDownloadManager;
    private final String mDownloadUrl = "http://pic11.nipic.com/20101119/3320946_221711832717_2.jpg";
    private final String APK_URL = "http://sw.bos.baidu.com/sw-search-sp/software/19de58890ffb8/QQ_8.6.18804.0_setup.exe";
    private long mDownloadId;

    @BindView(R.id.img)
    ImageView mImg;

    private DownloadReceiver mDownloadReceiver;

    private final int RC_PERMISSION = 100;

    @Override
    protected int getLayoutId() {
        return R.layout.download_manager_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        registerReceiver();

        requiresPermissions();
    }

    @OnClick(R.id.download_btn)
    void download() {
        LogUtil.d(TAG);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mDownloadUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle("下载中...");
        // 设置外置sd卡下载存放目录
        request.setDestinationUri(Uri.fromFile(this.getExternalCacheDir()));

        mDownloadId = mDownloadManager.enqueue(request);

        mImg.postDelayed(new Runnable() {
            @Override
            public void run() {
                query();
            }
        }, 2000);
    }

    private void query() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(mDownloadId);
        Cursor cursor = mDownloadManager.query(query);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String bytesDownload = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                String description = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
                String id = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
                String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                String mimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                String status = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                String totalSize = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                LogUtil.d(TAG, "bytesDownload=" + bytesDownload);
                LogUtil.d(TAG, "description=" + description);
                LogUtil.d(TAG, "id=" + id);
                LogUtil.d(TAG, "localUri=" + localUri);
                LogUtil.d(TAG, "mimeType=" + mimeType);
                LogUtil.d(TAG, "title=" + title);
                LogUtil.d(TAG, "status=" + status);
                LogUtil.d(TAG, "totalSize=" + totalSize);
            }
        }
    }

    private void cancelDownload() {
        mDownloadManager.remove(mDownloadId);
    }

    private void registerReceiver() {
        if (mDownloadReceiver == null) {
            mDownloadReceiver = new DownloadReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
            registerReceiver(mDownloadReceiver, intentFilter, Context.RECEIVER_EXPORTED);
        }

    }

    private void unRegisterReceiver() {
        if (mDownloadReceiver != null) {
            unregisterReceiver(mDownloadReceiver);
            mDownloadReceiver = null;
        }

    }

    private class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.d(TAG, "action=" + action);
            if (TextUtils.equals(action, DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                Uri uri = mDownloadManager.getUriForDownloadedFile(mDownloadId);
                mImg.setImageURI(uri);
            } else if (TextUtils.equals(action, DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                Toast.makeText(DownloadManagerActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    ////////////////////////////////////////// permission //////////////////////////////////////////
    @AfterPermissionGranted(RC_PERMISSION)
    private void requiresPermissions() {
        String[] perms = {Manifest.permission.INTERNET};
        if (EasyPermissions.hasPermissions(this, perms)) {
            LogUtil.d(TAG, "Already have permission!");
            // Already have permission, do the thing
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
        unRegisterReceiver();
    }
}
