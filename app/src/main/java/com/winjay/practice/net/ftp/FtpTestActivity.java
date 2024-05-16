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
 * FTP（File Transfer Protocol，文件传输协议）是一种用于在TCP/IP网络上传输文件的标准协议。它采用客户端-服务器（Client-Server）模式，用户可以通过FTP客户端与远程主机上的FTP服务器进行连接，实现文件的上传、下载和修改等操作。
 *
 * FTP协议支持两种传输模式：ASCII 模式和二进制模式。前者适用于传输文本文件，后者适用于传输图像、动态图像、音频、视频等二进制文件。
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

    }

}
