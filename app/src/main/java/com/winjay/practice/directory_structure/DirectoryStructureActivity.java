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

        one.setText("getFilesDir()：" + getFilesDir());
        two.setText("getCacheDir()：" + getCacheDir());
        three.setText("getObbDir()：" + getObbDir());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            four.setText("getDataDir()：" + getDataDir());
            five.setText("getCodeCacheDir()：" + getCodeCacheDir());
            six.setText("getNoBackupFilesDir()：" + getNoBackupFilesDir());
        }

        seven.setText("getExternalCacheDir()：" + getExternalCacheDir());
        eight.setText("getExternalFilesDir()：" + getExternalFilesDir(null));
    }
}
