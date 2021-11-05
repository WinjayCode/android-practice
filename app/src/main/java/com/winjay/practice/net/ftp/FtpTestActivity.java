package com.winjay.practice.net.ftp;

import android.text.TextUtils;
import android.util.Log;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.FtpUtil;
import com.winjay.practice.utils.LogUtil;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;

import butterknife.OnClick;

/**
 * FTP下载文件
 *
 * @author Winjay
 * @date 2021-08-17
 */
public class FtpTestActivity extends BaseActivity {
    private static final String TAG = FtpTestActivity.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.ftp_test_activity;
    }

    @OnClick(R.id.ftp_btn)
    void ftpDownload() {
        HandlerManager.getInstance().postOnSubThread(new Runnable() {
            @Override
            public void run() {
                FtpUtil ftpUtils = new FtpUtil();

                FTPClient ftpClient = ftpUtils.getFTPClient("ftp.axxc.cn", 21, "wftpsafe", "work2.hard");
                String savedPath = getFilesDir().getPath();
                LogUtil.d(TAG, "savedPath:" + savedPath);
                if (ftpClient != null) {
                    boolean result = ftpUtils.downLoadFTP(ftpClient, "/opt/m2mfile/ftp/", "synchronize_test.zip", savedPath);
                    LogUtil.d(TAG, "result=" + result);
                    ftpUtils.closeFTP(ftpClient);

//                    if (result) {
//                        try {
//                            FileUtil.upZipFile(savedPath + "/" + "synchronize_test.zip", savedPath);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        String zipDirName = "";
//                        if ("synchronize_test.zip".endsWith(".zip")) {
//                            zipDirName = "synchronize_test.zip".replace(".zip", "");
//                        }
//                        Log.d(TAG, "zipDirName=" + zipDirName);
//                        if (!TextUtils.isEmpty(zipDirName)) {
//                            File test = new File(savedPath + File.separator + zipDirName);
//                            if (test.exists()) {
//                                for (File file : test.listFiles()) {
//                                    Log.d(TAG, "dirName=" + file.getName());
//                                    if (new File(savedPath + File.separator + file.getName()).exists()) {
////                                        FileUtil.deleteFolder(savedPath + File.separator + file.getName());
//                                    }
//                                    FileUtil.copyFolder(savedPath + File.separator + zipDirName + File.separator + file.getName(),
//                                            savedPath + File.separator + file.getName());
//                                }
//                            }
//                        }
////                        FileUtil.deleteFolder(savedPath + File.separator + zipDirName);
////                        FileUtil.deleteFolder(savedPath + File.separator + "synchronize_test.zip");
//                    }
                }


//                String savedPath = getFilesDir().getPath();
//                LogUtil.d(TAG, "savedPath:" + savedPath);
//                try {
//                    FileUtil.upZipFile(savedPath + "/" + "synchronize_test.zip", savedPath);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                String zipDirName = "";
//                if ("synchronize_test.zip".endsWith(".zip")) {
//                    zipDirName = "synchronize_test.zip".replace(".zip", "");
//                }
//                Log.d(TAG, "zipDirName=" + zipDirName);
//                if (!TextUtils.isEmpty(zipDirName)) {
//                    File test = new File(savedPath + File.separator + zipDirName);
//                    if (test.exists()) {
//                        for (File file : test.listFiles()) {
//                            Log.d(TAG, "dirName=" + file.getName());
//                            if (new File(savedPath + File.separator + file.getName()).exists()) {
////                                        FileUtil.deleteFolder(savedPath + File.separator + file.getName());
//                                FileUtil.delete(savedPath + File.separator + file.getName());
//                            }
//                            FileUtil.copyFolder(savedPath + File.separator + zipDirName + File.separator + file.getName(),
//                                    savedPath + File.separator + file.getName());
//                        }
//                    }
//                }
            }
        });
    }

}
