package com.winjay.practice.media.codec.decode;

import android.graphics.SurfaceTexture;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.codec.decode.async.AsyncAudioDecode;
import com.winjay.practice.media.codec.decode.async.AsyncVideoDecode;
import com.winjay.practice.media.codec.decode.sync.SyncAudioDecode;
import com.winjay.practice.media.codec.decode.sync.SyncVideoDecode;
import com.winjay.practice.media.extractor_muxer.MyMediaExtractor;
import com.winjay.practice.utils.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 解码音视频
 *
 * @author Winjay
 * @date 2021-04-23
 */
public class DecodeMediaActivity extends BaseActivity {
    private static final String TAG = DecodeMediaActivity.class.getSimpleName();
    private static final String VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.MP4";
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(2);
    private SyncVideoDecode syncVideoDecode;
    private SyncAudioDecode syncAudioDecode;
    private AsyncVideoDecode asyncVideoDecode;
    private AsyncAudioDecode asyncAudioDecode;

    @BindView(R.id.texture_view)
    TextureView mTextureView;

    @Override
    protected int getLayoutId() {
        return R.layout.decode_media_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                LogUtil.d(TAG, "surface width*height=" + width + "*" + height);
                MyMediaExtractor myMediaExtractor = new MyMediaExtractor(VIDEO_PATH);
                ViewGroup.LayoutParams layoutParams = mTextureView.getLayoutParams();
                layoutParams.width = myMediaExtractor.getVideoFormat().getInteger(MediaFormat.KEY_WIDTH);
                layoutParams.height = myMediaExtractor.getVideoFormat().getInteger(MediaFormat.KEY_HEIGHT);
                LogUtil.d(TAG, "width*height=" + layoutParams.width + "*" + layoutParams.height);
                mTextureView.setLayoutParams(layoutParams);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    @OnClick(R.id.sync_decode_btn)
    void syncDecode() {
        stopMedia();
        syncVideoDecode = new SyncVideoDecode(VIDEO_PATH, mTextureView.getSurfaceTexture());
        syncAudioDecode = new SyncAudioDecode(VIDEO_PATH);
        mExecutorService.execute(syncVideoDecode);
        mExecutorService.execute(syncAudioDecode);
    }

    @OnClick(R.id.async_decode_btn)
    void asyncDecode() {
        stopMedia();
        asyncVideoDecode = new AsyncVideoDecode(VIDEO_PATH, mTextureView.getSurfaceTexture());
        asyncVideoDecode.start();
        asyncAudioDecode = new AsyncAudioDecode(VIDEO_PATH);
        asyncAudioDecode.start();
    }

    private void stopMedia() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
        }
        if (syncVideoDecode != null) {
            syncVideoDecode.release();
        }
        if (syncAudioDecode != null) {
            syncAudioDecode.release();
        }
        if (asyncVideoDecode != null) {
            asyncVideoDecode.release();
        }
        if (asyncAudioDecode != null) {
            asyncAudioDecode.release();
        }
    }
}
