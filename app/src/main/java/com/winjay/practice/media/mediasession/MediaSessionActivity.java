package com.winjay.practice.media.mediasession;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.mediasession.client.MediaBrowserHelper;
import com.winjay.practice.media.mediasession.service.MediaBrowserService;
import com.winjay.practice.media.ui.Media3SeekBar;
import com.winjay.practice.media.ui.MediaSeekBar;
import com.winjay.practice.utils.LogUtil;
import com.winjay.practice.utils.MediaUtil;

import java.util.List;

/**
 * MediaSession客户端
 *
 * @author Winjay
 * @date 2023-02-20
 */
public class MediaSessionActivity extends BaseActivity {
    private static final String TAG = MediaSessionActivity.class.getSimpleName();

    TextView mMusicTitleTV;

    TextView music_artist_tv;

    ImageView play_pause_iv;

    ImageView album_iv;

    MediaSeekBar media_seek_bar;

    Media3SeekBar media3_seek_bar;

    TextView position_tv;

    TextView duration_tv;

    private MediaBrowserHelper mMediaBrowserHelper;
    private String mCustomAction;
    private boolean mIsPlaying;

    private final Handler updatePositionHandler = new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_music_play;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMusicTitleTV = findViewById(R.id.music_title_tv);
        music_artist_tv = findViewById(R.id.music_artist_tv);
        play_pause_iv = findViewById(R.id.play_pause_iv);
        album_iv = findViewById(R.id.album_iv);
        media_seek_bar = findViewById(R.id.media_seek_bar);
        media3_seek_bar = findViewById(R.id.media3_seek_bar);
        position_tv = findViewById(R.id.position_tv);
        duration_tv = findViewById(R.id.duration_tv);
        media3_seek_bar.setVisibility(View.GONE);

        mMediaBrowserHelper = new MediaBrowserConnection(this);
        mMediaBrowserHelper.registerMediaControllerCallback(mMediaControllerCallback);
        mMediaBrowserHelper.onStart();

        findViewById(R.id.play_pause_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseSwitch();
            }
        });
        findViewById(R.id.prev_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevClick();
            }
        });
        findViewById(R.id.next_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextClick();
            }
        });
    }

    private class MediaBrowserConnection extends MediaBrowserHelper {
        private MediaBrowserConnection(Context context) {
            super(context, context.getPackageName(), MediaBrowserService.class.getName());
        }

        @Override
        protected void onConnected() {
            media_seek_bar.setMediaController(getMediaController());

            // 获取服务端所有自定义Action
            for (PlaybackStateCompat.CustomAction customAction : getPlaybackState().getCustomActions()) {
                LogUtil.d(TAG, "" + customAction.getAction());
                mCustomAction = customAction.getAction();
            }
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

                updatePositionHandler.removeCallbacks(mUpdatePositionRunnable);
                updatePositionHandler.post(mUpdatePositionRunnable);
            } else {
                play_pause_iv.setImageResource(android.R.drawable.ic_media_play);

                updatePositionHandler.removeCallbacks(mUpdatePositionRunnable);
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
            if (mediaMetadata == null) {
                return;
            }
            mMusicTitleTV.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            music_artist_tv.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

            long durationOnMs = mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            duration_tv.setText(MediaUtil.formatDuration(durationOnMs));

            Uri albumArtUri = Uri.parse(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
            if (albumArtUri == Uri.EMPTY) {
                album_iv.setImageResource(R.mipmap.icon);
            } else {
                Glide.with(MediaSessionActivity.this)
                        .load(albumArtUri)
                        .into(album_iv);
            }
        }
    };

    private final Runnable mUpdatePositionRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                position_tv.setText(MediaUtil.formatDuration(mMediaBrowserHelper.getPlaybackState().getPosition()));
                updatePositionHandler.postDelayed(mUpdatePositionRunnable, 1000);
            } catch (Exception e) {
                updatePositionHandler.removeCallbacks(this);
            }
        }
    };

    void playPauseSwitch() {
        if (mIsPlaying) {
            mMediaBrowserHelper.getTransportControls().pause();
        } else {
            mMediaBrowserHelper.getTransportControls().play();
        }

        // test customAction
        mMediaBrowserHelper.getTransportControls().sendCustomAction(mCustomAction, null);
    }

    void prevClick() {
        mMediaBrowserHelper.getTransportControls().skipToPrevious();
    }

    void nextClick() {
        mMediaBrowserHelper.getTransportControls().skipToNext();
    }

    @Override
    protected void onDestroy() {
        updatePositionHandler.removeCallbacks(mUpdatePositionRunnable);
        mMediaBrowserHelper.getTransportControls().stop();
        media_seek_bar.disconnectController();
        mMediaBrowserHelper.onStop();

        super.onDestroy();
    }
}
