package com.winjay.practice.net.retrofit;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 文件下载、文件上传工具类
 */
public class RetrofitFileUtils {

    private RetrofitFileUtils() {
    }

    private static <T> IApiService getRetrofitService(String baseUrl) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                //.baseUrl(BASE_URL)
                .baseUrl(baseUrl)
                .build();
        IApiService IApiService = retrofit.create(IApiService.class);
        return IApiService;
    }

    private static <T> IApiService getRetrofitService(String baseUrl, final RetrofitCallback<T> callback) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response proceed = chain.proceed(chain.request());
                return proceed.newBuilder().body(new FileResponseBody<T>(proceed.body(), callback)).build();
            }
        });

        // 添加日志打印
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//            @Override
//            public void log(String message) {
//                TLog.i("RetrofitLog","retrofitBack = "+message);
//            }
//        });
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        builder.addInterceptor(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
        IApiService IApiService = retrofit.create(IApiService.class);
        return IApiService;
    }

    /**
     * 文件上传
     *
     * @param baseFileUpload
     * @param callback
     */
    public static <T> Call uploadFile(String baseUrl, BaseFileUpload baseFileUpload, RetrofitCallback<T> callback) {
        Call call = baseFileUpload.getFileUploadCall(getRetrofitService(baseUrl));
        call.enqueue(callback);
        return call;
    }

    /**
     * 文件下载
     *
     * @param baseFileDownload
     * @param callback
     */
    public static <T> Call downloadFile(String baseUrl, BaseFileDownload baseFileDownload, RetrofitCallback<T> callback) {
        Call call = baseFileDownload.getFileDownloadCall(getRetrofitService(baseUrl, callback));
        call.enqueue(callback);
        return call;
    }

}
