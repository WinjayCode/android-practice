package com.winjay.practice.utils;

import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtils {
    private static String TAG = "URLUtils";

    /**
     * 或者指定url的域名
     *
     * @param path
     * @return
     */
    public static String getHost(String path) {
        String host = null;
        String key = null;
        try {
            //            URL url = new URL(path);
            //            host = "http://" + url.getHost();

            URL url = new URL(path);
            host = url.getHost();
            if (!TextUtils.isEmpty(host)) {
                String[] splits = path.split(host);
                if (splits != null && splits.length == 2) {
                    key = splits[1];
                }
                host = url.getProtocol() + "://" + host;
                if (key.startsWith(":")) {
                    int index = key.indexOf("/");
                    String port = key.substring(0, index);
                    host += port;
                    LogUtil.d(TAG, "该url以:开头 port = " + port);
                }
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "host = " + host);
        return host;
    }


    /**
     * 获取指定域名后的参数串
     *
     * @param path
     * @return
     */
    public static String getUrl(String path) {
        String key = null;
        try {
            URL url = new URL(path);
            String host = url.getHost();
            if (!TextUtils.isEmpty(host)) {
                String[] splits = path.split(host);
                if (splits != null && splits.length == 2) {
                    key = splits[1];
                }

                if (key.startsWith(":")) {
                    LogUtil.d(TAG, "该url以:开头");
                    int index = key.indexOf("/");
                    key = key.substring(index);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "key = " + key);
        return key;
    }
}
