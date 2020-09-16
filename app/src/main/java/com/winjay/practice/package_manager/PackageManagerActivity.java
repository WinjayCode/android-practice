package com.winjay.practice.package_manager;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * PackageManager学习
 *
 * @author Winjay
 * @date 2020/9/15
 */
public class PackageManagerActivity extends BaseActivity {
    private PackageManager pm;
    private final static int ALL_APP = 1;
    private final static int SYSTEM_APP = 2;
    private final static int THIRD_APP = 3;
    private final static int SDCARD_APP = 4;

    @BindView(R.id.app_list_rv)
    RecyclerView app_list_rv;

    @Override
    protected int getLayoutId() {
        return R.layout.package_manager_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = getPackageManager();
        app_list_rv.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick(R.id.all_app_btn)
    void allApp() {
        AppListAdapter adapter = new AppListAdapter(this, getAppInfo(ALL_APP));
        app_list_rv.setAdapter(adapter);
    }

    @OnClick(R.id.system_app_btn)
    void systemApp() {
        AppListAdapter adapter = new AppListAdapter(this, getAppInfo(SYSTEM_APP));
        app_list_rv.setAdapter(adapter);
    }

    @OnClick(R.id.third_app_btn)
    void thirdApp() {
        AppListAdapter adapter = new AppListAdapter(this, getAppInfo(THIRD_APP));
        app_list_rv.setAdapter(adapter);
    }

    @OnClick(R.id.sdcard_app_btn)
    void sdcardApp() {
        AppListAdapter adapter = new AppListAdapter(this, getAppInfo(SDCARD_APP));
        app_list_rv.setAdapter(adapter);
    }

    /**
     * 获取已安装的app信息
     *
     * @param flag
     * @return
     */
    private List<PMAppInfo> getAppInfo(int flag) {
        // GET_UNINSTALLED_PACKAGES == MATCH_UNINSTALLED_PACKAGES 即使是应用被uninstall了，但只要保留了数据，也可以被搜出来。
        List<ApplicationInfo> listApplications = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        List<PMAppInfo> appInfos = new ArrayList<>();
        switch (flag) {
            // 所有app
            case ALL_APP:
                appInfos.clear();
                for (ApplicationInfo app : listApplications) {
                    appInfos.add(makeAppInfo(app));
                }
                break;
            // 系统app
            case SYSTEM_APP:
                appInfos.clear();
                for (ApplicationInfo app : listApplications) {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        appInfos.add(makeAppInfo(app));
                    }
                }
                break;
            // 第三方app
            case THIRD_APP:
                appInfos.clear();
                for (ApplicationInfo app : listApplications) {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                        appInfos.add(makeAppInfo(app));
                    } else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                        appInfos.add(makeAppInfo(app));
                    }
                }
                break;
            // 安装在sdcard app
            case SDCARD_APP:
                appInfos.clear();
                for (ApplicationInfo app : listApplications) {
                    if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                        appInfos.add(makeAppInfo(app));
                    }
                }
                break;
            default:
                return null;
        }
        return appInfos;
    }

    private PMAppInfo makeAppInfo(ApplicationInfo app) {
        PMAppInfo appInfo = new PMAppInfo();
        appInfo.setAppLable((String) app.loadLabel(pm));
        appInfo.setAppIcon(app.loadIcon(pm));
        appInfo.setPkgName(app.packageName);
        return appInfo;
    }
}
