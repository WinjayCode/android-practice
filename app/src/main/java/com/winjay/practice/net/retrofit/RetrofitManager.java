package com.winjay.practice.net.retrofit;


import com.winjay.practice.entity.FileDownloadEntity;
import com.winjay.practice.interfaces.DoanloadCallback;
import com.winjay.practice.interfaces.UpgradeRequestCallBack;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.URLUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RetrofitManager {
    private String TAG = "RetrofitManager";
    private volatile static RetrofitManager instance;

    private RetrofitManager() {

    }

    public static RetrofitManager getInstance() {
        if (instance == null) {
            synchronized (RetrofitManager.class) {
                if (instance == null) {
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }

    /**
     * 下载文件
     *
     * @param fileUrl  下载文件url
     * @param dirPath  保存的目录名称
     * @param fileName 保存的文件名
     * @param callback 下载回调
     */
    public void download(String fileUrl, final String dirPath, final String fileName, final DoanloadCallback callback) {

        //        String path = "http://aispeech-kui-public.oss-cn-shenzhen.aliyuncs.com/release/dangbei/kui-tv/kui-tv-dangbei-1.0.11.180929.2-1011.apk";
        //        fileUrl = "http://ksyun-cdn.ottboxer.cn/apkmarket_file/app/video/ystjg/tv_video_3.3.2.2020_android_13090.apk";

        String url = URLUtil.getUrl(fileUrl);
        String baseUrl = URLUtil.getHost(fileUrl);
        LogUtil.d(TAG, "url=" + url);
        LogUtil.d(TAG, "baseUrl=" + baseUrl);
        try {
            FileDownloadEntity downloadEntity = new FileDownloadEntity(url);
            RetrofitFileUtils.downloadFile(baseUrl, downloadEntity, new RetrofitCallback<ResponseBody>() {
                @Override
                public void onSuccess(Call<ResponseBody> call, final Response<ResponseBody> response) {
                    LogUtil.d(TAG, "onSuccess ");

                    File f = new File(dirPath);
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    final File file = new File(f, fileName);
                    if (file.exists()) {
                        LogUtil.d(TAG, "文件已经纯在了，删除");
                        file.delete();
                    }

                    HandlerManager.getInstance().postOnSubThread(new Runnable() {
                        @Override
                        public void run() {
                            saveFile(file, response, callback);
                        }
                    });
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    LogUtil.d(TAG, "onFailure " + t.getMessage());
                    if (callback != null) {
                        callback.onFailure(t);
                    }
                }

                @Override
                public void onLoading(long total, long progress, boolean done) {
                    //super.onLoading(total, progress, done);
//                    LogUtil.i(TAG, "onLoading " + (float) (progress * 1.0 / total) * 100 + "% , " + (done ? "下载完成" : "未下载完成"));
                    if (callback != null) {
                        callback.onLoading(total, progress, done);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(new Throwable("baseUrl参数异常: " + baseUrl));
            }
        }
    }

    private void saveFile(File file, Response<ResponseBody> response, DoanloadCallback callback) {
        try {
            InputStream is = response.body().byteStream();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            fos.close();
            bis.close();
            is.close();
            LogUtil.d(TAG, "文件保存成功");

            if (callback != null) {
                callback.onSuccess(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取更新配置单
     *
     * @param configUrl 配置文件url
     * @param callback  回调
     */
    public void upgradeConfig(String configUrl, final UpgradeRequestCallBack callback) {
        String url = URLUtil.getUrl(configUrl);
        String baseUrl = URLUtil.getHost(configUrl);
        LogUtil.d(TAG, baseUrl);
        LogUtil.d(TAG, url);

        try {
            IApiService iApiService = RetrofitClient.getInstance(baseUrl).getiApiService();
            Call<ResponseBody> call = iApiService.upgradeConfig(url);
            call.enqueue(new RetrofitCallback<ResponseBody>() {
                @Override
                public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            String data = response.body().string();
                            LogUtil.d(TAG, "data -->>>  " + data);
                            if (callback != null) {
                                callback.requestSuccess(data);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (callback != null) {
                                callback.requestError(e.getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    LogUtil.e(TAG, "onFailure " + t.getMessage());
                    if (callback != null) {
                        callback.requestError(t.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.requestError("baseUrl参数异常: " + baseUrl);
            }
        }
    }

    /**
     * 请求版本更新
     *
     * @param baseURL  升级环境
     *                 productId   产品ID
     *                 versionCode 版本号
     *                 deviceId    设备号
     *                 packageName 包名
     * @param callback 回调
     */
    //    public void upgradeVersion(String baseURL, String productId, String versionCode, String deviceId, String packageName, final UpgradeRequestCallBack callback) {
    public void upgradeVersion(String baseURL, Map<String, String> map, final UpgradeRequestCallBack callback) {

        /**
         * 触发网络请求,先拉取是否可以更新
         * mBaseUrl: http://test.iot.aispeech.com:8089/skyline-iot-api/api/v2/tv/versionUpgrade ,productId: 278572232 ,deviceId: 4d07e4be9184e15a8b483c97077e171b
         * url = http://test.iot.aispeech.com:8089/skyline-iot-api/api/v2/tv/versionUpgrade?productId=278572232&versionCode=1005&deviceId=4d07e4be9184e15a8b483c97077e171b&packageName=com.aispeech.kui
         */

        /**
         *  不正常
         *  http://test.iot.aispeech.com/:8089/skyline-iot-api/api/v2/tv/versionUpgrade?productId=278572232&versionCode=1005&deviceId=4d07e4be9184e15a8b483c97077e171b&packageName=com.aispeech.kui http/1.1
         *  正常
         *  http://test.iot.aispeech.com:8089/skyline-iot-api/api/v2/tv/versionUpgrade?productId=278572232&versionCode=1005&deviceId=4d07e4be9184e15a8b483c97077e171b&packageName=com.aispeech.kui
         */

        //        Map<String, String> map = new HashMap<>();
        //        map.put("productId", productId);
        //        map.put("versionCode", versionCode);
        //        map.put("deviceId", deviceId);
        //        map.put("packageName", packageName);

        String url = URLUtil.getUrl(baseURL);
        String baseUrl = URLUtil.getHost(baseURL);
        LogUtil.d(TAG, baseUrl);
        LogUtil.d(TAG, url);

        try {
            IApiService iApiService = RetrofitClient.getInstance(baseUrl).getiApiService();

            //Call<ResponseBody> call = iApiService.upgradeVersion(url, productId, versionCode, deviceId, packageName);//带参数
            Call<ResponseBody> call = iApiService.upgradeVersion(url, map);//参数列表
            call.enqueue(new RetrofitCallback<ResponseBody>() {
                @Override
                public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            String data = response.body().string();
                            LogUtil.d(TAG, "data -->>>  " + data);
                            if (callback != null) {
                                callback.requestSuccess(data);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (callback != null) {
                                callback.requestError(e.getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    LogUtil.e(TAG, "onFailure " + t.getMessage());
                    if (callback != null) {
                        callback.requestError(t.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.requestError("baseUrl参数异常: " + baseUrl);
            }
        }
    }

}
