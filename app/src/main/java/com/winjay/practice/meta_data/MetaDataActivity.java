package com.winjay.practice.meta_data;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ActivityMetaDataBinding;
import com.winjay.practice.utils.LogUtil;

/**
 * 获取 AndroidManifest 中配置的 meta-data数据
 * -可以获取自己应用的
 * -也可以获取其他应用的（获取其他应用的数据需要配置获取应用列表的权限）
 * <uses-permission
 *         android:name="android.permission.QUERY_ALL_PACKAGES"
 *         tools:ignore="QueryAllPackagesPermission" />
 *
 * @author Winjay
 * @date 2023-08-10
 */
public class MetaDataActivity extends BaseActivity {
    private static final String TAG = "MetaDataActivity";
    private ActivityMetaDataBinding binding;

    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = ActivityMetaDataBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getMetaData();
    }

    private void getMetaData() {
        // 自己app
        String packageName = getPackageName();
        String className = getClass().getName();

        // 其他app
//        String packageName = "com.winjay.mirrorcast";
//        String className = "com.winjay.mirrorcast.VehicleActivity";

        PackageManager packageManager = getPackageManager();
        ComponentName cn = new ComponentName(packageName, className);

        try {
            // 获取 application 标签下的 meta-data 数据
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Bundle metaData = applicationInfo.metaData;
            if (metaData != null) {
                // 获取meta-data中的value值
                String value = metaData.getString("application_name");
                LogUtil.d(TAG, "application_value=" + value);

                binding.tv.setText(value);
            } else {
                LogUtil.e(TAG, "metaData == null");
            }



            // 获取 activity 标签下的 meta-data 数据
            ActivityInfo activityInfo = packageManager.getActivityInfo(cn, PackageManager.GET_META_DATA);
            Bundle metaData1 = activityInfo.metaData;
            if (metaData1 != null) {
                // 获取meta-data中的value值
                String value = metaData1.getString("activity_name");
                LogUtil.d(TAG, "activity_value=" + value);

                binding.tv.setText(binding.tv.getText().toString() + "\n" + value);
            } else {
                LogUtil.e(TAG, "metaData1 == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        }
    }
}
