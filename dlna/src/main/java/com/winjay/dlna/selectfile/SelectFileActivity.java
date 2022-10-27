package com.winjay.dlna.selectfile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.dlna.R;
import com.winjay.dlna.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author F2848777
 * @date 2022-09-15
 */
public class SelectFileActivity extends AppCompatActivity {
    private static final String TAG = "SelectFileActivity";
    private TextView current_directory_tv;
    private Button back_btn;
    private ListView file_list_lv;

    //当前文件目录
    private String currentpath;
    private int[] img = {R.drawable.other, R.drawable.wenjianjia};
    private File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        current_directory_tv = findViewById(R.id.current_directory_tv);
        back_btn = findViewById(R.id.back_btn);
        file_list_lv = findViewById(R.id.file_list_lv);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        LogUtil.d(TAG, "sdcard path=" + Environment.getExternalStorageDirectory().getPath());
        init(Environment.getExternalStorageDirectory());
        file_list_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String name = ((TextView) arg1.findViewById(R.id.file_name_tv)).getText().toString();
                try {
                    File file = new File(currentpath + '/' + name);
                    if (file.isDirectory()) {
                        init(file);
                    } else {
                        confirm(currentpath, name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // 返回上一级
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(currentpath) && !currentpath.equals(Environment.getExternalStorageDirectory().getPath())) {
                    File file = new File(currentpath);
                    init(file.getParentFile());
                }
            }
        });
    }

    // 界面初始化
    public void init(File f) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            LogUtil.d(TAG);
            // 获取SDcard目录下所有文件名
            files = f.listFiles();
            if (files != null) {
                currentpath = f.getPath();
                current_directory_tv.setText("当前目录:" + f.getPath());
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
                        R.layout.item_select_file, new String[]{"image", "filenames"},
                        new int[]{R.id.file_iv, R.id.file_name_tv});
                file_list_lv.setAdapter(simple);
            }
        }
    }

    private void confirm(String path, String name) {
        String select = path + "/" + name;
        LogUtil.d(TAG, "select=" + select);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择");
        builder.setMessage(name);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(select, 0);
                finish();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public void setResult(String select, int resultCode) {
        Intent intent = new Intent();
        intent.putExtra("select", select);
        setResult(resultCode, intent);
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
