package com.winjay.practice.net.retrofit;

import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final int DEFAULT_TIMEOUT = 5;
    /**
     * 接口类
     */
    private IApiService iApiService;
    private OkHttpClient okHttpClient;
    private static String baseUrl = "https://api.github.com";

    private static Retrofit.Builder builder;

    /**
     * 带参构造方法
     * @param url 要访问地址的baseUrl
     */
    private RetrofitClient(String url){
        if(TextUtils.isEmpty(url)){
            url = baseUrl;
        }
        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(
                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();

        iApiService = retrofit.create(IApiService.class);
    }


    public IApiService getiApiService() {
        return iApiService;
    }

    /**
     * 带参实例化方法
     * @param url baseUrl
     * @return 返回RetrofitClient对象(Retrofit已经初始化完毕)
     */
    public static RetrofitClient getInstance(String url){
        return new RetrofitClient(url);
    }

//    /**
//     * 普通Get方法 无参
//     * @param owner 请求的完整url的 组成部分
//     * @param repo 请求的完整url的 组成部分
//     * @return 请求返回的数据，以List数组的形式
//     */
//    public Call<List<Contributor>> getContributors(String owner, String repo){
//            return iApiService.contributors(owner, repo);
//    }

    /**
     * 多参数 Get方法
     * @param url 请求网址
     * @param parameters 请求参数
     * @return 返回请求对象BaseResponse
     */
    public Call<ResponseBody> get(String url, Map<String, String> parameters) {
        return iApiService.executeGet(url, parameters);
    }

    /**
     * 多参数post方法
     * @param url 请求网址
     * @param parameters 请求参数
     * @return 返回ResponseBody对象
     */
    public Call<ResponseBody> post(String url, Map<String, String> parameters) {
        return iApiService.executePost(url, parameters);
    }

    /**
     * 文件上传
     * @param file 要上传的文件
     * @return 返回上传结果
     */
    public Call<ResponseBody> upLoadFile(File file){
        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        return iApiService.upload(body);
    }

    /**
     * 多文件上传
     * @param maps 文件存储列表
     * @return 返回上传结果
     */
    public Call<ResponseBody> upLoadFiles(Map<String, File> maps){
        File file1 = maps.get("file1");
        File file2 = maps.get("file2");
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
        RequestBody requestBody2 = RequestBody.create(MediaType.parse("multipart/form-data"), file2);
        Map<String, RequestBody> params = new HashMap<>();
        params.put("file\"; filename=\""+ file1.getName(), requestBody1);
        params.put("file\"; filename=\""+ file2.getName(), requestBody2);

        return iApiService.uploadFiles(params);
    }

    /**
     * 文件下载
     * @param fileUrl 下载地址
     * @return 返回下载结果
     */
    public Call<ResponseBody> downLoadFile(String fileUrl){
        return iApiService.downLoadFile(fileUrl);
    }

}
