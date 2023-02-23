package com.winjay.practice.ui.remote_views;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.widget.RemoteViews;

import com.winjay.practice.R;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.BitmapUtil;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.ToastUtil;

/**
 * RemoteViews学习
 * 主要用在通知栏和桌面小部件
 *
 * @author Winjay
 * @date 2021-11-19
 */
public class RemoteViewsAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "RemoteViewsAppWidgetProvider";
    public static final String CLICK_ACTION = "com.winjay.practice.action.CLICK";

    public RemoteViewsAppWidgetProvider() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        LogUtil.d(TAG, "action=" + intent.getAction());
        // 这里判断是自己的action，做自己的事情，比如小部件被单击了要干什么，这里是做了一个动画效果
        if (intent.getAction().equals(CLICK_ACTION)) {
            ToastUtil.show(context, "clicked it");

            HandlerManager.getInstance().postOnSubThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap srcBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.kui_icon);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    for (int i = 0; i < 37; i++) {
                        float degree = (i * 10) % 360;
                        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                                R.layout.remote_views_widget);
                        remoteViews.setImageViewBitmap(R.id.widget_iv, BitmapUtil.rotateBitmap(
                                srcBitmap, degree));

//                        Intent intentClick = new Intent(CLICK_ACTION);
//                        intentClick.setFlags(0x01000000);
//                        intentClick.setPackage(context.getPackageName());
//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
//                                intentClick, 0);
//                        remoteViews.setOnClickPendingIntent(R.id.widget_iv, pendingIntent);

                        appWidgetManager.updateAppWidget(
                                new ComponentName(context, RemoteViewsAppWidgetProvider.class),
                                remoteViews);
                        SystemClock.sleep(30);
                    }
                }
            });
        }
    }

    /**
     * 每次桌面小部件更新时都调用一次该方法
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final int counter = appWidgetIds.length;
        LogUtil.d(TAG, "counter=" + counter);
        for (int appWidgetId : appWidgetIds) {
            onWidgetUpdate(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * 桌面小部件更新
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     */
    private void onWidgetUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        LogUtil.d(TAG, "appWidgetId=" + appWidgetId);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.remote_views_widget);

        Intent intentClick = new Intent(CLICK_ACTION);
        intentClick.setFlags(0x01000000);
        intentClick.setPackage(context.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intentClick, PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.widget_iv, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        LogUtil.d(TAG);
    }
}
