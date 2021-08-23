package com.winjay.practice.entity;


import com.winjay.practice.net.retrofit.BaseFileDownload;
import com.winjay.practice.net.retrofit.IApiService;

import retrofit2.Call;

/**
 * 测试下载文件请求实体类(具体更具需求变更)
 * 必须继承BaseFileDownload
 */
public class FileDownloadEntity extends BaseFileDownload {

    private String url;

    public FileDownloadEntity(String url) {
        this.url = url;
    }

    @Override
    public Call getFileDownloadCall(IApiService IApiService) {
        return IApiService.downLoadFile(url);
    }
}
