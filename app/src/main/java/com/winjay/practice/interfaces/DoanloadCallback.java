package com.winjay.practice.interfaces;

import java.io.File;


public interface DoanloadCallback {

    /**
     * 文件下载成功并保存
     *
     * @param file 保存的文件
     */
    void onSuccess(File file);


    /**
     * 文件下载失败
     *
     * @param error
     */
    void onFailure(Throwable error);

    /**
     * 更新下载进度
     *
     * @param total    文件总大小
     * @param progress 当已经下载进度
     * @param done     是否下载完成
     */
    void onLoading(long total, long progress, boolean done);
}
