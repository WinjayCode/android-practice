package com.winjay.practice.net.ftp;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.FtpUtil;
import com.winjay.practice.utils.LogUtil;

import org.apache.commons.net.ftp.FTPClient;

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

                FTPClient ftpClient = ftpUtils.getFTPClient("ftp.axxc.cn", 21, "ftpsafe", "work1.hard");
                String savedPath = getFilesDir().getPath();
                LogUtil.d(TAG, "savedPath:" + savedPath);
                if (ftpClient != null) {
                    boolean result = ftpUtils.downLoadFTP(ftpClient, "/opt/m2mfile/ftp", "CSharpDEMO_USB.zip", savedPath);
                    LogUtil.d(TAG, "result=" + result);
                    ftpUtils.closeFTP(ftpClient);
                }
            }
        });
    }
}
