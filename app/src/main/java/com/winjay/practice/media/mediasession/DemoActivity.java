/*

package com.winjay.practice.media.music;


import android.content.ComponentName;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import java.util.List;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TEST_DemoActivity";

    private MediaBrowserCompat mMediaBrowser;
    PlayInfo mPlayInfo = new PlayInfo();

    private TextView mTvInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mTvInfo = findViewById(R.id.tv_music_name);
        findViewById(R.id.btn_play_or_puse).setOnClickListener(this);
        findViewById(R.id.btn_pre).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        connectRemoteService();
    }


    private void connectRemoteService() {
        // 1.连接的服务

        ComponentName componentName = new ComponentName("com.xxx.xxxx", "com.xxx.xxxx.session.MossMusicService");

        // 2.创建MediaBrowser
        mMediaBrowser = new MediaBrowserCompat(this, componentName, mConnectionCallbacks, null);
        // 3.建立连接
        mMediaBrowser.connect();
    }

    private void refreshPlayInfo() {
        mTvInfo.setText(mPlayInfo.debugInfo());
    }

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallbacks = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            Log.d(TAG, "MediaBrowser.onConnected");
            if (mMediaBrowser.isConnected()) {
                String mediaId = mMediaBrowser.getRoot();
                mMediaBrowser.unsubscribe(mediaId);
                //之前说到订阅的方法还需要一个参数，即设置订阅回调SubscriptionCallback
                //当Service获取数据后会将数据发送回来，此时会触发SubscriptionCallback.onChildrenLoaded回调
                mMediaBrowser.subscribe(mediaId, browserSubscriptionCallback);
                try {
                    MediaControllerCompat mediaController = new MediaControllerCompat(DemoActivity.this,
                            mMediaBrowser.getSessionToken());

                    MediaControllerCompat.setMediaController(DemoActivity.this, mediaController);

//                    mediaController = new MediaControllerCompat(DemoActivity.this, mMediaBrowser.getSessionToken());
                    mediaController.registerCallback(mMediaControllerCallback);
                    if (mediaController.getMetadata() != null) {
                        updatePlayMetadata(mediaController.getMetadata());
                        updatePlayState(mediaController.getPlaybackState());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onConnectionSuspended() {
            // 连接中断回调
            Log.d(TAG, "onConnectionSuspended");
        }

        @Override
        public void onConnectionFailed() {
            Log.d(TAG, "onConnectionFailed");
        }
    };


    private void updatePlayMetadata(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        mPlayInfo.setMetadata(metadata);
        refreshPlayInfo();
    }


    */
/**
     * 被动接收播放信息、状态改变
     *//*

    MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onSessionDestroyed() {
            // Session销毁
            Log.d(TAG, "onSessionDestroyed");

        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            // 当前播放列表更新回调
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            // 数据变化
            Log.e(TAG, "onMetadataChanged ");
            updatePlayMetadata(metadata);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            // 播放状态变化
            Log.d(TAG, "onPlaybackStateChanged   PlaybackState:" + state.getState());
            updatePlayState(state);
        }

        @Override
        public void onQueueTitleChanged(CharSequence title) {
            super.onQueueTitleChanged(title);
            //播放列表信息回调，QueueItem在文章后面会提及
        }

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            super.onSessionEvent(event, extras);
            //自定义的事件回调，满足你各种自定义需求
        }

        @Override
        public void onExtrasChanged(Bundle extras) {
            super.onExtrasChanged(extras);
            //额外信息回调，可以承载播放模式等信息
        }
    };


    */
/**
     * 向媒体浏览器服务(MediaBrowserService)发起数据订阅请求的回调接口b
     *//*

    private final MediaBrowserCompat.SubscriptionCallback browserSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {

        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children, @NonNull Bundle options) {
            super.onChildrenLoaded(parentId, children, options);
            //订阅消息时添加了Bundle参数，会回调到此方法
            //即mMediaBrowser.subscribe("PARENT_ID_1", mCallback，bundle)的回调
            Log.d(TAG, "onChildrenLoaded: parentId = " + parentId + "  children = " + children + " options = " + options);
        }

        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            Log.d(TAG, "onChildrenLoaded: parentId = " + parentId + "  children = " + children);
            mPlayInfo.setChildren(children);
            refreshPlayInfo();
        }

        @Override
        public void onError(@NonNull String parentId) {
            super.onError(parentId);
            Log.d(TAG, "onError: parentId = " + parentId);
        }

        @Override
        public void onError(@NonNull String parentId, @NonNull Bundle options) {
            super.onError(parentId, options);
            Log.d(TAG, "onError: parentId = " + parentId + "  options = " + options);
        }
    };


    @Override
    public void onClick(View v) {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(DemoActivity.this);

        switch (v.getId()) {
            case R.id.btn_play_or_puse:
                PlaybackStateCompat playbackState = mediaController.getPlaybackState();
                if (null != playbackState && playbackState.getState() == PlaybackState.STATE_PLAYING) {
                    Log.d(TAG, "onClick: 暂停");
                    mediaController.getTransportControls().pause();
                } else {
                    Log.d(TAG, "onClick: 播放");
//                    Log.d(TAG, "onClick: play or puse " + .getState());
                    mediaController.getTransportControls().play();
                }
                break;
            case R.id.btn_pre:
                mediaController.getTransportControls().skipToPrevious();
                break;
            case R.id.btn_next:
                mediaController.getTransportControls().skipToNext();
                break;
        }
    }


    private void updatePlayState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        mPlayInfo.setState(state);
        refreshPlayInfo();
    }


    static class PlayInfo {
        private MediaMetadataCompat metadata;
        private PlaybackStateCompat state;
        private List<MediaBrowserCompat.MediaItem> children;


        public void setMetadata(MediaMetadataCompat metadata) {
            this.metadata = metadata;
        }

        public void setState(PlaybackStateCompat state) {
            this.state = state;
        }

        public void setChildren(List<MediaBrowserCompat.MediaItem> children) {
            this.children = children;
        }

        public String debugInfo() {
            StringBuilder builder = new StringBuilder();
            if (state != null) {
                builder.append("当前播放状态：\t").append(state.getState() == PlaybackState.STATE_PLAYING ? "播放中" : "未播放");
                Bundle extras = state.getExtras();
                if (null != extras) {
                    long currentPostion = extras.getLong("currentPostion", 0);
                    long duration = extras.getLong("duration", 0);
                    builder.append("\n当前播放进度：\t").append("currentPostion = ").append(currentPostion).append(" / duration = ").append(duration);
                }
                builder.append("\n\n");
            }
            if (metadata != null) {
                builder.append("当前播放信息：\t").append(transform(metadata));
                builder.append("\n\n");
            }
            if (children != null && !children.isEmpty()) {
                builder.append("当前播放列表：").append("\n");
                for (int i = 0; i < children.size(); i++) {
                    MediaBrowserCompat.MediaItem mediaItem = children.get(i);
                    builder.append(i + 1).append(" ").append(mediaItem.getDescription().getTitle()).append(" - ").append(mediaItem.getDescription().getSubtitle()).append("\n");
                }
            }
            return builder.toString();
        }

        public static String transform(MediaMetadataCompat data) {
            if (data == null) {
                return null;
            }
            String title = data.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
            String artist = data.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
            String albumName = data.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
            long mediaNumber = data.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER);
            long mediaTotalNumber = data.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS);
            return title + " - " + artist;
        }
    }
}
*/
