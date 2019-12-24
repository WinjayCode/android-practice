package com.winjay.practice.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

/**
 * 闹钟提醒
 *
 * @author Winjay
 * @date 2019-10-23
 */
public class AlarmActivity extends BaseActivity {
    private final String TAG = AlarmActivity.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.alarm_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void setAlarm(int period) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent("自己定义的action名字");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + period * 1000, period * 1000, pendingIntent);
        //这里我设置的是重复闹醒 LZ也可以用  alarmManager.set(type, triggerAtTime, operation)；
        //来设置单次闹醒
        LogUtil.d(TAG, System.currentTimeMillis() + " " + period * 1000);
    }

    //取消唤醒闹铃
    public void cancelAlarm() {
        Intent intent = new Intent("自己定义的action名字");
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.cancel(sender);
    }
}
