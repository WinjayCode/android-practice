package com.winjay.mirrorcast.car.server;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.winjay.mirrorcast.AppApplication;
import com.winjay.mirrorcast.BaseFragment;
import com.winjay.mirrorcast.Constants;
import com.winjay.mirrorcast.app_mirror.AppSocketClientManager;
import com.winjay.mirrorcast.databinding.FragmentCarAppBinding;
import com.winjay.mirrorcast.util.DisplayUtil;
import com.winjay.mirrorcast.util.LogUtil;

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
                int virtualDisplay = createVirtualDisplay();
                AppSocketClientManager.getInstance().sendMessage(
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

    private int createVirtualDisplay() {
        try {
            LogUtil.d(TAG);
            DisplayManager displayManager = (DisplayManager) AppApplication.context.getSystemService(Context.DISPLAY_SERVICE);
            int[] screenSize = DisplayUtil.getScreenSize(AppApplication.context);
            int flags = 139;
            VirtualDisplay display = displayManager.createVirtualDisplay("app_mirror",
                    screenSize[1], screenSize[0], screenSize[2], new SurfaceView(AppApplication.context).getHolder().getSurface(),
                    flags);

            int displayId = display.getDisplay().getDisplayId();
            LogUtil.d(TAG, "virtual display ID=" + displayId);
            return displayId;
        } catch (Exception e) {
            LogUtil.e(TAG, "createVirtualDisplay error " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
