package com.winjay.practice.media.mediasession.service;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.winjay.practice.Constants;
import com.winjay.practice.media.audio_focus.AudioFocusManager;
import com.winjay.practice.media.bean.AudioBean;
import com.winjay.practice.media.interfaces.IMediaStatus;
import com.winjay.practice.media.interfaces.MediaType;
import com.winjay.practice.media.mediasession.players.MediaPlayerAdapter;
import com.winjay.practice.media.mediasession.players.PlaybackInfoListener;
import com.winjay.practice.media.mediasession.players.PlayerAdapter;
import com.winjay.practice.media.receiver.MediaNotificationReceiver;
import com.winjay.practice.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 音乐播放器媒体浏览器服务
 * <p>
 * assets目录下的资源无法直接获取资源本身信息(最好拷贝到具体目录下)
 *
 * @author Winjay
 * @date 2023-02-21
 */
public class MusicBrowserService extends MediaBrowserServiceCompat implements IMediaStatus {
    private static final String TAG = "MusicPlayBrowserService";
    private static final String ROOT_ID = "ROOT_ID";
    private static final String MY_MEDIA_ROOT_ID = "MY_MEDIA_ROOT_ID";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "MY_EMPTY_MEDIA_ROOT_ID";
    private static final String CUSTOM_ACTION_THUMBS_UP = "CUSTOM_ACTION_THUMBS_UP";
    private Context mContext;

    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mPlaybackState;
    private MediaMetadataCompat.Builder mMediaMetadata;

    private MediaPlayer mMediaPlayer;
    private AudioFocusManager mAudioFocusManager;
    private MediaNotificationReceiver mMediaNotificationReceiver;

    private String mParentId;

    private boolean isPrepare = false;

    private final List<AudioBean> mPlayList = new ArrayList<>();

    private final String assetsDir = "audio";

    private int mCurrentIndex = 0;

    private PlayerAdapter mPlayback;
    private boolean mServiceInStartedState;


    /**
     * 控制客户端链接
     *
     * @param clientPackageName 客户端的packageName
     * @param clientUid         clientUid
     * @param rootHints         从客户端传递过来的Bundle
     * @return BrowserRoot
     */
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        LogUtil.d(TAG, "clientPackageName=" + clientPackageName + ", clientUid=" + clientUid);
        if (rootHints != null && rootHints.getParcelable("audio") != null) {
            LogUtil.d(TAG, "audio.title=" + ((AudioBean) (rootHints.getParcelable("audio"))).getTitle());
        }
        // 通过以上参数来进行判断，若同意连接，则返回BrowserRoot对象，否则返回null;
        // 构造BrowserRoot的第一个参数为rootId(自定义)，第二个参数为Bundle;
        return new BrowserRoot(clientPackageName, null);

//        // (Optional) Control the level of access for the specified package name.
//        if (allowBrowsing(clientPackageName, clientUid)) {
//            // Returns a root ID that clients can use with onLoadChildren() to retrieve
//            // the content hierarchy.
//            return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
//        } else {
//            // Clients can connect, but this BrowserRoot is an empty hierarchy
//            // so onLoadChildren returns nothing. This disables the ability to browse for content.
//            return new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null);
//        }
    }

    /**
     * 播放列表
     *
     * @param parentId parentId
     * @param result   result
     */
    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        LogUtil.d(TAG, "parentId=" + parentId);
        mParentId = parentId;
        // 将信息从当前线程中移除，允许后续调用sendResult方法
//        result.detach();

        //  Browsing not allowed
        if (TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID, parentId)) {
            result.sendResult(null);
            return;
        }

        // 模拟获取数据的过程，真实情况应该是异步从网络或本地读取数据
        getAssetsSource();

        List<MediaBrowserCompat.MediaItem> mediaItems = transformPlayList(mPlayList);

//        MusicNotificationManager.getInstance(this).setMediaData(audioBean);

        // Check if this is the root menu:
        if (MY_MEDIA_ROOT_ID.equals(parentId)) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems);
    }

    private ArrayList<MediaBrowserCompat.MediaItem> transformPlayList(List<AudioBean> audioBeanList) {
        ArrayList<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        for (int i = 0; i < audioBeanList.size(); i++) {
            MediaMetadataCompat metadata = transformAudioBean(audioBeanList.get(i));
            mediaItems.add(createMediaItem(metadata));
        }
        return mediaItems;
    }

    private MediaMetadataCompat transformAudioBean(AudioBean bean) {
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        if (!TextUtils.isEmpty(bean.getTitle())) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, bean.getTitle());
        }
        return builder.build();

//        return new MediaMetadataCompat.Builder()
//                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + bean.mediaId)
//                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, bean.tilte)
//                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, bean.artist)
//                .build();
    }

    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata) {
        return new MediaBrowserCompat.MediaItem(metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG);
        mContext = this;

        mMediaSession = new MediaSessionCompat(this, TAG);
        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mPlaybackState = new PlaybackStateCompat.Builder().setActions(
                        PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE)
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f);
        mMediaSession.setPlaybackState(mPlaybackState.build());

        mMediaSession.setCallback(mMediaSessionCallback);
        setSessionToken(mMediaSession.getSessionToken());

        mMediaSession.setActive(true);

        mPlaybackState = new PlaybackStateCompat.Builder();
        // 收藏按钮
//        mPlaybackState.addCustomAction(new PlaybackStateCompat.CustomAction.Builder(
//                CUSTOM_ACTION_THUMBS_UP, getResources().getString(R.string.thumbs_up), thumbsUpIcon)
//                .setExtras(customActionExtras)
//                .build());
        mMediaMetadata = new MediaMetadataCompat.Builder();

        mMediaPlayer = new MediaPlayer();
        mAudioFocusManager = new AudioFocusManager(this, MediaType.MUSIC);
        mAudioFocusManager.setOnAudioFocusChangeListener(mOnAudioFocusChangeListener);
        mMediaPlayer.setAudioAttributes(mAudioFocusManager.getAudioAttributes());

        registerReceiver();

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtil.d(TAG);
                isPrepare = true;
                mMediaSessionCallback.onPlay();
//                MusicNotificationManager.getInstance(mContext).showMusicNotification(true);
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtil.d(TAG);
                mMediaSessionCallback.onSkipToNext();
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e(TAG, "what=" + what + ", extra=" + extra);
                isPrepare = false;
                mMediaPlayer.reset();
                return false;
            }
        });


        mPlayback = new MediaPlayerAdapter(this, new MediaPlayerListener());
    }

    public class MediaPlayerListener extends PlaybackInfoListener {

        private final ServiceManager mServiceManager;

        MediaPlayerListener() {
            mServiceManager = new ServiceManager();
        }

        @Override
        public void onPlaybackStateChange(PlaybackStateCompat state) {
            // Report the state to the MediaSession.
            mMediaSession.setPlaybackState(state);

            // Manage the started state of this service.
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mServiceManager.moveServiceToStartedState(state);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    mServiceManager.updateNotificationForPause(state);
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    mServiceManager.moveServiceOutOfStartedState(state);
                    break;
            }
        }

        class ServiceManager {

            private void moveServiceToStartedState(PlaybackStateCompat state) {
//                Notification notification =
//                        mMediaNotificationManager.getNotification(
//                                mPlayback.getCurrentMedia(), state, getSessionToken());

                if (!mServiceInStartedState) {
                    ContextCompat.startForegroundService(
                            mContext,
                            new Intent(mContext, MusicBrowserService.class));
                    mServiceInStartedState = true;
                }

//                startForeground(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void updateNotificationForPause(PlaybackStateCompat state) {
                stopForeground(false);
//                Notification notification =
//                        mMediaNotificationManager.getNotification(
//                                mPlayback.getCurrentMedia(), state, getSessionToken());
//                mMediaNotificationManager.getNotificationManager()
//                        .notify(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void moveServiceOutOfStartedState(PlaybackStateCompat state) {
                stopForeground(true);
                stopSelf();
                mServiceInStartedState = false;
            }
        }

    }

    AudioFocusManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioFocusManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    LogUtil.d(TAG, "media:AUDIOFOCUS_GAIN");
                    mMediaSessionCallback.onPlay();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    LogUtil.d(TAG, "media:AUDIOFOCUS_LOSS");
                    mMediaSessionCallback.onStop();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    LogUtil.d(TAG, "media:AUDIOFOCUS_LOSS_TRANSIENT");
                    mMediaSessionCallback.onPause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    LogUtil.d(TAG, "media:AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    mMediaSessionCallback.onPause();
                    break;
            }
        }
    };

    MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPrepare() {
            LogUtil.d(TAG);
            setAssetsSource();
        }

        @Override
        public void onPlay() {
            LogUtil.d(TAG);
            if (isPrepare) {
                if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioFocusManager.requestFocus()) {
                    mMediaPlayer.start();
                }
            }

//            MusicNotificationManager.getInstance(mContext).showMusicNotification(true);
        }

        @Override
        public void onPause() {
            LogUtil.d(TAG);
            mMediaPlayer.pause();
//            MusicNotificationManager.getInstance(mContext).showMusicNotification(false);
        }

        @Override
        public void onStop() {
            LogUtil.d(TAG);
            mMediaPlayer.stop();
            mMediaPlayer.release();

//            MusicNotificationManager.getInstance(mContext).showMusicNotification(false);

            mAudioFocusManager.releaseAudioFocus();
        }

        @Override
        public void onSkipToNext() {
            LogUtil.d(TAG);
            ++mCurrentIndex;
            if (mCurrentIndex > mPlayList.size() - 1) {
                mCurrentIndex = 0;
            }
            onPrepare();
        }

        @Override
        public void onSkipToPrevious() {
            LogUtil.d(TAG);
            --mCurrentIndex;
            if (mCurrentIndex < 0) {
                mCurrentIndex = mPlayList.size() - 1;
            }
            onPrepare();
        }

        @Override
        public void onSeekTo(long pos) {
            mMediaPlayer.seekTo((int) pos);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            LogUtil.d(TAG, "action=" + action);
            if (CUSTOM_ACTION_THUMBS_UP.equals(action)) {
                LogUtil.d(TAG);
            }
        }
    };

    private void getAssetsSource() {
        try {
            String[] mAssetsMusicList = getAssets().list(assetsDir);
            LogUtil.d(TAG, "mAssetsMusicList=" + mAssetsMusicList.length);
            for (String title : mAssetsMusicList) {
                LogUtil.d(TAG, "assetsFile=" + title);
                AudioBean bean = new AudioBean();
                bean.setTitle(title);
                mPlayList.add(bean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setLocalSource(String path) {
        try {
            mMediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAssetsSource() {
        LogUtil.d(TAG, "assets=" + mPlayList.get(mCurrentIndex).getTitle());
        mMediaPlayer.reset();
        try {
            AssetFileDescriptor afd = getAssets().openFd(assetsDir + File.separator + mPlayList.get(mCurrentIndex).getTitle());
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();

        mMediaSession.setMetadata(transformAudioBean(mPlayList.get(mCurrentIndex)));

//        MusicNotificationManager.getInstance(this).setMediaData(audioBean);
    }

    private void registerReceiver() {
        if (mMediaNotificationReceiver == null) {
            mMediaNotificationReceiver = new MediaNotificationReceiver(this);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PREV);
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PAUSE);
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PLAY);
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_NEXT);
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_CLOSE);
        registerReceiver(mMediaNotificationReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        if (mMediaNotificationReceiver != null) {
            unregisterReceiver(mMediaNotificationReceiver);
            mMediaNotificationReceiver = null;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isPrepare = false;

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if (mMediaSession != null) {
            mMediaSession.release();
            mMediaSession = null;
        }

        unregisterReceiver();

        mMediaSession.setActive(false);
        mMediaSession.release();

//        MusicNotificationManager.getInstance(mContext).cancel();
    }

    @Override
    public void prev() {
        mMediaSessionCallback.onSkipToPrevious();
    }

    @Override
    public void next() {
        mMediaSessionCallback.onSkipToNext();
    }

    @Override
    public void play() {
        mMediaSessionCallback.onPlay();
    }

    @Override
    public void pause() {
        mMediaSessionCallback.onPause();
    }
}
