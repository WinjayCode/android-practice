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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * ActivityManager学习
 *
 * @author Winjay
 * @date 2020/9/16
 */
public class ActivityManagerActivity extends BaseActivity {
    private ActivityManager mActivityManager;

    @BindView(R.id.app_process_rv)
    RecyclerView app_process_rv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manager_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        app_process_rv.setLayoutManager(new LinearLayoutManager(this));
        app_process_rv.setAdapter(new AppProcessAdapter(this, getRunningProcessInfo()));
    }

    private List<AMProcessInfo> getRunningProcessInfo() {
        List<AMProcessInfo> amProcessInfoList = new ArrayList<>();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = mActivityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : runningAppProcesses) {
            AMProcessInfo amProcessInfo = new AMProcessInfo();
            amProcessInfo.setPid("Pid:" + appProcessInfo.pid);
            amProcessInfo.setUid("Uid:" + appProcessInfo.uid);
            amProcessInfo.setProcessName(appProcessInfo.processName);
            Debug.MemoryInfo[] processMemoryInfo = mActivityManager.getProcessMemoryInfo(new int[]{appProcessInfo.pid});
            amProcessInfo.setMemorySize("memsize:" + processMemoryInfo[0].getTotalPss() + "KB");

            amProcessInfoList.add(amProcessInfo);
        }
        return amProcessInfoList;
    }
}