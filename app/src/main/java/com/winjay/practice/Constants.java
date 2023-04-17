package com.winjay.practice;

/**
 * 常量集合
 *
 * @author Winjay
 * @date 2020-02-18
 */
public class Constants {
    public final static int MSG_FROM_CLIENT = 1;
    public final static int MSG_FROM_SERVICE = 2;

    // media
    /**
     * prev
     */
    public static final String ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PREV = "com.winjay.action.MEDIA_NOTIFICATION_PENDINGINTENT_PREV";
    /**
     * play
     */
    public static final String ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PLAY = "com.winjay.action.MEDIA_NOTIFICATION_PENDINGINTENT_PLAY";
    /**
     * pause
     */
    public static final String ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PAUSE = "com.winjay.action.MEDIA_NOTIFICATION_PENDINGINTENT_PAUSE";
    /**
     * next
     */
    public static final String ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_NEXT = "com.winjay.action.MEDIA_NOTIFICATION_PENDINGINTENT_NEXT";
    /**
     * close
     */
    public static final String ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_CLOSE = "com.winjay.action.MEDIA_NOTIFICATION_PENDINGINTENT_CLOSE";

    /**
     * 应用通知唯一 Channel ID
     */
    public static final String NOTIFICATION_CHANNEL_ID = AppApplication.getApplication().getPackageName();
    /**
     * 应用通知唯一 Channel Name
     */
    public static final String NOTIFICATION_CHANNEL_NAME = AppApplication.getApplication().getResources().getString(R.string.app_name);

    public static final int NOTIFICATION_ID = 100;

    //---------------------------------- net ----------------------------------
    public static final int SOCKET_PORT = 8080;
    //---------------------------------- net ----------------------------------
}
