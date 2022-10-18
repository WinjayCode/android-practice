package com.winjay.practice.storage;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.MainAdapter;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Android文件目录
 * <p>
 * android 内置存储
 * Android可以说是一个Linux操作系统，它的内部存储空间对于应用程序和用户来讲就是“/data/data“目录。
 * 内部存储与外部存储相比有着比较稳定，存储方便，操作简单，更加安全（可以控制访问权限）等优点，而它唯一的缺点就是空间有限。
 * 内部存储空间的有限意味着应物尽其用，用来保存比较重要的数据，例如用户信息资料，口令秘码等不需要与其他应用程序共享的数据。
 * 注意应用程序被卸载时，应用程序在内部存储空间的文件数据将全部被删除，避免占用宝贵的空间。
 * <p>
 * 内部存储即data文件夹，其中里面有两个文件夹值得关注：
 * app文件夹（未root无法打开）：存放着所有app的apk文件夹，当开发者调试某个app时，AS控制台输出的内容中有一项是uploading…，代表正在上传apk到这个文件夹。
 * data文件夹：内部都是app的包名，存储着应用程序相关的数据，例如 data/data/包名/(shared_prefs、database、files、cache)
 * <p>
 * data/user/0 为多用户文件夹 0为当前用户
 *
 * @author Winjay
 * @date 2020-01-06
 */
public class StorageActivity extends BaseActivity {
    private static final String TAG = "StorageActivity";

    // 创建共享文件
    private static final int CREATE_FILE = 1;
    // 打开共享文件
    private static final int PICK_PDF_FILE = 2;
    //
    private static final int OPEN_DOCUMENT_TREE = 3;

    @BindView(R.id.directory_structure_rv)
    RecyclerView mRecyclerView;

    @BindView(R.id.open_img)
    ImageView open_img;

    private List<String> mList = new ArrayList<>();

    private Uri deleteFileUri;

    @Override
    protected int getLayoutId() {
        return R.layout.directory_structure_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mList.add("内部存储：");

        // data
        mList.add("Environment.getDataDirectory()：" + Environment.getDataDirectory());
        // data/cache
        mList.add("Environment.getDownloadCacheDirectory()：" + Environment.getDownloadCacheDirectory());
        // system
        mList.add("Environment.getRootDirectory()：" + Environment.getRootDirectory());

        mList.add("内部应用专属存储");

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

        mList.add("外部存储：");

        // storage/emulated/0 或者 sdcard 外部存储根目录
        mList.add("Environment.getExternalStorageDirectory()：" + Environment.getExternalStorageDirectory());
        // 外部存储公有目录
        mList.add("Environment.getExternalStoragePublicDirectory:DIRECTORY_DCIM()：" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));

        // storage/emulated/0/Android/obb/xxx 或者 sdcard/Android/obb/xxx
        mList.add("getObbDir()：" + getObbDir());

        mList.add("外部应用专属存储");

        // 当app被卸载后，sdCard/Android/data/PackageName/下的所有文件都会被删除，不会留下垃圾信息。两个API对应的目录分别对应着 设置->应用->应用详情里面的“清除数据”与“清除缓存”选项。
        // storage/emulated/0/Android/data/xxx/cache 或者 sdcard/Android/data/xxx/cache 外部存储私有目录,一般存储临时缓存数据
        mList.add("getExternalCacheDir()：" + getExternalCacheDir());
        // storage/emulated/0/Android/data/xxx/files 或者 sdcard/Android/data/xxx/files  外部存储私有目录,一般存储长时间保存的数据
        mList.add("getExternalFilesDir()：" + getExternalFilesDir(null));
        // storage/emulated/0/Android/media/xxx
        mList.add("getExternalMediaDirs()：" + Arrays.toString(getExternalMediaDirs()));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MainAdapter(mList));


//        File file = new File(getFilesDir(), "internal_test_dir");
//        file.mkdir();
//
//        File file2 = new File(getExternalFilesDir(null), "external_test_dir");
//        file2.mkdir();
    }

    // 申请所有文件访问权限
    @RequiresApi(api = Build.VERSION_CODES.R)
    @OnClick(R.id.all_files_access_permission_btn)
    void getAllFilesAccessPermission() {
        if (!Environment.isExternalStorageManager()) {
            // 所有应用列表
//            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//            startActivity(intent);

            // 指定应用授权页面
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
            startActivity(intent);
        } else {
            LogUtil.d(TAG, "MANAGE_EXTERNAL_STORAGE permission had been granted.");

            File file = new File(Environment.getExternalStorageDirectory(), "test_file");
            if (file.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = fis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }

                    toast(baos.toString());

                    fis.close();
                    baos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    boolean result = file.createNewFile();
                    LogUtil.d(TAG, "create file " + (result ? "success" : "failure"));
                    toast(file.getPath() + "\ncreate file " + (result ? "success" : "failure"));

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write("lalalalala".getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 使用SAF（存储访问框架）创建共享文件
    @OnClick(R.id.create_share_doc_btn)
    void createShareDocBtn() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("application/pdf");
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, "test_share.txt");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
//        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, CREATE_FILE);
    }

    // 使用SAF（存储访问框架）打开共享文件
    @OnClick(R.id.open_share_doc_btn)
    void openShareDocBtn() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("application/pdf");
        intent.setType("*/*");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
//        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        // 查找所有 PDF、ODT 和 TXT 文件
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {
//                "application/pdf", // .pdf
//                "application/vnd.oasis.opendocument.text", // .odt
//                "text/plain" // .txt
//        });

        startActivityForResult(intent, PICK_PDF_FILE);
    }

    // 使用SAF（存储访问框架）授予对目录内容的访问权限
    @OnClick(R.id.open_share_doc_tree_btn)
    public void openDirectory() {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
//        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        startActivityForResult(intent, OPEN_DOCUMENT_TREE);
    }

    @OnClick(R.id.delete_btn)
    public void deleteFile() {
        if (deleteFileUri != null) {
            boolean result = FileUtil.deleteFileFromUri(this, deleteFileUri);
            LogUtil.d(TAG, "result=" + result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CREATE_FILE:
                // The result data contains a URI for the document or directory that the user selected.
                if (data != null) {
                    Uri uri = data.getData();
                    // Perform operations on the document using its URI.
                    LogUtil.d(TAG, "uri=" + uri.toString());
                    LogUtil.d(TAG, "path=" + FileUtil.getPathFromUri(this, uri));
                    FileUtil.dumpMetaData(this, uri);
                }
                break;
            case PICK_PDF_FILE:
                // The result data contains a URI for the document or directory that the user selected.
                if (data != null) {
                    Uri uri = data.getData();
                    deleteFileUri = uri;
                    // Perform operations on the document using its URI.
                    LogUtil.d(TAG, "uri=" + uri.toString());
                    LogUtil.d(TAG, "path=" + FileUtil.getPathFromUri(this, uri));
                    FileUtil.dumpMetaData(this, uri);

                    String fileType = FileUtil.getFileTypeFromUri(this, uri);
                    LogUtil.d(TAG, "fileType=" + fileType);

                    if (fileType.equals("text/plain")) {
                        // 打开文档
                        try {
                            toast(FileUtil.readTextFromUri(this, uri));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // 修改文档
                        FileUtil.alterTextFromUri(this, uri, "Overwritten at " + System.currentTimeMillis() + "\n");
                    } else if (fileType.equals("image/jpeg")) {
                        try {
                            open_img.setImageBitmap(FileUtil.getBitmapFromUri(this, uri));
                            open_img.setVisibility(View.VISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
}
