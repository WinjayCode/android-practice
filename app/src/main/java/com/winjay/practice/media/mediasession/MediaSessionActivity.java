package com.winjay.practice.media.mediasession;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Size;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.mediasession.client.MediaBrowserHelper;
import com.winjay.practice.media.mediasession.service.MediaBrowserService;
import com.winjay.practice.media.ui.MediaSeekBar;
import com.winjay.practice.utils.LogUtil;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * MediaSession客户端
 *
 * @author Winjay
 * @date 2023-02-20
 */
public class MediaSessionActivity extends BaseActivity {
    private static final String TAG = MediaSessionActivity.class.getSimpleName();

    @BindView(R.id.music_title_tv)
    TextView mMusicTitleTV;

    @BindView(R.id.music_artist_tv)
    TextView music_artist_tv;

    @BindView(R.id.play_pause_iv)
    ImageView play_pause_iv;

    @BindView(R.id.album_iv)
    ImageView album_iv;

    @BindView(R.id.media_seek_bar)
    MediaSeekBar media_seek_bar;

    private MediaBrowserHelper mMediaBrowserHelper;
    private boolean mIsPlaying;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_music_play;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMediaBrowserHelper = new MediaBrowserConnection(this);
        mMediaBrowserHelper.registerMediaControllerCallback(mMediaControllerCallback);
        mMediaBrowserHelper.onStart();
    }

    private class MediaBrowserConnection extends MediaBrowserHelper {
        private MediaBrowserConnection(Context context) {
            super(context, context.getPackageName(), MediaBrowserService.class.getName());
        }

        @Override
        protected void onConnected() {
            media_seek_bar.setMediaController(getMediaController());
        }

        @Override
        protected void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);

            final MediaControllerCompat mediaController = getMediaController();

            // Queue up all media items for this simple sample.
//            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
//                mediaController.addQueueItem(mediaItem.getDescription());
//            }

            // Call prepare now so pressing play just works.
            mediaController.getTransportControls().prepare();
        }
    }

    private final MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
            if (playbackState == null) {
                return;
            }
            LogUtil.d(TAG, "state=" + playbackState.getState());
            mIsPlaying = playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
            if (mIsPlaying) {
                play_pause_iv.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                play_pause_iv.setImageResource(android.R.drawable.ic_media_play);
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
            if (mediaMetadata == null) {
                return;
            }
            mMusicTitleTV.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            music_artist_tv.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
//            mAlbumArt.setImageBitmap(MusicLibrary.getAlbumBitmap(
//                    MainActivity.this,
//                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)));
        }
    };

    @OnClick(R.id.play_pause_iv)
    void playPauseSwitch() {
        if (mIsPlaying) {
            mMediaBrowserHelper.getTransportControls().pause();
        } else {
            mMediaBrowserHelper.getTransportControls().play();
        }
    }

    @OnClick(R.id.prev_iv)
    void prevClick() {
        mMediaBrowserHelper.getTransportControls().skipToPrevious();
    }

    @OnClick(R.id.next_iv)
    void nextClick() {
        mMediaBrowserHelper.getTransportControls().skipToNext();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        media_seek_bar.disconnectController();
        mMediaBrowserHelper.onStop();
    }


    /**
     * 获取音乐资源信息
     *
     * @param filePath
     */
    private void getMusicInfo(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象mmr
        mmr.setDataSource(filePath);//设置mmr对象的数据源为上面file对象的绝对路径
        String albumString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);//获得音乐专辑的标题
        String artistString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);//获取音乐的艺术家信息
        String titleString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);//获取音乐标题信息
        String mimetypeString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);//获取音乐mime类型
        String durationString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//获取音乐持续时间
        String bitrateString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);//获取音乐比特率，位率
        String dateString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);//获取音乐的日期
        LogUtil.d(TAG, "albumString=" + albumString
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
