package com.winjay.practice.storage;

import static android.os.ext.SdkExtensions.getExtensionVersion;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.bean.AudioBean;
import com.winjay.practice.media.bean.VideoBean;
import com.winjay.practice.media.music.MusicPlayActivity;
import com.winjay.practice.media.video.VideoPlayActivity;
import com.winjay.practice.storage.database.MyDatabase;
import com.winjay.practice.storage.database.User;
import com.winjay.practice.storage.database.UserDao;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.JsonUtil;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.PreferenceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Android存储
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

    @BindView(R.id.open_img)
    ImageView open_img;

    @BindView(R.id.img_location_tv)
    TextView img_location_tv;

    private Uri deleteFileUri;

    private UserDao userDao;

    @Override
    protected int getLayoutId() {
        return R.layout.storage_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyDatabase myDatabase = Room.databaseBuilder(this, MyDatabase.class, "my_database").build();
        userDao = myDatabase.userDao();

//        File file = new File(getFilesDir(), "internal_test_dir");
//        file.mkdir();
//
//        File file2 = new File(getExternalFilesDir(null), "external_test_dir");
//        file2.mkdir();
    }

    @OnClick(R.id.directory_structure_btn)
    void directoryStructure() {
        startNewActivity(DirectoryStructureActivity.class);
    }

    /////////////////////////////////////////////////////// 文档和其他文件 ///////////////////////////////////////////////////////
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

            toast("MANAGE_EXTERNAL_STORAGE permission had been granted.");
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

                    toast("test_file content: " + baos);

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
            toast("delete file " + (result ? "success" : "failure"));
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

                    handleFile(uri);
                }
                break;
            default:
                break;
        }
    }

    private void handleFile(Uri uri) {
        String fileType = FileUtil.getFileTypeFromUri(this, uri);
        LogUtil.d(TAG, "fileType=" + fileType);

        if (fileType.startsWith("text")) {
            showTxt(uri);
        } else if (fileType.startsWith("image")) {
            showImg(uri);
        } else if (fileType.startsWith("audio")) {
            AudioBean audioBean = new AudioBean();
            audioBean.setUri(uri);
            audioBean.setPath(FileUtil.getPathFromUri(this, uri));

            Intent intent = new Intent(this, MusicPlayActivity.class);
            intent.putExtra("audio", audioBean);
            startActivity(intent);
        } else if (fileType.startsWith("video")) {
            getVideoLocation(uri);

            VideoBean videoBean = new VideoBean();
            videoBean.setPath(FileUtil.getPathFromUri(this, uri));

            Intent intent = new Intent(this, VideoPlayActivity.class);
            intent.putExtra("video", videoBean);
            startActivity(intent);
        }
    }

    private void showTxt(Uri uri) {
        // 打开文档
        try {
            toast(FileUtil.readTextFromUri(this, uri));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 修改文档
        FileUtil.alterTextFromUri(this, uri, "Overwritten at " + System.currentTimeMillis() + "\n");
    }

    private void showImg(Uri uri) {
        try {
            open_img.setImageBitmap(FileUtil.getBitmapFromUri(this, uri));
            open_img.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        float[] imageLocation = getImageLocation(uri);
        img_location_tv.setText("latitude=" + imageLocation[0] + "\nlongitude=" + imageLocation[1]);
        img_location_tv.setVisibility(View.VISIBLE);
    }
    /////////////////////////////////////////////////////// 文档和其他文件 ///////////////////////////////////////////////////////

    /////////////////////////////////////////////////////// 媒体内容 ///////////////////////////////////////////////////////

    // Registers a photo picker activity launcher in single-select mode.
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the photo picker.
                if (uri != null) {
                    LogUtil.d(TAG, "Selected photo URI: " + uri);
                    handleFile(uri);
                } else {
                    LogUtil.w(TAG, "No media selected");
                }
            });

    @OnClick(R.id.single_pic_select_btn)
    void singlePicSelectBtn() {
        boolean photoPickerAvailable = ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable();
        toast("photo picker is " + (photoPickerAvailable ? "available" : "unavailable"));

        // Include only one of the following calls to launch(), depending on the types
        // of media that you want to allow the user to choose from.

        // Launch the photo picker and allow the user to choose images and videos.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());

        // Launch the photo picker and allow the user to choose only images.
//        pickMedia.launch(new PickVisualMediaRequest.Builder()
//                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
//                .build());

        // Launch the photo picker and allow the user to choose only videos.
//        pickMedia.launch(new PickVisualMediaRequest.Builder()
//                .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
//                .build());

        // Launch the photo picker and allow the user to choose only images/videos of a
        // specific MIME type, such as GIFs.
//        String mimeType = "image/gif";
//        pickMedia.launch(new PickVisualMediaRequest.Builder()
//                .setMediaType(new ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType))
//                .build());
    }

    // Registering Photo Picker activity launcher with multiple selects (5 max in this example)
    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(5), uris -> {
                // Callback is invoked after the user selects media items or closes the photo picker.
                if (!uris.isEmpty()) {
                    LogUtil.d(TAG, "Number of items selected: " + uris.size());
                } else {
                    LogUtil.w(TAG, "No media selected");
                }
            });

    // 如果照片选择器不可用，且支持库调用 ACTION_OPEN_DOCUMENT intent 操作，则系统会忽略指定的可选媒体文件数量上限。
    @OnClick(R.id.multi_pic_select_btn)
    void multiPicSelectBtn() {
//        boolean photoPickerAvailable = ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable();
        toast("photo picker is " + (isPhotoPickerAvailable() ? "available" : "unavailable"));

        // For this example, launch the photo picker and allow the user to choose images
        // and videos. If you want the user to select a specific type of media file,
        // use the overloaded versions of launch(), as shown in the section about how
        // to select a single media item.
        pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    private boolean isPhotoPickerAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return getExtensionVersion(Build.VERSION_CODES.R) >= 2;
        } else {
            return false;
        }
    }

    /**
     * 获取图片的经纬度信息(会报权限问题!!!)
     *
     * @param uri
     */
    private float[] getImageLocationFromUri(Uri uri) {
        float[] latLong = new float[2];
        try {
            // may be need these code
//            Uri photoUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getString(idColumnIndex));

            // Get location data using the Exifinterface library.
            // Exception occurs if ACCESS_MEDIA_LOCATION permission isn't granted.
            Uri photoUri = MediaStore.setRequireOriginal(uri);
            InputStream stream = getContentResolver().openInputStream(photoUri);
            if (stream != null) {
                ExifInterface exifInterface = new ExifInterface(stream);
                boolean result = exifInterface.getLatLong(latLong);

                LogUtil.d(TAG, "get image location " + (result ? "success" : "failure"));

                // Don't reuse the stream associated with
                // the instance of "ExifInterface".
                stream.close();
            } else {
                LogUtil.w(TAG, "stream is null!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latLong;
    }

    /**
     * 获取图片的经纬度信息
     *
     * @param uri
     */
    private float[] getImageLocation(Uri uri) {
        float[] latLong = new float[2];
        try {
            ExifInterface exifInterface = new ExifInterface(new File(FileUtil.getPathFromUri(this, uri)));
            boolean result = exifInterface.getLatLong(latLong);

            LogUtil.d(TAG, "get image location " + (result ? "success" : "failure"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latLong;
    }

    /**
     * 获取视频的经纬度信息
     *
     * @param uri
     */
    private String getVideoLocation(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(FileUtil.getPathFromUri(this, uri));
        } catch (RuntimeException e) {
            LogUtil.w(TAG, "Cannot retrieve video file", e);
        }
        // Metadata should use a standardized format.
        String locationMetadata = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);

        LogUtil.d(TAG, "locationMetadata=" + locationMetadata);
        return locationMetadata;
    }
    /////////////////////////////////////////////////////// 媒体内容 ///////////////////////////////////////////////////////

    /////////////////////////////////////////////////////// 键值对数据 ///////////////////////////////////////////////////////
    int spInt = 0;

    @OnClick(R.id.save_sp_btn)
    void saveSp() {
        PreferenceUtils.putIntAsync(this, "sp_key", spInt++);
    }

    @OnClick(R.id.get_sp_btn)
    void getSp() {
        toast(String.valueOf(PreferenceUtils.getInt(this, "sp_key", spInt)));
    }
    /////////////////////////////////////////////////////// 键值对数据 ///////////////////////////////////////////////////////

    /////////////////////////////////////////////////////// 本地数据库 ///////////////////////////////////////////////////////
    private User user = new User(1, 1 + "_first_name", 1 + "_last_name");

    @OnClick(R.id.insert_data_btn)
    void insertData() {
        HandlerManager.getInstance().postOnSubThread(new Runnable() {
            @Override
            public void run() {
                userDao.insertAll(user);
            }
        });
    }

    @OnClick(R.id.database_all_data_btn)
    void databaseAllData() {
        HandlerManager.getInstance().postOnSubThread(new Runnable() {
            @Override
            public void run() {
                List<User> userList = userDao.getAll();
                for (User user : userList) {
                    toast(JsonUtil.getInstance().toJson(user));
                }
            }
        });
    }

    @OnClick(R.id.delete_data_btn)
    void deleteData() {
        HandlerManager.getInstance().postOnSubThread(new Runnable() {
            @Override
            public void run() {
                userDao.delete(user);
            }
        });
    }

    @OnClick(R.id.query_data_by_id_btn)
    void queryDataById() {
        HandlerManager.getInstance().postOnSubThread(new Runnable() {
            @Override
            public void run() {
                List<User> userList = userDao.loadAllByIds(new int[]{1});
                for (User user : userList) {
                    toast(JsonUtil.getInstance().toJson(user));
                }
            }
        });
    }

    @OnClick(R.id.query_data_by_parameter_btn)
    void queryDataByParameter() {
        HandlerManager.getInstance().postOnSubThread(new Runnable() {
            @Override
            public void run() {
                User user = userDao.findByName(1 + "_first_name", 1 + "_last_name");
                toast(JsonUtil.getInstance().toJson(user));
            }
        });
    }
    /////////////////////////////////////////////////////// 本地数据库 ///////////////////////////////////////////////////////
}
