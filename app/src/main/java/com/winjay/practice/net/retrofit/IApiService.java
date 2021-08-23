package com.winjay.practice.net.retrofit;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by huijieZ on 2018/8/27.
 * @author huijieZ
 */

public interface IApiService {

//    /**
//     * Get方法 url部分写死，部分配置，注解中大括号里面的内容如下方法所示，可以通过传参设置
//     * @param owner
//     * @param repo
//     * @return
//     */
//    @GET("/repos/{owner}/{repo}/contributors")
//    Call<List<Contributor>> contributors(
//            @Path("owner") String owner,
//            @Path("repo") String repo
//    );

    /**
     * Get请求
     * @param url 请求地址
     * @param maps 请求参数
     * @return 请求返回的对象
     */
    @GET("{url}")
    Call<ResponseBody> executeGet(
            @Path("url") String url,
            @QueryMap Map<String, String> maps);


    /**
     * post请求
     * @param url 请求地址
     * @param maps post请求参数键值对
     * @return 返回post请求结果
     */
    @POST("{url}")
    Call<ResponseBody> executePost(
            @Path("url") String url,
            @QueryMap Map<String, String> maps);


    /**
     * 文件上传
     * @param file 上传文件
     * @return 返回上传结果
     */
    @Multipart
    @POST("testUploadFiles")
    Call<ResponseBody> upload(@Part MultipartBody.Part file);

    /**
     * 多文件上传
     * @param maps 文件上传列表
     * @return 返回下载结果
     */
    @Multipart
    @POST("testUploadFiles")
    Call<ResponseBody> uploadFiles(@PartMap Map<String, RequestBody> maps);

    /**
     * 文件下载
     * @param fileUrl 下载地址
     * @return 返回下载结果
     */
    @Streaming
    @GET
    Call<ResponseBody> downLoadFile(@Url String fileUrl);

    /**
     * 断点续传下载接口
     * @param start 下载的起始位置
     * @param url 下载地址
     */
    @Streaming
    @GET
    Call<ResponseBody> downLoadFile(@Header("RANGE") String start, @Url String url);

    /**
     * 多线程下载
     */




    /**
     * 文件下载
     *
     * @param url
     * @return
     */
    @GET("{url}")
    Call<ResponseBody> download(@Path(value = "url", encoded = true) String url);

    //http://aispeech-kui-public.oss-cn-shenzhen.aliyuncs.com/release/dangbei/version-guide-1.1.json

    /**
     * 请求更新配置
     *
     * @param url
     * @return
     */
    @GET("{url}")
    Call<ResponseBody> upgradeConfig(@Path(value = "url", encoded = true) String url);

    /**
     * 触发网络请求,先拉取是否可以更新
     * mBaseUrl: http://test.iot.aispeech.com:8089/skyline-iot-api/api/v2/tv/versionUpgrade ,productId: 278572232 ,deviceId: 4d07e4be9184e15a8b483c97077e171b
     * url = http://test.iot.aispeech.com:8089/skyline-iot-api/api/v2/tv/versionUpgrade?productId=278572232&versionCode=1005&deviceId=4d07e4be9184e15a8b483c97077e171b&packageName=com.aispeech.kui
     */

    /**
     * 请求版本更新
     *
     * @param url 请求URL
     * @param map
     * @return
     */
    @GET("{url}")
    Call<ResponseBody> upgradeVersion(@Path(value = "url", encoded = true) String url, @QueryMap Map<String, String> map);


    /**
     * 请求版本更新
     *
     * @param url         请求URL
     * @param productId   产品ID
     * @param versionCode 版本号
     * @param deviceId    设备号
     * @param packageName 包名
     * @return
     */
    @GET("{url}")
    Call<ResponseBody> upgradeVersion(@Path(value = "url", encoded = true) String url, @Query("productId") String productId, @Query("versionCode") String versionCode, @Query("deviceId") String deviceId, @Query("packageName") String packageName);

}
