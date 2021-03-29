package com.winjay.practice.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.URL;

public class NetUtil {
    private static final String TAG = NetUtil.class.getSimpleName();

    public static boolean networkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 以太网是否连接
     *
     * @param context
     * @return
     */
    public static boolean isEthernetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // TYPE_ETHERNET 以太网
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (context == null || networkInfo == null || !networkInfo.isAvailable()) {
            LogUtil.d(TAG, "111");
            return false;
        }
        if (networkInfo.getState() == NetworkInfo.State.CONNECTED || networkInfo.getState() == NetworkInfo.State.CONNECTING) {
            LogUtil.d(TAG, "222");
            return true;
        }
        LogUtil.d(TAG, "333");
        return false;
    }

    public static boolean isOnline() {
        try {
            new URL("https://www.baidu.com").openStream();
            return true;
        } catch (Exception unused) {
            return false;
        }
    }
}
