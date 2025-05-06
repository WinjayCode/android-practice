package com.winjay.practice.package_manager;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * PackageManager学习
 *
 * @author Winjay
 * @date 2020/9/15
 */
public class PackageManagerActivity extends BaseActivity {
    private static final String TAG = PackageManagerActivity.class.getSimpleName();
    private PackageManager pm;
    private final static int ALL_APP = 1;
    private final static int SYSTEM_APP = 2;
    private final static int THIRD_APP = 3;
    private final static int SDCARD_APP = 4;

    RecyclerView app_list_rv;

    @Override
    protected int getLayoutId() {
        return R.layout.package_manager_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app_list_rv = findViewById(R.id.app_list_rv);
        pm = getPackageManager();
        app_list_rv.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.all_app_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allApp();
            }
        });
    }

    void allApp() {
        AppListAdapter adapter = new AppListAdapter(this, getAppInfo(ALL_APP));
        app_list_rv.setAdapter(adapter);
    }

//    @OnClick(R.id.system_app_btn)
//    void systemApp() {
//        AppListAdapter adapter = new AppListAdapter(this, getAppInfo(SYSTEM_APP));
//        app_list_rv.setAdapter(adapter);
//    }
//
//    @OnClick(R.id.third_app_btn)
//    void thirdApp() {
//        AppListAdapter adapter = new AppListAdapter(this, getAppInfo(THIRD_APP));
//        app_list_rv.setAdapter(adapter);
//    }
//
//    @OnClick(R.id.sdcard_app_btn)
//    void sdcardApp() {
//        AppListAdapter adapter = new AppListAdapter(this, getAppInfo(SDCARD_APP));
//        app_list_rv.setAdapter(adapter);
//    }

    private List<PMAppInfo> getAppInfo(int flag) {
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        List<PMAppInfo> appInfos = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolveInfos) {
            appInfos.add(makeAppInfo(resolveInfo));
        }
        return appInfos;
    }

    private PMAppInfo makeAppInfo(ResolveInfo resolveInfo) {
        PMAppInfo appInfo = new PMAppInfo();
        String appLabel = (String) resolveInfo.loadLabel(pm);
        LogUtil.d(TAG, "app name=" + appLabel);
        appInfo.setAppLabel(appLabel);
        appInfo.setAppIcon(resolveInfo.loadIcon(pm));
        appInfo.setPkgName(resolveInfo.activityInfo.packageName);
        appInfo.setAppEnterClass(resolveInfo.activityInfo.name);
        LogUtil.d(TAG, "app enter class=" + resolveInfo.activityInfo.name);
        return appInfo;
    }

    /**
     * 获取已安装的app信息
     *
     * @param flag
     * @return
     */
    @Deprecated
    private List<PMAppInfo> getAppInfoOld(int flag) {
        // GET_UNINSTALLED_PACKAGES == MATCH_UNINSTALLED_PACKAGES 即使是应用被uninstall了，但只要保留了数据，也可以被搜出来。
//        List<ApplicationInfo> listApplications = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        List<ApplicationInfo> listApplications = pm.getInstalledApplications(0);
        List<PMAppInfo> appInfos = new ArrayList<>();
        switch (flag) {
            // 所有app
            case ALL_APP:
                appInfos.clear();
                for (ApplicationInfo app : listApplications) {
                    appInfos.add(makeAppInfoOld(app));
                }
                break;
            // 系统app
            case SYSTEM_APP:
                appInfos.clear();
                for (ApplicationInfo app : listApplications) {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        appInfos.add(makeAppInfoOld(app));
                    }
                }
                break;
            // 第三方app
            case THIRD_APP:
                appInfos.clear();
                for (ApplicationInfo app : listApplications) {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                        appInfos.add(makeAppInfoOld(app));
                    } else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                        appInfos.add(makeAppInfoOld(app));
                    }
                }
                break;
            // 安装在sdcard app
            case SDCARD_APP:
                appInfos.clear();
                for (ApplicationInfo app : listApplications) {
                    if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                        appInfos.add(makeAppInfoOld(app));
                    }
                }
                break;
            default:
                return null;
        }
        return appInfos;
    }

    @Deprecated
    private PMAppInfo makeAppInfoOld(ApplicationInfo app) {
        PMAppInfo appInfo = new PMAppInfo();
        appInfo.setAppLabel((String) app.loadLabel(pm));
        appInfo.setAppIcon(app.loadIcon(pm));
        appInfo.setPkgName(app.packageName);
        return appInfo;
    }
}
