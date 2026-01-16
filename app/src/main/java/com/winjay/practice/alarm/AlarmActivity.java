package com.winjay.practice.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

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
    private PendingIntent mPendingIntent;
    private AlarmManager mAlarmManager;

    @Override
    protected int getLayoutId() {
        return R.layout.alarm_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        requestExactAlarmsPermission();
        Intent intent = new Intent(this, AlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        findViewById(R.id.set_alarm_btn).setOnClickListener(v -> {
            setAlarm();
        });
        findViewById(R.id.cancel_alarm_btn).setOnClickListener(v -> {
            cancelAlarm();
        });
    }

    public void requestExactAlarmsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!mAlarmManager.canScheduleExactAlarms()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("需要精确闹钟权限");
                builder.setMessage("请允许应用精确闹钟权限以继续使用功能");
                builder.setPositiveButton("去设置", (dialog, which) ->
                        goToExactAlarmsPermissionPage()
                );
                builder.show();
            }
        }
    }

    /**
     * 跳转到精确闹钟权限设置界面
     */
    public void goToExactAlarmsPermissionPage() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        startActivity(intent);
    }

    public void setAlarm() {
        cancelAlarm();
        // 设置精确闹钟
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (mAlarmManager.canScheduleExactAlarms()) {
                // 10s
                mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, mPendingIntent);
            }
        }

//        mAlarmManager.setRepeating(); // 设置重复闹钟
    }

    //取消唤醒闹铃
    public void cancelAlarm() {
        mAlarmManager.cancel(mPendingIntent);
    }
}
