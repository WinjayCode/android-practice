package com.winjay.practice.media.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Size;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.bean.AudioBean;
import com.winjay.practice.media.interfaces.SourceType;
import com.winjay.practice.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 音乐播放器
 *
 * @author Winjay
 * @date 2021-02-05
 */
public class MusicPlayActivity2 extends BaseActivity {
    private static final String TAG = MusicPlayActivity2.class.getSimpleName();
    private Context mContext;

    @BindView(R.id.music_title_tv)
    TextView mMusicTitleTV;

    @BindView(R.id.play_pause_iv)
    ImageView play_pause_iv;

    @BindView(R.id.album_iv)
    ImageView album_iv;

    private MediaBrowserCompat mMediaBrowser = null;
    private MediaControllerCompat mMediaController = null;
    private final int CMD_PLAY = 1;
    private final int CMD_PAUSE = 2;
    private final int CMD_PRV = 3;
    private final int CMD_NEXT = 4;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_music_play;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        connectMediaBrowser();
    }

    private void connectMediaBrowser() {
        if (null != mMediaBrowser) {
            disconnectMediaBrowser();
        }

        Bundle bundle = new Bundle();

//        if (getIntent().hasExtra("audio")) {
//            AudioBean audio = (AudioBean) getIntent().getParcelableExtra("audio");
//            if (audio != null && !TextUtils.isEmpty(audio.getPath())) {
//                bundle.putParcelable("audio", audio);
//            }
//        } else {
//            getAssetsSource();
//            if (mAssetsMusicList.length > 0) {
//                setAssetsSource();
//            }
//        }

        mMediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicPlayBrowserService.class),
                mConnectionCallback, null);
        mMediaBrowser.connect();
    }

    private void disconnectMediaBrowser() {
        if (null != mMediaBrowser) {
            mMediaBrowser.disconnect();
            mMediaBrowser = null;
        }
    }

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            LogUtil.d(TAG, "SessionToken=" + mMediaBrowser.getSessionToken());

            mMediaController = new MediaControllerCompat(mContext, mMediaBrowser.getSessionToken());
            mMediaController.registerCallback(mMediaControllerCallback);

            mMediaBrowser.subscribe(mMediaBrowser.getRoot(), mMediaBrowserSubscriptionCallback);
        }

        @Override
        public void onConnectionFailed() {
            LogUtil.d(TAG);
        }

        @Override
        public void onConnectionSuspended() {
            LogUtil.d(TAG);
        }
    };

    private final MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            LogUtil.d(TAG);
            invalidController();
        }

        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            LogUtil.d(TAG, "state=" + state);

            if (state != null) {
                if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    play_pause_iv.setImageResource(android.R.drawable.ic_media_pause);
                } else if (state.getState() == PlaybackStateCompat.STATE_PAUSED) {
                    play_pause_iv.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            LogUtil.d(TAG, "metadata=" + metadata);

//            mMusicTitleTV.setText(audio.getDisplayName());
//            loadCover(audio.getPath());
////                loadThumbnail(audio.getUri());
        }
    };

    private void invalidController() {
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mMediaControllerCallback);
        }
        mMediaController = null;
    }

    private final MediaBrowserCompat.SubscriptionCallback mMediaBrowserSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                                     @NonNull List<MediaBrowserCompat.MediaItem> children) {
            LogUtil.d(TAG);
            mMediaController.getTransportControls().prepare();
        }
    };

    private void musicControlCmd(int cmd) {
        if (null == mMediaController || null == mMediaController.getTransportControls()) {
            LogUtil.w(TAG, "mediaSession is not ready");
            return;
        }
        LogUtil.d(TAG, "cmd=" + cmd);
        MediaControllerCompat.TransportControls transportControls = mMediaController.getTransportControls();
        switch (cmd) {
            case CMD_PLAY:
                transportControls.play();
                break;
            case CMD_PAUSE:
                transportControls.pause();
                break;
            case CMD_PRV:
                transportControls.skipToPrevious();
                break;
            case CMD_NEXT:
                transportControls.skipToNext();
                break;
        }
    }

    @OnClick(R.id.play_pause_iv)
    void playPauseSwitch() {
        if (mMediaController == null || mMediaController.getPlaybackState() == null) {
            return;
        }
        if (mMediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            musicControlCmd(CMD_PAUSE);
        } else {
            musicControlCmd(CMD_PLAY);
        }
    }

    @OnClick(R.id.prev_iv)
    void prevClick() {
        musicControlCmd(CMD_PRV);
    }

    @OnClick(R.id.next_iv)
    void nextClick() {
        musicControlCmd(CMD_NEXT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 断开连接后音乐服务是否销毁？
        disconnectMediaBrowser();
    }


    /**
     * 获取音乐资源信息
     *
     * @param filePath
     */
    private void getMusicInfo(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象mmr
        mmr.setDataSource(filePath);//设置mmr对象的数据源为上面file对象的绝对路径
        String ablumString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);//获得音乐专辑的标题
        String artistString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);//获取音乐的艺术家信息
        String titleString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);//获取音乐标题信息
        String mimetypeString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);//获取音乐mime类型
        String durationString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//获取音乐持续时间
        String bitrateString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);//获取音乐比特率，位率
        String dateString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);//获取音乐的日期
        LogUtil.d(TAG, "ablumString=" + ablumString
                + "\n" + "artistString=" + artistString
                + "\n" + "titleString=" + titleString
                + "\n" + "mimetypeString=" + mimetypeString
                + "\n" + "durationString=" + durationString
                + "\n" + "bitrateString=" + bitrateString
                + "\n" + "dateString=" + dateString);

        /* 设置文本的内容 */
//        ablum.setText("专辑标题为："+ablumString);
//        artist.setText("艺术家名称为："+artistString);
//        title.setText("音乐标题为："+titleString);
//        mimetype.setText("音乐的MIME类型为："+mimetypeString);
//        duration.setText("duration为："+durationString);
//        bitrate.setText("bitrate为："+bitrateString);
//        date.setText("date为："+dateString);
    }

    /**
     * 加载专辑图片
     *
     * @param path 资源路径
     */
    private void loadCover(String path) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte[] cover = mediaMetadataRetriever.getEmbeddedPicture();
        if (cover.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(cover, 0, cover.length);
            album_iv.setImageBitmap(bitmap);
        }
    }

    /**
     * 加载专辑缩略图片（只适合小缩略图显示，否则会看起来很模糊）
     *
     * @param uri contentUri (Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);)
     */
    private void loadThumbnail(Uri uri) {
        if (uri != null) {
            try {
                album_iv.setImageBitmap(getContentResolver().loadThumbnail(uri, new Size(200, 200), null));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
