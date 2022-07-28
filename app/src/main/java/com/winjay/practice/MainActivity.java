package com.winjay.practice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.g2048.activity.Game2048Activity;
import com.winjay.practice.activity_manager.ActivityManagerActivity;
import com.winjay.practice.bluetooth.BluetoothListActivity;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.content_provider.ProviderActivity;
import com.winjay.practice.crash.CrashTestActivity;
import com.winjay.practice.design_mode.DesignModeListActivity;
import com.winjay.practice.directory_structure.DirectoryStructureActivity;
import com.winjay.practice.download_manager.DownloadManagerActivity;
import com.winjay.practice.hardware_test.HardwareTestListActivity;
import com.winjay.practice.ioc.IOCActivity;
import com.winjay.practice.ipc.IPCListActivity;
import com.winjay.practice.jetpack.JetpackLibListActivity;
import com.winjay.practice.jni.JniTestActivity;
import com.winjay.practice.kotlin.KotlinListActivity;
import com.winjay.practice.location.LocationActivity;
import com.winjay.practice.media.ModuleMediaListActivity;
import com.winjay.practice.net.NetListActivity;
import com.winjay.practice.notification.NotificationActivity;
import com.winjay.practice.package_manager.PackageManagerActivity;
import com.winjay.practice.performance_optimize.PerformanceOptimizeActivity;
import com.winjay.practice.plugin.PluginActivity;
import com.winjay.practice.system_info.SystemInfoActivity;
import com.winjay.practice.ui.UIListActivity;
import com.winjay.practice.utils.LogUtil;
import com.winjay.puzzle.activity.PuzzleMainActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;

/**
 * @author Winjay
 * @date 2019/3/21
 */
public class MainActivity extends BaseActivity {
    private final String TAG = "MainActivity";

    @BindView(R.id.main_rv)
    RecyclerView main_rv;

    private LinkedHashMap<String, Class<?>> mainMap = new LinkedHashMap<String, Class<?>>() {
        {
            put("Jetpack", JetpackLibListActivity.class);
            put("UI", UIListActivity.class);
            put("Media", ModuleMediaListActivity.class);
            put("Bluetooth", BluetoothListActivity.class);
            put("Plugin", PluginActivity.class);
            put("Net", NetListActivity.class);
            put("JNI", JniTestActivity.class);
            put("Kotlin", KotlinListActivity.class);
            put("ContentProvider", ProviderActivity.class);
            put("Location", LocationActivity.class);
            put("Intent Filter", null);
            put("DownloadManager", DownloadManagerActivity.class);
            put("Directory Structure", DirectoryStructureActivity.class);
            put("Design Mode", DesignModeListActivity.class);
            put("IOC", IOCActivity.class);
            put("IPC", IPCListActivity.class);
            put("SystemInfo", SystemInfoActivity.class);
            put("PackageManager", PackageManagerActivity.class);
            put("ActivityManager", ActivityManagerActivity.class);
            put("Notification", NotificationActivity.class);
            put("Hardware Test", HardwareTestListActivity.class);
            put("Performance Optimization", PerformanceOptimizeActivity.class);
            put("Puzzle", PuzzleMainActivity.class);
            put("2048", Game2048Activity.class);
            put("Test", TestActivity.class);
            put("Crash Test", CrashTestActivity.class);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG);
        main_rv.setLayoutManager(new LinearLayoutManager(this));
        MainAdapter mainAdapter = new MainAdapter(new ArrayList<>(mainMap.keySet()));
        main_rv.setAdapter(mainAdapter);
        mainAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String key) {
                switch (key) {
                    case "Intent_Filter":
                        Intent it = new Intent();
                        it.setAction("com.winjay.practice.action_1");
                        it.addCategory("com.winjay.practice.category_1");
                        it.setDataAndType(Uri.parse("file://abc"), "text/plain");
                        startActivity(it);
                        break;
                    default:
                        LogUtil.d(TAG, "key=" + key + ", class=" + mainMap.get(key));
                        Intent intent = new Intent(MainActivity.this, mainMap.get(key));
                        startActivity(intent);

//                        // 使用Activity过渡动画
//                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
//                        // 使用单个共享元素过渡动画
//                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, view, "share").toBundle());
//                        // 使用多个共享元素过渡动画
//                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, Pair.create(view, "share")).toBundle());
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG);

//        Intent intent = new Intent(this, TestService.class);
//        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
    }
}
