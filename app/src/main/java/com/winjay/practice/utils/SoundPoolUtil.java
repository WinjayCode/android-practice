package com.winjay.practice.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.winjay.practice.interfaces.DoanloadCallback;
import com.winjay.practice.net.RetrofitManager;
import com.winjay.practice.thread.HandlerManager;

import java.io.File;
import java.io.IOException;

/**
 * 短音频播放器（类似MediaPlayer）
 * <p>
 * SoundPool相对于MediaPlayer的优点<br/>
 * 1.SoundPool适合 短且对反应速度比较高 的情况（游戏音效或按键声等），文件大小一般控制在几十K到几百K，最好不超过1M；<br/>
 * 2.SoundPool 可以与MediaPlayer同时播放，SoundPool也可以同时播放多个声音；<br/>
 * 3.SoundPool 最终编解码实现与MediaPlayer相同；<br/>
 * 4.MediaPlayer只能同时播放一个声音，加载文件有一定的时间，适合文件比较大，响应时间要是那种不是非常高的场景<br/>
 * </p>
 *
 * @author Winjay
 * @date 2019-10-24
 */
public class SoundPoolUtil {
    private final String TAG = SoundPoolUtil.class.getSimpleName();
    private Context mContext;
    private SoundPool mSoundPool;
    private int mSoundID;
    private int mStreamID;
    private String mDirPath;

    private MediaPlayer mediaPlayer;
    private long startTime = 0;
    private int duration = 0;
    private boolean isInPreparation = false;
    private boolean shouldStop = false;

    private OnPlayerStatusListener mOnPlayerStatusListener;

    private volatile static SoundPoolUtil instance;

    public static final SoundPoolUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SoundPoolUtil.class) {
                if (instance == null) {
                    instance = new SoundPoolUtil(context);
                }
            }
        }
        return instance;
    }

    private SoundPoolUtil(Context context) {
        mContext = context;
        mDirPath = FileUtil.getCacheAbsolutePath(context) + "SoundPool" + File.separator;
        init();
    }

    private void init() {
        LogUtil.d(TAG, "init()");
        mediaPlayer = new MediaPlayer();
        // sdk版本21是SoundPool的一个分水岭
        if (Build.VERSION.SDK_INT >= 21) {
            LogUtil.d(TAG, "SDK_INT >= 21");
            SoundPool.Builder builder = new SoundPool.Builder();
            // AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            // 设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            // 传入最多播放音频数量,
            builder.setMaxStreams(1);
            // 加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            mSoundPool = builder.build();
        } else {
            LogUtil.d(TAG, "SDK_INT < 21");
            // 第一个参数：int maxStreams：SoundPool对象的最大并发流数
            // 第二个参数：int streamType：AudioManager中描述的音频流类型
            // 第三个参数：int srcQuality：采样率转换器的质量。 目前没有效果。 使用0作为默认值。
            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                LogUtil.d(TAG, "<<< onLoadComplete status:" + status);
                if (status == 0) {
                    LogUtil.d(TAG, "onLoadComplete()");
                    if (shouldStop) {
                        LogUtil.d(TAG, "soundPool should stop!");
                        shouldStop = false;
                    } else {
                        // 第一个参数soundID
                        // 第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
                        // 第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
                        // 第四个参数priority为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
                        // 第五个参数loop为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
                        // 第六个参数rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
                        mStreamID = mSoundPool.play(mSoundID, 1, 1, 1, 0, 1);
                        LogUtil.d(TAG, "onLoadComplete():mStreamID=" + mStreamID + ", mSoundID=" + mSoundID);
                        // load完一个音效，unload它，再load后，soundID会增加，待增到300左右时音效放不出，play返回的streamid为0.只能release掉后再new一个soundpool。
                        if (mStreamID == 0) {
                            int replaySoundID = mSoundID;
                            release();
                            getInstance(mContext);
                            mStreamID = mSoundPool.play(replaySoundID, 1, 1, 1, 0, 1);
                            if (mStreamID == 0) {
                                if (mOnPlayerStatusListener != null) {
                                    mOnPlayerStatusListener.onError();
                                    return;
                                }
                            }
                        }

                        if (mOnPlayerStatusListener != null) {
                            mOnPlayerStatusListener.onStart();
                        }
                        HandlerManager.getInstance().postDelayedOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mOnPlayerStatusListener != null) {
                                    mOnPlayerStatusListener.onCompletion();
                                }
                            }
                        }, duration + 1000);
                    }
                }
            }
        });
    }

    /**
     * 播放在线音频
     *
     * @param url
     */
    public void play(String url) {
        LogUtil.d(TAG, "play()_url=" + url);
        shouldStop = false;
        if (mSoundPool == null || mediaPlayer == null) {
            LogUtil.d(TAG, "init again!");
            init();
        }
        if (!TextUtils.isEmpty(url)) {
            String path = mDirPath + getFileName(url);
            boolean fileExists = FileUtil.isFileExists(path);
            if (fileExists) {
                LogUtil.d(TAG, "文件已经存在！");
                playSound(path);
            } else {
                playByMediaPlayer(url);
                LogUtil.d(TAG, "文件不存在，需要下载！");
                downloadFile(url);
            }
        }
    }

    private void playByMediaPlayer(String url) {
        LogUtil.d(TAG, "use mediaPlayer!");
        LogUtil.d(TAG, "isInPreparation=" + isInPreparation + ", isPlaying=" + mediaPlayer.isPlaying());
        if (!isInPreparation && !mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(mContext, Uri.parse(url));
                isInPreparation = true;
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    LogUtil.d(TAG, "onPrepared()");
                    isInPreparation = false;
                    if (shouldStop) {
                        LogUtil.d(TAG, "mediaPlayer should stop!");
                        shouldStop = false;
                    } else {
                        startTime = System.currentTimeMillis();
                        mediaPlayer.start();
                        if (mOnPlayerStatusListener != null) {
                            mOnPlayerStatusListener.onStart();
                        }
                    }
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    LogUtil.d(TAG, "onCompletion()");
                    duration = (int) (System.currentTimeMillis() - startTime) / 1000;
                    LogUtil.d(TAG, "duration=" + duration);
                    startTime = 0;
                    if (mOnPlayerStatusListener != null) {
                        mOnPlayerStatusListener.onCompletion();
                    }
//                    mediaPlayer.stop();
//                    mediaPlayer.reset();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    LogUtil.d(TAG, "onError()");
                    mediaPlayer.reset();
                    if (mOnPlayerStatusListener != null) {
                        mOnPlayerStatusListener.onError();
                    }
                    return false;
                }
            });
        }
    }

    private String getFileName(String url) {
        String fileName = "";
        if (!TextUtils.isEmpty(url)) {
            int i = url.lastIndexOf('/');
            if (i != -1) {
                fileName = url.substring(i);
            }
        }
        return fileName;
    }

    /**
     * 下载在线音频
     *
     * @param url
     */
    public void downloadFile(String url) {
        String fileName = getFileName(url);
        LogUtil.d(TAG, "downloadFile()_url=" + url + ",fileName=" + fileName);
        RetrofitManager.getInstance().download(url, mDirPath, fileName, new DoanloadCallback() {
            @Override
            public void onSuccess(File file) {
                LogUtil.d(TAG, "onSuccess()_file=" + file.toString());
//                playSound(file.getAbsolutePath());
            }

            @Override
            public void onFailure(Throwable error) {
                LogUtil.d(TAG, "onFailure()_error=" + error.getMessage());
            }

            @Override
            public void onLoading(long total, long progress, boolean done) {
//                LogUtil.d(TAG, "onLoading()_done=" + done);
            }
        });
    }

    private void playSound(String path) {
        LogUtil.d(TAG, "playSound()_path=" + path);
        getDuration(path);
        // 可以通过四种途径来加载一个音频资源：
        // 1.通过一个AssetFileDescriptor对象
        // int load(AssetFileDescriptor afd, int priority)
        // 2.通过一个资源ID
        // int load(Context context, int resId, int priority)
        // 3.通过指定的路径加载
        // int load(String path, int priority)
        // 4.通过FileDescriptor加载
        // int load(FileDescriptor fd, long offset, long length, int priority)
        // 声音ID 加载音频资源,这里用的是第二种，第三个参数为priority，声音的优先级*API中指出，priority参数目前没有效果，建议设置为1。
        mSoundID = mSoundPool.load(path, 1);
    }

    private int getDuration(String path) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.setOnPreparedListener(null);
                mediaPlayer.setOnCompletionListener(null);
                mediaPlayer.setOnErrorListener(null);
                mediaPlayer.prepare();
                duration = mediaPlayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.d(TAG, "getDuration():duration=" + duration);
        return duration;
    }

    public void playSoundFromAssets(String fileName, int duration) {
        LogUtil.d(TAG, "<<< playSoundFromAssets fileName : " + fileName);
        if (TextUtils.isEmpty(fileName)) {
            LogUtil.e(TAG, "playSoundFromAssets error fileName is null");
            return;
        }
        this.duration = duration;
        try {
            AssetFileDescriptor assetFileDescriptor = mContext.getAssets().openFd(fileName);
            if (mSoundPool != null && assetFileDescriptor != null) {
                mSoundID = mSoundPool.load(assetFileDescriptor, 1);
                try {
                    assetFileDescriptor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                LogUtil.e(TAG, "mSoundPool is null");
            }
            LogUtil.d(TAG, "playSoundFromAssets mSoundID:" + mSoundID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放Assets目录下音频
     *
     * @param fileName Assets目录完整路径
     */
    public void playSoundFromAssets(String fileName) {
        playSoundFromAssets(fileName, 0);
    }

    public void pause() {
        if (mSoundPool != null) {
            LogUtil.d(TAG, "pause()");
            mSoundPool.autoPause();
        }
    }

    public void resume() {
        if (mSoundPool != null) {
            LogUtil.d(TAG, "resume()");
            mSoundPool.autoResume();
        }
    }

    public void stop() {
        LogUtil.d(TAG, "stop()");
        shouldStop = true;
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                LogUtil.d(TAG, "mediaPlayer pause");
//                mediaPlayer.pause();
//                mediaPlayer.stop();
                mediaPlayer.reset();
                if (mOnPlayerStatusListener != null) {
                    mOnPlayerStatusListener.onStop();
                }
            }
        }
        if (mSoundPool != null && mStreamID != 0) {
            LogUtil.d(TAG, "SoundPool stop, StreamID=" + mStreamID);
            mSoundPool.stop(mStreamID);
            if (mOnPlayerStatusListener != null) {
                mOnPlayerStatusListener.onStop();
            }
        }
    }

    public void clearCache() {
        LogUtil.d(TAG, "clearCache()_mDirPath=" + mDirPath);
        FileUtil.delete(mDirPath);
    }

    public void release() {
        LogUtil.d(TAG, "soundpool release()");
        duration = 0;
        startTime = 0;
        isInPreparation = false;
        shouldStop = false;
        if (mSoundPool != null) {
            mSoundPool.autoPause();
            mSoundPool.unload(mSoundID);
            mSoundID = 0;
            mStreamID = 0;
            mSoundPool.release();
            mSoundPool = null;
        }
        if (mediaPlayer != null) {
//            mediaPlayer.reset();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mOnPlayerStatusListener != null) {
            mOnPlayerStatusListener = null;
        }

        instance = null;
    }

    public interface OnPlayerStatusListener {
        void onStart();

        void onStop();

        void onCompletion();

        void onError();
    }

    public void setOnPlayerStatusListener(OnPlayerStatusListener mOnPlayerStatusListener) {
        this.mOnPlayerStatusListener = mOnPlayerStatusListener;
    }
}
