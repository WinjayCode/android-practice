package com.winjay.practice.utils;


import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

public class MediaUtil {
    private static final String TAG = MediaUtil.class.getSimpleName();

    /**
     * 通知android媒体库更新文件夹
     */
    public static void scanFile(Context context, String dirPath) {
        try {
            File[] listFiles = new File(dirPath).listFiles();
            if (listFiles != null) {
                String[] paths = new String[listFiles.length];
                for (int i = 0; i < listFiles.length; i++) {
                    paths[i] = listFiles[i].getAbsolutePath();
                }
                LogUtil.d(TAG, "files=" + Arrays.asList(paths));
                MediaScannerConnection.scanFile(context, paths, new String[]{"audio/*", "video/*"},
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                LogUtil.d(TAG, "path=" + path);
                                LogUtil.d(TAG, "uri=" + uri);
                                Toast.makeText(context, "scan path=" + path, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeFile(Context context, String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    int res = context.getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.Video.Media.DATA + "= \"" + file.getAbsolutePath() + "\"",
                            null);
                    if (res > 0) {
                        LogUtil.d(TAG, "remove success!");
                    } else {
                        LogUtil.w(TAG, "remove failure!");
                    }
                }
            }
        }
    }

    public static String formatDuration(long milliseconds) {
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;

        String time;
        if (hours > 0) {
            time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            time = String.format("%02d:%02d", minutes, seconds);
        }

        return time;
    }
}
