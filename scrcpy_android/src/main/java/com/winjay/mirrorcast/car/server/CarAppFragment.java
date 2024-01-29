package com.winjay.mirrorcast.car.server;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.app_socket.AppSocketManager;
import com.winjay.mirrorcast.common.BaseFragment;
import com.winjay.mirrorcast.databinding.FragmentCarAppBinding;
import com.winjay.mirrorcast.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

public class CarAppFragment extends BaseFragment<FragmentCarAppBinding> {
    private static final String TAG = CarAppFragment.class.getSimpleName();

    private AppListAdapter mAppListAdapter;

    @Override
    protected FragmentCarAppBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentCarAppBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getBinding().titleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        initAppItemView();
    }

    private void initAppItemView() {
        getBinding().appItemRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mAppListAdapter = new AppListAdapter(getContext());
        getBinding().appItemRv.setAdapter(mAppListAdapter);
        mAppListAdapter.setOnItemClickListener(new AppListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AppBean appBean) {
                int virtualDisplay = DisplayUtil.createVirtualDisplay(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                AppSocketManager.getInstance().sendMessage(
                        Constants.APP_COMMAND_PHONE_APP_MIRROR_CAST
                                + Constants.COMMAND_SPLIT
                                + appBean.getPkgName()
                                + Constants.COMMAND_SPLIT
                                + appBean.getEnterClass()
                                + Constants.COMMAND_SPLIT
                                + virtualDisplay);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppBean> appInfo = getAppInfo();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAppListAdapter.setData(appInfo);
                    }
                });
            }
        }).start();
    }

    private List<AppBean> getAppInfo() {
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = getContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
        List<AppBean> appInfos = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolveInfos) {
            appInfos.add(makeAppInfo(resolveInfo));
        }
        return appInfos;
    }

    private AppBean makeAppInfo(ResolveInfo resolveInfo) {
        AppBean appBean = new AppBean();
        appBean.setAppName((String) resolveInfo.loadLabel(getContext().getPackageManager()));
        appBean.setAppIcon(resolveInfo.loadIcon(getContext().getPackageManager()));
        appBean.setPkgName(resolveInfo.activityInfo.packageName);
        appBean.setEnterClass(resolveInfo.activityInfo.name);
        return appBean;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
