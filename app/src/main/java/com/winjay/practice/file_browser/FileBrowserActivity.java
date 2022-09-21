package com.winjay.practice.file_browser;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ActivityFileBrowserBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File Browser
 *
 * @author F2848777
 * @date 2022-09-15
 */
public class FileBrowserActivity extends BaseActivity {
    private static final String TAG = "FileBrowserActivity";
    private ActivityFileBrowserBinding binding;

    //当前文件目录
    private String currentpath;
    private int[] img = {R.drawable.file, R.drawable.folder};
    private File[] files;

    @Override
    protected String[] permissions() {
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = ActivityFileBrowserBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!hasPermissions()) {
            requestPermissions();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init(Environment.getExternalStorageDirectory());
        binding.fileListLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                // 获取单击的文件或文件夹的名称
                String folder = ((TextView) arg1.findViewById(R.id.file_name_tv)).getText().toString();
                try {
                    File file = new File(currentpath + '/' + folder);
                    if (file.isDirectory()) {
                        init(file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // 返回上一级
        binding.backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentpath.equals(Environment.getExternalStorageDirectory().getPath())) {
                    File file = new File(currentpath);
                    init(file.getParentFile());
                }
            }
        });
    }

    // 界面初始化
    public void init(File f) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 获取SDcard目录下所有文件名
            files = f.listFiles();
            if (!files.equals(null)) {
                currentpath = f.getPath();
                binding.currentDirectoryTv.setText("当前目录:" + f.getPath());
                List<Map<String, Object>> list = new ArrayList<>();
                for (int i = 0; i < files.length; i++) {
                    Map<String, Object> maps = new HashMap<>();
                    if (files[i].isFile()) {
                        maps.put("image", img[0]);
                    } else {
                        maps.put("image", img[1]);
                    }
                    maps.put("filenames", files[i].getName());
                    list.add(maps);
                }
                SimpleAdapter simple = new SimpleAdapter(this, list,
                        R.layout.item_file_browser, new String[]{"image", "filenames"},
                        new int[]{R.id.file_iv, R.id.file_name_tv});
                binding.fileListLv.setAdapter(simple);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
