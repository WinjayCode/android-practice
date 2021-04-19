package com.winjay.practice.media.extractor_muxer;

import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.utils.FileUtil;
import com.winjay.practice.utils.LogUtil;

import java.io.File;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * MediaExtractor 的基础使用，并分离视频轨和音频轨
 * <p>
 * MediaMuxer 的基础使用，并合成新视频
 *
 * @author Winjay
 * @date 2021-04-14
 */
public class MediaExtractorAndMuxerActivity extends BaseActivity {
    private static final String TAG = MediaExtractorAndMuxerActivity.class.getSimpleName();

    private static final String VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.MP4";
    private static final String AUDIO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.M4A";
    private static String mDestPath;

    @BindView(R.id.video_info_tv)
    TextView videoInfoTV;

    private MyMediaExtractor myMediaExtractor;

    private MyMediaMuxer mMyMediaMuxer;

    private StringBuilder sb;

    @Override
    protected int getLayoutId() {
        return R.layout.extractor_muxer_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDestPath = getExternalCacheDir() + File.separator + "mixVideo";

        sb = new StringBuilder();

        File file = new File(VIDEO_PATH);
        if (file.exists()) {
            sb.append("视频名称：" + file.getName()).append("\n");
            myMediaExtractor = new MyMediaExtractor(VIDEO_PATH);
            sb.append("视频轨道数：" + myMediaExtractor.getTrackCount()).append("\n");
            sb.append(Arrays.toString(myMediaExtractor.getMimeType())).append("\n");
            sb.append("视频宽高：" + myMediaExtractor.getVideoWH()[0] + "x"
                    + myMediaExtractor.getVideoWH()[1]).append("\n");
            sb.append("视频时间：" + myMediaExtractor.getVideoDuration() + "分钟").append("\n");
            sb.append("视频帧率：" + myMediaExtractor.getVideoFrameRate() + "fps").append("\n");
        } else {
            toast("测试视频不存在！");
        }

        File audioFile = new File(AUDIO_PATH);
        if (audioFile.exists()) {
            sb.append("\n音频名称：" + audioFile.getName()).append("\n");
        }

        videoInfoTV.setText(sb.toString());

        mMyMediaMuxer = new MyMediaMuxer.Builder()
                .setVideoSource(VIDEO_PATH)
                .setAudioSource(AUDIO_PATH)
                .setDestPath(mDestPath)
                .setMuxerType(MyMediaMuxer.MIX_TRACK_IN_VIDEO_AND_AUDIO)
                .build();

//        mMyMediaMuxer = new MyMediaMuxer(VIDEO_PATH, mDestPath);
        mMyMediaMuxer.setMuxerListener(new MyMediaMuxer.MuxerListener() {
            @Override
            public void onStart() {
                LogUtil.d(TAG);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sb.append("\n开始生成新视频，请稍等~~~").append("\n");
                        videoInfoTV.setText(sb.toString());
                    }
                });
            }

            @Override
            public void onSuccess(String filePath) {
                LogUtil.d(TAG);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sb.append("视频生成完成，路径为：" + filePath).append("\n");
                        videoInfoTV.setText(sb.toString());
                    }
                });
            }

            @Override
            public void onFail(String errorMsg) {
                LogUtil.d(TAG);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sb.append("视频生成失败：" + errorMsg).append("\n");
                        videoInfoTV.setText(sb.toString());
                    }
                });
            }
        });
    }

    @OnClick({R.id.muxer_btn})
    void muxer() {
        mMyMediaMuxer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtil.delete(mDestPath);
    }
}
