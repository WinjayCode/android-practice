package com.winjay.practice.utils;


import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

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
            String[] paths = new String[listFiles.length];
            for (int i = 0; i < listFiles.length; i++) {
                paths[i] = listFiles[i].getAbsolutePath();
            }
            Log.d(TAG, "files=" + Arrays.asList(paths));
            MediaScannerConnection.scanFile(context, paths, null,
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
}
