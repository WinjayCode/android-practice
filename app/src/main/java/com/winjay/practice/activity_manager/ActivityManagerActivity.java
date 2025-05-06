package com.winjay.practice.activity_manager;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Debug;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * ActivityManager学习
 *
 * @author Winjay
 * @date 2020/9/16
 */
public class ActivityManagerActivity extends BaseActivity {
    private static final String TAG = ActivityManagerActivity.class.getSimpleName();
    private ActivityManager mActivityManager;

    RecyclerView app_process_rv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manager_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        app_process_rv = findViewById(R.id.app_process_rv);
        app_process_rv.setLayoutManager(new LinearLayoutManager(this));
        app_process_rv.setAdapter(new AppProcessAdapter(this, getRunningProcessInfo()));

        HandlerManager.getInstance().postDelayedOnMainThread(testRunnable, 2000);
    }

    private Runnable testRunnable = new Runnable() {
        @Override
        public void run() {
            getRunningProcessInfo();
            HandlerManager.getInstance().postDelayedOnMainThread(testRunnable, 2000);
        }
    };

    private List<AMProcessInfo> getRunningProcessInfo() {
        List<AMProcessInfo> amProcessInfoList = new ArrayList<>();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = mActivityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : runningAppProcesses) {
            AMProcessInfo amProcessInfo = new AMProcessInfo();
            amProcessInfo.setPid("Pid:" + appProcessInfo.pid);
            amProcessInfo.setUid("Uid:" + appProcessInfo.uid);
            LogUtil.d(TAG, "processName=" + appProcessInfo.processName);
            amProcessInfo.setProcessName(appProcessInfo.processName);
            Debug.MemoryInfo[] processMemoryInfo = mActivityManager.getProcessMemoryInfo(new int[]{appProcessInfo.pid});
            amProcessInfo.setMemorySize("memsize:" + processMemoryInfo[0].getTotalPss() + "KB");

            amProcessInfoList.add(amProcessInfo);
        }
        return amProcessInfoList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HandlerManager.getInstance().getmMainHandler().removeCallbacks(testRunnable);
    }
}
