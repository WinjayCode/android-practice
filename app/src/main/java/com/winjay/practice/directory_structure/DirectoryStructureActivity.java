package com.winjay.practice.directory_structure;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

import butterknife.BindView;

/**
 * Android文件目录
 *
 * @author Winjay
 * @date 2020-01-06
 */
public class DirectoryStructureActivity extends BaseActivity {

    @BindView(R.id.one)
    TextView one;

    @BindView(R.id.two)
    TextView two;

    @BindView(R.id.three)
    TextView three;

    @BindView(R.id.four)
    TextView four;

    @BindView(R.id.five)
    TextView five;

    @BindView(R.id.six)
    TextView six;

    @BindView(R.id.seven)
    TextView seven;

    @BindView(R.id.eight)
    TextView eight;

    @Override
    protected int getLayoutId() {
        return R.layout.directory_structure_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // data/data/xxx/files 或者 data/user/0/xxx/files
        one.setText("getFilesDir()：" + getFilesDir());
        // data/data/xxx/cache 或者 data/user/0/xxx/cache
        two.setText("getCacheDir()：" + getCacheDir());

        // storage/emulated/0/Android/obb/xxx 或者 sdcard/Android/obb/xxx
        three.setText("getObbDir()：" + getObbDir());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            four.setText("getDataDir()：" + getDataDir());
            five.setText("getCodeCacheDir()：" + getCodeCacheDir());
            six.setText("getNoBackupFilesDir()：" + getNoBackupFilesDir());
        }

        // storage/emulated/0/Android/data/xxx/cache 或者 sdcard/Android/data/xxx/cache
        seven.setText("getExternalCacheDir()：" + getExternalCacheDir());
        // storage/emulated/0/Android/data/xxx/files 或者 sdcard/Android/data/xxx/files
        eight.setText("getExternalFilesDir()：" + getExternalFilesDir(null));
    }
}
