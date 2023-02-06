package com.winjay.practice;

import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.g2048.activity.Game2048Activity;
import com.winjay.practice.accessibility.AccessibilityServiceHelper;
import com.winjay.practice.accessibility.AutoClickAccessibilityService;
import com.winjay.practice.activity_manager.ActivityManagerActivity;
import com.winjay.practice.bluetooth.BluetoothListActivity;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.crash.CrashTestActivity;
import com.winjay.practice.design_mode.DesignModeListActivity;
import com.winjay.practice.download_manager.DownloadManagerActivity;
import com.winjay.practice.file_browser.FileBrowserActivity;
import com.winjay.practice.hardware_test.HardwareTestListActivity;
import com.winjay.practice.ioc.IOCActivity;
import com.winjay.practice.ipc.IPCListActivity;
import com.winjay.practice.jetpack.JetpackLibListActivity;
import com.winjay.practice.jni.JniTestActivity;
import com.winjay.practice.kotlin.KotlinListActivity;
import com.winjay.practice.location.LocationActivity;
import com.winjay.practice.media.MediaListActivity;
import com.winjay.practice.net.NetListActivity;
import com.winjay.practice.notification.NotificationActivity;
import com.winjay.practice.package_manager.PackageManagerActivity;
import com.winjay.practice.performance_optimize.PerformanceOptimizeActivity;
import com.winjay.practice.plugin.PluginActivity;
import com.winjay.practice.storage.StorageActivity;
import com.winjay.practice.system_info.SystemInfoActivity;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.ui.UIListActivity;
import com.winjay.practice.usb.USBDeviceReceiver;
import com.winjay.practice.usb.VolumeInfo;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.UsbUtil;
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

    private USBDeviceReceiver usbDeviceReceiver;

    private LinkedHashMap<String, Class<?>> mainMap = new LinkedHashMap<String, Class<?>>() {
        {
            put("Jetpack", JetpackLibListActivity.class);
            put("UI", UIListActivity.class);
            put("Media", MediaListActivity.class);
            put("Bluetooth", BluetoothListActivity.class);
            put("Plugin", PluginActivity.class);
            put("Net", NetListActivity.class);
            put("JNI", JniTestActivity.class);
            put("Kotlin", KotlinListActivity.class);
            put("Location", LocationActivity.class);
            put("Intent Filter", null);
            put("DownloadManager", DownloadManagerActivity.class);
            put("Storage", StorageActivity.class);
            put("Design Mode", DesignModeListActivity.class);
            put("IOC", IOCActivity.class);
            put("IPC", IPCListActivity.class);
            put("SystemInfo", SystemInfoActivity.class);
            put("PackageManager", PackageManagerActivity.class);
            put("ActivityManager", ActivityManagerActivity.class);
            put("Notification", NotificationActivity.class);
            put("Hardware Test", HardwareTestListActivity.class);
            put("File Browser", FileBrowserActivity.class);
            put("Performance Optimization", PerformanceOptimizeActivity.class);
            put("Puzzle", PuzzleMainActivity.class);
            put("2048", Game2048Activity.class);
            put("DLNA", com.winjay.dlna.MainActivity.class);
            put("Scrcpy_Android_2_Android", com.winjay.mirrorcast.MainActivity.class);
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
                    case "Intent Filter":
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



//                        ActivityOptions activityOptions = ActivityOptions.makeBasic();
//                        MediaRouter mediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
//                        MediaRouter.RouteInfo route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
//                        if (route != null) {
//                            Display presentationDisplay = route.getPresentationDisplay();
//                            LogUtil.d(TAG, "displayId=" + presentationDisplay.getDisplayId());
//                            Bundle bundle = activityOptions.setLaunchDisplayId(presentationDisplay.getDisplayId()).toBundle();
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent, bundle);
//                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG);
/*
        if (!AccessibilityServiceHelper.isServiceON(this, AutoClickAccessibilityService.class.getName())) {
            AccessibilityServiceHelper.requirePermission(this);
        }

        HandlerManager.getInstance().postDelayedOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.d(TAG, "main thread mock event");

//                    InjectUtil.click(90, 300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3000);

        HandlerManager.getInstance().postDelayedOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.d(TAG, "sub thread mock event");

//                    Instrumentation instrumentation = new Instrumentation();
//                    instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
//                    instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 100, 10, 0));
//                    instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 100, 10, 0));

//                    Util.slide(100, 10, 100, 100);
//                    InjectUtil.click(90, 300);

//                    InputManager inputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);

//                    CommandUtil.exec("adb shell input tap 90 200");

//                    AutoClickAccessibilityService.getInstance().onClick(90, 300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3000);
*/

//        Intent intent = new Intent(this, TestService.class);
//        startService(intent);

        registerUSBReceiver();
    }

    private void registerUSBReceiver() {
        if (usbDeviceReceiver == null) {
            usbDeviceReceiver = new USBDeviceReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addAction(UsbUtil.ACTION_USB_PERMISSION);
        intentFilter.addAction(VolumeInfo.ACTION_VOLUME_STATE_CHANGED);
        registerReceiver(usbDeviceReceiver, intentFilter);
    }

    private void unregisterUSBReceiver() {
        if (usbDeviceReceiver != null) {
            unregisterReceiver(usbDeviceReceiver);
            usbDeviceReceiver = null;
        }
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
        unregisterUSBReceiver();
    }
}
