package com.winjay.mirrorcast.util;

import android.content.Context;
import android.os.Build;
import android.os.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Winjay
 * @date 2023-04-21
 */
public class FileUtil {
    public static void copyAssetsFile(Context context, String fileName, String destPath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                inputStream = context.getAssets().open(fileName);
                outputStream = new FileOutputStream(destPath);
                if (inputStream != null) {
                    FileUtils.copy(inputStream, outputStream);
                    inputStream.close();
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
