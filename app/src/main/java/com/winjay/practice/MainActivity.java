package com.winjay.practice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.winjay.practice.activity_manager.ActivityManagerActivity;
import com.winjay.practice.app_compat_text.AppCompatTextActivity;
import com.winjay.practice.audio.AudioRecordActivity;
import com.winjay.practice.camera.CameraActivity;
import com.winjay.practice.cardview.CardViewActivity;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.constraint_layout.ConstraintLayoutActivity;
import com.winjay.practice.content_provider.ProviderActivity;
import com.winjay.practice.custom_view.CustomViewActivity;
import com.winjay.practice.design_mode.DesignModeActivity;
import com.winjay.practice.directory_structure.DirectoryStructureActivity;
import com.winjay.practice.download_manager.DownloadManagerActivity;
import com.winjay.practice.exoplayer.ExoPlayerActivity;
import com.winjay.practice.ioc.IOCActivity;
import com.winjay.practice.kotlin.KotlinTestActivity;
import com.winjay.practice.location.LocationActivity;
import com.winjay.practice.material_design.MaterialDesignActivity;
import com.winjay.practice.notification.NotificationActivity;
import com.winjay.practice.package_manager.PackageManagerActivity;
import com.winjay.practice.so.SOActivity;
import com.winjay.practice.surfaceview_animation.SurfaceViewAnimationActivity;
import com.winjay.practice.svg.SVGActivity;
import com.winjay.practice.system_info.SystemInfoActivity;
import com.winjay.practice.toolbar.ToolbarActivity;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.viewpager_fragment.ViewPagerActivity;
import com.winjay.practice.websocket.WebsocketTest;
import com.winjay.puzzle.activity.PuzzleMainActivity;

import butterknife.OnClick;

/**
 * @author Winjay
 * @date 2019/3/21
 */
public class MainActivity extends BaseActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate()");
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
    @OnClick(R.id.websocket)
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
    @OnClick(R.id.so_use)
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
