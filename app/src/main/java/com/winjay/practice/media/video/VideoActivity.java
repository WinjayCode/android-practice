package com.winjay.practice.media.video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.storage.AssetHelper;
import com.winjay.practice.media.bean.VideoBean;
import com.winjay.practice.utils.LogUtil;

import java.io.File;

/**
 * VideoView
 * <p>
 * 播放视频
 * mVideoView.start();
 * <p>
 * 暂停视频
 * mVideoView.pause();
 * <p>
 * 获取播放时间信息
 * mVideoView.getDuration();//获取视频的总时长
 * mVideoView.getCurrentPosition();//获取视频的当前播放位置
 * <p>
 * 停止释放
 * mVideoView.stopPlayback();//停止播放视频,并且释放
 * mVideoView.suspend();//在任何状态下释放媒体播放器
 * <p>
 * 其他Api介绍
 * mVideoView.canPause();　　//是否可以暂停
 * mVideoView.canSeekBackward();　　//视频是否可以向后调整播放位置
 * mVideoView.canSeekForward();　　//视频是否可以向前调整播放位置
 * mVideoView.getBufferPercentage();　　//获取视频缓冲百分比
 * mVideoView.resolveAdjustedSize();　　//获取自动解析后VideoView的大小
 * mVideoView.resume();　　//重新开始播放
 * mVideoView.isPlaying();　　//是否在播放中
 * mVideoView.setMediaController();　　//设置多媒体控制器
 * mVideoView.onKeyDown();　　//发送物理按键值
 * mVideoView.setVideoURI(); 　　//播放网络视频,参数为网络地址
 *
 * @author Winjay
 * @date 2020/11/27
 */
public class VideoActivity extends BaseActivity {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private VideoView mVideoView;

    @Override
    protected int getLayoutId() {
        return R.layout.video_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoView = findViewById(R.id.video_view);

        if (getIntent().hasExtra("video")) {
            VideoBean video = (VideoBean) getIntent().getSerializableExtra("video");
            if (video != null && !TextUtils.isEmpty(video.getPath())) {
                setVideoPath(video.getPath());
            }
        } else {
            File videoFile = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "3G2.3g2");
            // ok
//            File videoFile = new File(getExternalFilesDir(null), "3G2.3g2");
            AssetHelper.Companion.copyAssetSingleFile(this, "3G2.3g2", videoFile);
//            MyMediaCollection myMediaCollection = new MyMediaCollection();
//            myMediaCollection.insertVideoToCollection(this, "MP4.mp4");
//            List<MyMediaCollection.VideoBean> videoBeans = myMediaCollection.queryVideoCollection(this);
//            LogUtil.d(TAG, "video.size=" + videoBeans.size());
//            setVideoUri(videoBeans.get(videoBeans.size() - 1).getUri());
            setVideoPath(videoFile.getAbsolutePath());
        }
    }

    private void setVideoPath(String path) {
////        File file = new File(Environment.getExternalStorageDirectory() + "/Movies/", "3G2.3g2");
////        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "3G2.3g2");
//        File file = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "3G2.3g2");
////        File file = new File("/sdcard/Movies/M4V.m4v");
//        LogUtil.d(TAG, "11path=" + file.getAbsolutePath());
//        if (!file.exists()) {
//            toast("视频不存在");
//            finish();
//            return;
//        }
//        path = file.getAbsolutePath();
//
        LogUtil.d(TAG, "path=" + path);

        mVideoView.setVideoPath(path);//设置视频文件
        playVideo();
    }

    private void setVideoUri(Uri uri) {
        mVideoView.setVideoURI(uri);
        playVideo();
    }

    private void playVideo() {
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtil.i(TAG, "onPrepared()");
                //视频加载完成,准备好播放视频的回调
                mVideoView.start();
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e(TAG, "onError():what=" + what + ", extra=" + extra);
                switch (what) {
                    case -1004:
                        LogUtil.d(TAG, "MEDIA_ERROR_IO");
                        break;
                    case -1007:
                        LogUtil.d(TAG, "MEDIA_ERROR_MALFORMED");
                        break;
                    case 200:
                        LogUtil.d(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                        break;
                    case 100:
                        LogUtil.d(TAG, "MEDIA_ERROR_SERVER_DIED");
                        break;
                    case -110:
                        LogUtil.d(TAG, "MEDIA_ERROR_TIMED_OUT");
                        break;
                    case 1:
                        LogUtil.d(TAG, "MEDIA_ERROR_UNKNOWN");
                        break;
                    case -1010:
                        LogUtil.d(TAG, "MEDIA_ERROR_UNSUPPORTED");
                        break;
                }
                switch (extra) {
                    case 800:
                        LogUtil.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING");
                        break;
                    case 702:
                        LogUtil.d(TAG, "MEDIA_INFO_BUFFERING_END");
                        break;
                    case 701:
                        LogUtil.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                        break;
                    case 802:
                        LogUtil.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                        break;
                    case 801:
                        LogUtil.d(TAG, "MEDIA_INFO_NOT_SEEKABLE");
                        break;
                    case 1:
                        LogUtil.d(TAG, "MEDIA_INFO_UNKNOWN");
                        break;
                    case 3:
                        LogUtil.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                        break;
                    case 700:
                        LogUtil.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                        break;
                }
                //异常回调
                return false;//如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtil.i(TAG, "onCompletion()");
                //视频播放完成后的回调

            }
        });
        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                LogUtil.d(TAG, "onInfo():what=" + what + ", extra=" + extra);
                if (what == 805 && extra == -1007) {
                    LogUtil.w(TAG, "Video coding anomaly, fast forward 5 sec!");
                    LogUtil.d(TAG, "isPlaying=" + mVideoView.isPlaying());
                    LogUtil.d(TAG, "current position=" + mp.getCurrentPosition());
                    int currentPosition = mp.getCurrentPosition();
                    mVideoView.stopPlayback();
                    mVideoView.suspend();
                    mVideoView.resume();
                    mVideoView.seekTo(currentPosition + 5 * 1000);
                }
                //信息回调
//                what 对应返回的值如下
//                public static final int MEDIA_INFO_UNKNOWN = 1;  媒体信息未知
//                public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700; 媒体信息视频跟踪滞后
//                public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3; 媒体信息\视频渲染\开始
//                public static final int MEDIA_INFO_BUFFERING_START = 701; 媒体信息缓冲启动
//                public static final int MEDIA_INFO_BUFFERING_END = 702; 媒体信息缓冲结束
//                public static final int MEDIA_INFO_NETWORK_BANDWIDTH = 703; 媒体信息网络带宽（703）
//                public static final int MEDIA_INFO_BAD_INTERLEAVING = 800; 媒体-信息-坏-交错
//                public static final int MEDIA_INFO_NOT_SEEKABLE = 801; 媒体信息找不到
//                public static final int MEDIA_INFO_METADATA_UPDATE = 802; 媒体信息元数据更新
//                public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901; 媒体信息不支持字幕
//                public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902; 媒体信息字幕超时

                return true; //如果方法处理了信息，则为true；如果没有，则为false。返回false或根本没有OnInfoListener，将导致丢弃该信息。
            }
        });

        mVideoView.setMediaController(new MediaController(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
        mVideoView.suspend();
    }
}
