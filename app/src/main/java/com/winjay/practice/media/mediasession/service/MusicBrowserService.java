package com.winjay.practice.media.mediasession.service;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.winjay.practice.Constants;
import com.winjay.practice.media.bean.AudioBean;
import com.winjay.practice.media.mediasession.data.MusicDataHelper;
import com.winjay.practice.media.mediasession.players.MediaPlayerAdapter;
import com.winjay.practice.media.mediasession.players.PlaybackInfoListener;
import com.winjay.practice.media.mediasession.players.PlayerAdapter;
import com.winjay.practice.media.notification.MusicNotificationManager2;
import com.winjay.practice.utils.LogUtil;

import java.util.List;

/**
 * 音乐播放器媒体浏览器服务
 * <p>
 * assets目录下的资源无法直接获取资源本身信息(最好拷贝到具体目录下)
 *
 * @author Winjay
 * @date 2023-02-21
 */
public class MusicBrowserService extends MediaBrowserServiceCompat {
    private static final String TAG = "MusicPlayBrowserService";
    private static final String ROOT_ID = "ROOT_ID";
    private static final String MY_MEDIA_ROOT_ID = "MY_MEDIA_ROOT_ID";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "MY_EMPTY_MEDIA_ROOT_ID";
    private static final String CUSTOM_ACTION_THUMBS_UP = "CUSTOM_ACTION_THUMBS_UP";
    private Context mContext;

    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mPlaybackState;
    private MediaMetadataCompat.Builder mMediaMetadata;

    private String mParentId;

    private PlayerAdapter mPlayback;
    private int mCurrentIndex = 0;
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
        List<MediaBrowserCompat.MediaItem> mediaItems = MusicDataHelper.getMediaBrowserItemsFromAssets(this);

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

        mPlaybackState = new PlaybackStateCompat.Builder();
        // 收藏按钮
//        mPlaybackState.addCustomAction(new PlaybackStateCompat.CustomAction.Builder(
//                CUSTOM_ACTION_THUMBS_UP, getResources().getString(R.string.thumbs_up), thumbsUpIcon)
//                .setExtras(customActionExtras)
//                .build());
        mMediaMetadata = new MediaMetadataCompat.Builder();

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

        @Override
        public void onPlaybackCompleted() {
            LogUtil.d(TAG);
            mMediaSessionCallback.onSkipToNext();
        }

        class ServiceManager {

            private void moveServiceToStartedState(PlaybackStateCompat state) {
//                Notification notification =
//                        mMediaNotificationManager.getNotification(
//                                mPlayback.getCurrentMedia(), state, getSessionToken());

//                if (!mServiceInStartedState) {
//                    ContextCompat.startForegroundService(
//                            mContext,
//                            new Intent(mContext, MusicBrowserService.class));
//                    mServiceInStartedState = true;
//                }

//                startForeground(MediaNotificationManager.NOTIFICATION_ID, notification);

                Notification notification = MusicNotificationManager2.getInstance(mContext).getNotification(
                        mPlayback.getCurrentMedia().getDescription(),
                        mMediaSession.getSessionToken());
                startForeground(Constants.NOTIFICATION_ID, notification);
            }

            private void updateNotificationForPause(PlaybackStateCompat state) {
//                stopForeground(false);
//                Notification notification =
//                        mMediaNotificationManager.getNotification(
//                                mPlayback.getCurrentMedia(), state, getSessionToken());
//                mMediaNotificationManager.getNotificationManager()
//                        .notify(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void moveServiceOutOfStartedState(PlaybackStateCompat state) {
//                stopForeground(true);
//                stopSelf();
//                mServiceInStartedState = false;
            }
        }

    }

    MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public void onPrepare() {
            LogUtil.d(TAG);

            mMediaSession.setMetadata(MusicDataHelper.getMediaMetadataItemsFromAssets().get(mCurrentIndex));

            if (!mMediaSession.isActive()) {
                mMediaSession.setActive(true);
            }
        }

        @Override
        public void onPlay() {
            LogUtil.d(TAG);
            mPlayback.playFromMedia(MusicDataHelper.getMediaMetadataItemsFromAssets().get(mCurrentIndex));
        }

        @Override
        public void onPause() {
            LogUtil.d(TAG);
            mPlayback.pause();
        }

        @Override
        public void onStop() {
            LogUtil.d(TAG);
            mPlayback.stop();
            mMediaSession.setActive(false);

        }

        @Override
        public void onSkipToNext() {
            LogUtil.d(TAG);
            mCurrentIndex = (++mCurrentIndex % MusicDataHelper.getMediaMetadataItemsFromAssets().size());
            onPrepare();
            onPlay();
        }

        @Override
        public void onSkipToPrevious() {
            LogUtil.d(TAG);
            mCurrentIndex = mCurrentIndex > 0 ? mCurrentIndex - 1 : MusicDataHelper.getMediaMetadataItemsFromAssets().size() - 1;
            onPrepare();
            onPlay();
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo(pos);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            LogUtil.d(TAG, "action=" + action);
            if (CUSTOM_ACTION_THUMBS_UP.equals(action)) {
                LogUtil.d(TAG);
            }
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPlayback.stop();

        mMediaSession.setActive(false);
        mMediaSession.release();
    }
}
