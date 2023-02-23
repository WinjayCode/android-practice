package com.winjay.practice.utils;

import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class URLUtil {
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

    /**
     * 获取Url的md5值
     *
     * @param url
     * @return
     */
    public static String hashKeyFormUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = ByteUtil.bytesToHex(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }
}
