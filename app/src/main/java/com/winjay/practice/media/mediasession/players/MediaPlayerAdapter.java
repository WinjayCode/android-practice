/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.winjay.practice.media.mediasession.players;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.winjay.practice.media.mediasession.data.MusicDataHelper;
import com.winjay.practice.utils.LogUtil;

import java.io.File;

/**
 * Exposes the functionality of the {@link MediaPlayer} and implements the {@link PlayerAdapter}
 * so that Activity can control music playback.
 */
public final class MediaPlayerAdapter extends PlayerAdapter {
    private static final String TAG = MediaPlayerAdapter.class.getSimpleName();
    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private String mFilename;
    private String mUri;
    private PlaybackInfoListener mPlaybackInfoListener;
    private MediaMetadataCompat mCurrentMedia;
    private int mState;
    private boolean mCurrentMediaPlayedToCompletion;

    // Work-around for a MediaPlayer bug related to the behavior of MediaPlayer.seekTo()
    // while not playing.
    private int mSeekWhileNotPlaying = -1;

    public MediaPlayerAdapter(Context context, PlaybackInfoListener listener) {
        super(context);
        mContext = context.getApplicationContext();
        mPlaybackInfoListener = listener;
        initializeMediaPlayer();
    }

    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    LogUtil.d(TAG, "duration=" + mMediaPlayer.getDuration());
                    play();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mPlaybackInfoListener.onPlaybackCompleted();

                    // Set the state to "paused" because it most closely matches the state
                    // in MediaPlayer with regards to available state transitions compared
                    // to "stop".
                    // Paused allows: seekTo(), start(), pause(), stop()
                    // Stop allows: stop()
                    setNewState(PlaybackStateCompat.STATE_PAUSED);
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    LogUtil.e(TAG, "what=" + what + ", extra=" + extra);
                    return false;
                }
            });
        }
    }

    // Implements PlaybackControl.
    @Override
    public void playFromMedia(MediaMetadataCompat metadata) {
        mCurrentMedia = metadata;
        if (MusicDataHelper.SOURCES_FROM_NET) {
            playFileFromNet(metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
        } else {
            playFileFromAssets(MusicDataHelper.ASSETS_DIR + File.separator + metadata.getDescription().getTitle());
        }
    }

    @Override
    public MediaMetadataCompat getCurrentMedia() {
        return mCurrentMedia;
    }

    private void playFileFromNet(String uri) {
        boolean mediaChanged = !uri.equals(mUri);
        if (!mediaChanged) {
            if (!isPlaying()) {
                play();
            }
            return;
        } else {
//            release();
        }

        mUri = uri;

        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mContext, Uri.parse(uri));
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            throw new RuntimeException("Failed to open file: " + uri, e);
        }
    }

    private void playFileFromAssets(String filename) {
        boolean mediaChanged = !filename.equals(mFilename);
        if (mCurrentMediaPlayedToCompletion) {
            // Last audio file was played to completion, the resourceId hasn't changed, but the
            // player was released, so force a reload of the media file for playback.
            mediaChanged = true;
            mCurrentMediaPlayedToCompletion = false;
        }
        if (!mediaChanged) {
            if (!isPlaying()) {
                play();
            }
            return;
        } else {
//            release();
        }

        mFilename = filename;

        mMediaPlayer.reset();

        try {
            AssetFileDescriptor assetFileDescriptor = mContext.getAssets().openFd(mFilename);
            mMediaPlayer.setDataSource(
                    assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength());
        } catch (Exception e) {
            throw new RuntimeException("Failed to open file: " + mFilename, e);
        }

        try {
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            throw new RuntimeException("Failed to open file: " + mFilename, e);
        }

//        play();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    protected void onPlay() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            setNewState(PlaybackStateCompat.STATE_PLAYING);
        }
    }

    @Override
    protected void onPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            setNewState(PlaybackStateCompat.STATE_PAUSED);
        }
    }

    @Override
    public void onStop() {
        // Regardless of whether or not the MediaPlayer has been created / started, the state must
        // be updated, so that MediaNotificationManager can take down the notification.
        setNewState(PlaybackStateCompat.STATE_STOPPED);
        release();
    }

    private void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    // This is the main reducer for the player state machine.
    private void setNewState(@PlaybackStateCompat.State int newPlayerState) {
        mState = newPlayerState;

        // Whether playback goes to completion, or whether it is stopped, the
        // mCurrentMediaPlayedToCompletion is set to true.
        if (mState == PlaybackStateCompat.STATE_STOPPED) {
            mCurrentMediaPlayedToCompletion = true;
        }

        // Work around for MediaPlayer.getCurrentPosition() when it changes while not playing.
        final long reportPosition;
        if (mSeekWhileNotPlaying >= 0) {
            reportPosition = mSeekWhileNotPlaying;

            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                mSeekWhileNotPlaying = -1;
            }
        } else {
            reportPosition = mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
        }

        final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(getAvailableActions());
        stateBuilder.setState(mState,
                              reportPosition,
                              1.0f,
                              SystemClock.elapsedRealtime());
        mPlaybackInfoListener.onPlaybackStateChange(stateBuilder.build());
    }

    /**
     * Set the current capabilities available on this session. Note: If a capability is not
     * listed in the bitmask of capabilities then the MediaSession will not handle it. For
     * example, if you don't want ACTION_STOP to be handled by the MediaSession, then don't
     * included it in the bitmask that's returned.
     */
    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                       | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                       | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                       | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        switch (mState) {
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                           | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                actions |= PlaybackStateCompat.ACTION_STOP
                           | PlaybackStateCompat.ACTION_PAUSE
                           | PlaybackStateCompat.ACTION_SEEK_TO;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                           | PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                           | PlaybackStateCompat.ACTION_PLAY_PAUSE
                           | PlaybackStateCompat.ACTION_STOP
                           | PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }

    @Override
    public void seekTo(long position) {
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                mSeekWhileNotPlaying = (int) position;
            }
            mMediaPlayer.seekTo((int) position);

            // Set the state (to the current state) because the position changed and should
            // be reported to clients.
            setNewState(mState);
        }
    }

    @Override
    public void setVolume(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }
}
