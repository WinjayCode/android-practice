package com.winjay.practice.directory_structure;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.MainAdapter;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Android文件目录
 *
 * @author Winjay
 * @date 2020-01-06
 */
public class DirectoryStructureActivity extends BaseActivity {

    @BindView(R.id.directory_structure_rv)
    RecyclerView mRecyclerView;

    private List<String> mList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.directory_structure_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // app的私有目录
        // data/data/xxx/files 或者 data/user/0/xxx/files
        mList.add("getFilesDir()：" + getFilesDir());
        // data/data/xxx/cache 或者 data/user/0/xxx/cache
        mList.add("getCacheDir()：" + getCacheDir());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // data/data/xxx 或者 data/user/0/xxx (Android不建议使用)
            mList.add("getDataDir()：" + getDataDir());
            // data/data/xxx/code_cache 或者 data/user/0/xxx/code_cache
            mList.add("getCodeCacheDir()：" + getCodeCacheDir());
            // data/data/xxx/no_backup 或者 data/user/0/xxx/no_backup
            mList.add("getNoBackupFilesDir()：" + getNoBackupFilesDir());
        }

        mList.add("");

        // storage/emulated/0
        mList.add("Environment.getExternalStorageDirectory()：" + Environment.getExternalStorageDirectory());

        mList.add("");

        // storage/emulated/0/Android/data/xxx/cache 或者 sdcard/Android/data/xxx/cache
        mList.add("getExternalCacheDir()：" + getExternalCacheDir());
        // storage/emulated/0/Android/data/xxx/files 或者 sdcard/Android/data/xxx/files
        mList.add("getExternalFilesDir()：" + getExternalFilesDir(null));

        mList.add("");

        // storage/emulated/0/Android/obb/xxx 或者 sdcard/Android/obb/xxx
        mList.add("getObbDir()：" + getObbDir());

        mList.add("");

        // data
        mList.add("Environment.getDataDirectory()：" + Environment.getDataDirectory());
        // data/cache
        mList.add("Environment.getDownloadCacheDirectory()：" + Environment.getDownloadCacheDirectory());
        // system
        mList.add("Environment.getRootDirectory()：" + Environment.getRootDirectory());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MainAdapter(mList));
    }
}
