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
        //android 内置存储
        //Android可以说是一个Linux操作系统，它的内部存储空间对于应用程序和用户来讲就是“/data/data“目录。
        //内部存储与外部存储相比有着比较稳定，存储方便，操作简单，更加安全（可以控制访问权限）等优点，而它唯一的缺点就是空间有限。
        //内部存储空间的有限意味着应物尽其用，用来保存比较重要的数据，例如用户信息资料，口令秘码等不需要与其他应用程序共享的数据。
        //注意应用程序被卸载时，应用程序在内部存储空间的文件数据将全部被删除，避免占用宝贵的空间。

        //内部存储即data文件夹，其中里面有两个文件夹值得关注：
        //app文件夹（未root无法打开）：存放着所有app的apk文件夹，当开发者调试某个app时，AS控制台输出的内容中有一项是uploading…，代表正在上传apk到这个文件夹。
        //data文件夹：内部都是app的包名，存储着应用程序相关的数据，例如 data/data/包名/(shared_prefs、database、files、cache)

        // data/user/0 为多用户文件夹 0为当前用户

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

        // storage/emulated/0 或者 sdcard 外部存储根目录
        mList.add("Environment.getExternalStorageDirectory()：" + Environment.getExternalStorageDirectory());
        // 外部存储公有目录
        mList.add("Environment.getExternalStoragePublicDirectory:DIRECTORY_DCIM()：" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        mList.add("");

        // 当app被卸载后，sdCard/Android/data/PackageName/下的所有文件都会被删除，不会留下垃圾信息。两个API对应的目录分别对应着 设置->应用->应用详情里面的“清除数据”与“清除缓存”选项。
        // storage/emulated/0/Android/data/xxx/cache 或者 sdcard/Android/data/xxx/cache 外部存储私有目录,一般存储临时缓存数据
        mList.add("getExternalCacheDir()：" + getExternalCacheDir());
        // storage/emulated/0/Android/data/xxx/files 或者 sdcard/Android/data/xxx/files  外部存储私有目录,一般存储长时间保存的数据
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
