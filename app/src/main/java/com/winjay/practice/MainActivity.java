package com.winjay.practice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.g2048.activity.Game2048Activity;
import com.winjay.practice.activity_manager.ActivityManagerActivity;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.ui.app_compat_text.AppCompatTextActivity;
import com.winjay.practice.audio.AudioRecordActivity;
import com.winjay.practice.camera.CameraActivity;
import com.winjay.practice.ui.cardview.CardViewActivity;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.ui.constraint_layout.ConstraintLayoutActivity;
import com.winjay.practice.content_provider.ProviderActivity;
import com.winjay.practice.ui.custom_view.CustomViewActivity;
import com.winjay.practice.design_mode.DesignModeActivity;
import com.winjay.practice.directory_structure.DirectoryStructureActivity;
import com.winjay.practice.download_manager.DownloadManagerActivity;
import com.winjay.practice.exoplayer.ExoPlayerActivity;
import com.winjay.practice.ioc.IOCActivity;
import com.winjay.practice.ipc.aidl.BookManagerClientActivity;
import com.winjay.practice.kotlin.KotlinTestActivity;
import com.winjay.practice.location.LocationActivity;
import com.winjay.practice.ui.material_design.MaterialDesignActivity;
import com.winjay.practice.notification.NotificationActivity;
import com.winjay.practice.package_manager.PackageManagerActivity;
import com.winjay.practice.so.SOActivity;
import com.winjay.practice.ui.surfaceview_animation.SurfaceViewAnimationActivity;
import com.winjay.practice.ui.svg.SVGActivity;
import com.winjay.practice.system_info.SystemInfoActivity;
import com.winjay.practice.ui.toolbar.ToolbarActivity;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.ui.viewpager_fragment.ViewPagerActivity;
import com.winjay.practice.websocket.WebsocketTest;
import com.winjay.puzzle.activity.PuzzleMainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Winjay
 * @date 2019/3/21
 */
public class MainActivity extends BaseActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.main_rv)
    RecyclerView main_rv;

    private LinkedHashMap<String, Class<?>> mainMap = new LinkedHashMap<String, Class<?>>() {
        {
            put("WebSocket", null);
            put("so_use", SOActivity.class);
            put("kotlin", KotlinTestActivity.class);
            put("contentProvider", ProviderActivity.class);
            put("ConstrainLayout", ConstraintLayoutActivity.class);
            put("Location", LocationActivity.class);
            put("Intent_Filter", null);
            put("CardView", CardViewActivity.class);
            put("AppCompatTextView", AppCompatTextActivity.class);
            put("DownloadManager", DownloadManagerActivity.class);
            put("Directory Structure", DirectoryStructureActivity.class);
            put("Design Mode", DesignModeActivity.class);
            put("AudioRecorder", AudioRecordActivity.class);
            put("EmptyPage", EmptyActivity.class);
            put("SurfaceViewAnimation", SurfaceViewAnimationActivity.class);
            put("ViewPager+Fragment", ViewPagerActivity.class);
            put("IOC", IOCActivity.class);
            put("IPC", BookManagerClientActivity.class);
            put("exoplayer", ExoPlayerActivity.class);
            put("CustomView", CustomViewActivity.class);
            put("SystemInfo", SystemInfoActivity.class);
            put("PackageManager", PackageManagerActivity.class);
            put("ActivityManager", ActivityManagerActivity.class);
            put("MaterialDesign", MaterialDesignActivity.class);
            put("Camera", CameraActivity.class);
            put("Toolbar", ToolbarActivity.class);
            put("Notification", NotificationActivity.class);
            put("SVG", SVGActivity.class);
            put("Puzzle", PuzzleMainActivity.class);
            put("2048", Game2048Activity.class);
            put("Test", TestActivity.class);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.main_activity2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate()");
        main_rv.setLayoutManager(new LinearLayoutManager(this));
//        List<String> list = new ArrayList<>();
//        for (String key : mainMap.keySet()) {
//            list.add(key);
//        }
//        list.addAll(mainMap.keySet());
        MainAdapter mainAdapter = new MainAdapter(new ArrayList<>(mainMap.keySet()));
        main_rv.setAdapter(mainAdapter);
        mainAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String key) {
                switch (key) {
                    case "WebSocket":
                        HandlerManager.getInstance().postOnSubThread(new Runnable() {
                            @Override
                            public void run() {
                                WebsocketTest websocketTest = new WebsocketTest();
                                websocketTest.startServer();
                                websocketTest.startClient();
                            }
                        });
                        break;
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
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume()");

//        Intent intent = new Intent(this, TestService.class);
//        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy()");
    }

    /**
     * Websocket
     */
//    @OnClick(R.id.websocket)
    void websocketTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WebsocketTest websocketTest = new WebsocketTest();
                websocketTest.startServer();
                websocketTest.startClient();
            }
        }).start();
    }

    /**
     * so库的使用
     */
//    @OnClick(R.id.so_use)
    void soTest() {
        Intent intent = new Intent(this, SOActivity.class);
        startActivity(intent);
    }

    /**
     * Kotlin
     *
     * @param view
     */
    public void kotlinTest(View view) {
        Intent intent = new Intent(this, KotlinTestActivity.class);
        startActivity(intent);
    }

    /**
     * ContentProvider
     *
     * @param view
     */
    public void provider(View view) {
        Intent intent = new Intent(this, ProviderActivity.class);
        startActivity(intent);
    }

    /**
     * ConstrainLayout
     *
     * @param view
     */
    public void constrainLayout(View view) {
        Intent intent = new Intent(this, ConstraintLayoutActivity.class);
        startActivity(intent);
    }

    /**
     * Location
     *
     * @param view
     */
    public void location(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    /**
     * 隐式启动
     *
     * @param view
     */
    public void intentFilter(View view) {
        Intent intent = new Intent();
        intent.setAction("com.winjay.practice.action_1");
        intent.addCategory("com.winjay.practice.category_1");
        intent.setDataAndType(Uri.parse("file://abc"), "text/plain");
        startActivity(intent);
    }

    /**
     * CardView
     *
     * @param view
     */
    public void cardview(View view) {
        Intent intent = new Intent(this, CardViewActivity.class);
        startActivity(intent);
        // 使用Activity过渡动画
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        // 使用单个共享元素过渡动画
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, view, "share").toBundle());
        // 使用多个共享元素过渡动画
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(view, "share")).toBundle());
    }

    /**
     * AppCompatTextView
     *
     * @param view
     */
    public void textview(View view) {
        Intent intent = new Intent(this, AppCompatTextActivity.class);
        startActivity(intent);
    }

    /**
     * DownloadManager
     *
     * @param view
     */
    public void download(View view) {
        Intent intent = new Intent(this, DownloadManagerActivity.class);
        startActivity(intent);
    }

    /**
     * android本地文件目录
     *
     * @param view
     */
    public void directory(View view) {
        Intent intent = new Intent(this, DirectoryStructureActivity.class);
        startActivity(intent);
    }

    /**
     * Android架构模式
     *
     * @param view
     */
    public void designmode(View view) {
        Intent intent = new Intent(this, DesignModeActivity.class);
        startActivity(intent);
    }

    /**
     * 录音机
     *
     * @param view
     */
    public void audioRecorder(View view) {
        Intent intent = new Intent(this, AudioRecordActivity.class);
        startActivity(intent);
    }

    private long startActivityTime;

    /**
     * 空白页面
     *
     * @param view
     */
    public void emptyPage(View view) {
        startActivityTime = System.currentTimeMillis();
        LogUtil.d(TAG, "startActivity() time=" + startActivityTime);
        Intent intent = new Intent(this, EmptyActivity.class);
        intent.putExtra("time", System.currentTimeMillis());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * ViewPager+Fragment
     *
     * @param view
     */
    public void viewPager(View view) {
        Intent intent = new Intent(this, ViewPagerActivity.class);
        startActivity(intent);
    }

    /**
     * IOC-Test
     *
     * @param view
     */
    public void iocTest(View view) {
        Intent intent = new Intent(this, IOCActivity.class);
        startActivity(intent);
    }

    /**
     * IPC
     */
//    @OnClick(R.id.ipc)
    void ipc() {
        Intent intent = new Intent(this, BookManagerClientActivity.class);
        startActivity(intent);
    }

    /**
     * exoplayer
     *
     * @param view
     */
    public void exoplayer(View view) {
        Intent intent = new Intent(this, ExoPlayerActivity.class);
        startActivity(intent);
    }

    /**
     * customView
     *
     * @param view
     */
    public void customView(View view) {
        Intent intent = new Intent(this, CustomViewActivity.class);
        startActivity(intent);
    }

    /**
     * SystemInfo
     *
     * @param view
     */
    public void systemInfo(View view) {
        Intent intent = new Intent(this, SystemInfoActivity.class);
        startActivity(intent);
    }

    /**
     * PackageManager
     *
     * @param view
     */
    public void packageManager(View view) {
        Intent intent = new Intent(this, PackageManagerActivity.class);
        startActivity(intent);
    }

    /**
     * ActivityManager
     *
     * @param view
     */
    public void activityManager(View view) {
        Intent intent = new Intent(this, ActivityManagerActivity.class);
        startActivity(intent);
    }

    /**
     * Material Design
     *
     * @param view
     */
    public void materialDesign(View view) {
        Intent intent = new Intent(this, MaterialDesignActivity.class);
        startActivity(intent);
    }

    /**
     * Camera
     *
     * @param view
     */
    public void camera(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    /**
     * Toolbar
     *
     * @param view
     */
    public void toolbar(View view) {
        Intent intent = new Intent(this, ToolbarActivity.class);
        startActivity(intent);
    }

    /**
     * Notification
     *
     * @param view
     */
    public void notification(View view) {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    /**
     * SVG
     *
     * @param view
     */
    public void svg(View view) {
        Intent intent = new Intent(this, SVGActivity.class);
        startActivity(intent);
    }

    /**
     * Puzzle
     *
     * @param view
     */
    public void puzzle(View view) {
        Intent intent = new Intent(this, PuzzleMainActivity.class);
        startActivity(intent);
    }

    /**
     * 2048
     *
     * @param view
     */
    public void g2048(View view) {
        Intent intent = new Intent(this, Game2048Activity.class);
        startActivity(intent);
    }

    public void test(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);

//        AccountManager accountManager = AccountManager.get(this);
//        Account[] accounts = accountManager.getAccounts();
//        for (int i = 0; i < accounts.length; i++) {
//            Log.d("dwj", "info=" + accounts[i].toString());
//            Log.d("dwj", "psd=" + accountManager.getPassword(accounts[i]));
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause() cost time=" + (System.currentTimeMillis() - startActivityTime));
    }

    /**
     * SurfaceViewAnimation
     *
     * @param view
     */
    public void surfaceViewAnim(View view) {
        Intent intent = new Intent(this, SurfaceViewAnimationActivity.class);
        startActivity(intent);
    }
}
