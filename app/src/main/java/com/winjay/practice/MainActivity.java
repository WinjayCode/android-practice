package com.winjay.practice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Printer;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.g2048.activity.Game2048Activity;
import com.winjay.practice.activity_manager.ActivityManagerActivity;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.content_provider.ProviderActivity;
import com.winjay.practice.design_mode.DesignModeActivity;
import com.winjay.practice.directory_structure.DirectoryStructureActivity;
import com.winjay.practice.download_manager.DownloadManagerActivity;
import com.winjay.practice.ioc.IOCActivity;
import com.winjay.practice.ipc.aidl.BookManagerClientActivity;
import com.winjay.practice.kotlin.KotlinTestActivity;
import com.winjay.practice.location.LocationActivity;
import com.winjay.practice.media.ModuleMediaListActivity;
import com.winjay.practice.notification.NotificationActivity;
import com.winjay.practice.package_manager.PackageManagerActivity;
import com.winjay.practice.plugin.PluginActivity;
import com.winjay.practice.so.SOActivity;
import com.winjay.practice.system_info.SystemInfoActivity;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.ui.app_compat_text.AppCompatTextActivity;
import com.winjay.practice.ui.cardview.CardViewActivity;
import com.winjay.practice.ui.constraint_layout.ConstraintLayoutActivity;
import com.winjay.practice.ui.custom_view.CustomViewActivity;
import com.winjay.practice.ui.material_design.MaterialDesignActivity;
import com.winjay.practice.ui.surfaceview_animation.SurfaceViewAnimationActivity;
import com.winjay.practice.ui.svg.SVGActivity;
import com.winjay.practice.ui.toolbar.ToolbarActivity;
import com.winjay.practice.ui.viewpager_fragment.ViewPagerActivity;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.websocket.WebsocketTest;
import com.winjay.puzzle.activity.PuzzleMainActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;

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
            put("Media", ModuleMediaListActivity.class);
            put("Plugin", PluginActivity.class);
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
            put("EmptyPage", EmptyActivity.class);
            put("SurfaceViewAnimation", SurfaceViewAnimationActivity.class);
            put("ViewPager+Fragment", ViewPagerActivity.class);
            put("IOC", IOCActivity.class);
            put("IPC", BookManagerClientActivity.class);
            put("CustomView", CustomViewActivity.class);
            put("SystemInfo", SystemInfoActivity.class);
            put("PackageManager", PackageManagerActivity.class);
            put("ActivityManager", ActivityManagerActivity.class);
            put("MaterialDesign", MaterialDesignActivity.class);
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
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate()");
        main_rv.setLayoutManager(new LinearLayoutManager(this));
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
        LogUtil.d(TAG, "onResume()");

//        Intent intent = new Intent(this, TestService.class);
//        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy()");
    }
}
