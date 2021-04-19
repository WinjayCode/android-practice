package com.winjay.practice.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;

/**
 * 多媒体工具类
 *
 * @author Winjay
 * @date 2021-04-07
 */
public class MultimediaUtil {
    private static final String TAG = MultimediaUtil.class.getSimpleName();

    /**
     * 通知android媒体库更新文件夹
     *
     * @param context
     * @param filePaths 文件绝对路径数组集合
     */
    public static void scanFiles(Context context, String[] filePaths) {
        try {
            MediaScannerConnection.scanFile(context, filePaths, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d(TAG, "path=" + path);
                            Log.d(TAG, "uri=" + uri);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知系统数据库刷新单个文件
     *
     * @param context
     * @param path    文件绝对路径（带文件名）
     */
    public static void scanFile(Context context, String path) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
    }
}
