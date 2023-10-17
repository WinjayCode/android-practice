package com.winjay.practice.media.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.media3.common.C;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;

import com.winjay.practice.utils.LogUtil;

/**
 * SeekBar that can be used with a {@link Player} to track and seek in playing
 * media.
 */

public class Media3SeekBar extends AppCompatSeekBar implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = Media3SeekBar.class.getSimpleName();
    private Player mPlayer;
    private PlayerListener playerListener;

    private boolean mIsTracking = false;

    private ValueAnimator mProgressAnimator;

    public Media3SeekBar(Context context) {
        super(context);
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    public Media3SeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    public Media3SeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    private final OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mIsTracking = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mPlayer.seekTo(getProgress());
            mIsTracking = false;
        }
    };

    @Override
    public final void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        // Prohibit adding seek listeners to this subclass.
        throw new UnsupportedOperationException("Cannot add listeners to a Media3SeekBar");
    }

    public void setPlayer(final Player player) {
        if (player != null) {
            playerListener = new PlayerListener();
            player.addListener(playerListener);
        } else if (mPlayer != null) {
            mPlayer.removeListener(playerListener);
            playerListener = null;
        }
        mPlayer = player;

        initDisplay();
    }

    private void initDisplay() {
        float max = mPlayer.getDuration();
        if (max != C.TIME_UNSET) {
            setMax((int) max);
        }

        int progress = (int) mPlayer.getCurrentPosition();
        setProgress(progress);

        handleAnimator(mPlayer.isPlaying());
    }

    public void disconnectPlayer() {
        if (mPlayer != null) {
            mPlayer.removeListener(playerListener);
            playerListener = null;
//            mPlayer = null;
        }
    }

    private void handleAnimator(boolean isPlaying) {
        // If there's an ongoing animation, stop it now.
        if (mProgressAnimator != null) {
            mProgressAnimator.cancel();
            mProgressAnimator = null;
        }

        setProgress(0);
        int max = (int) mPlayer.getDuration();
//        LogUtil.d(TAG, "max=" + max);
        setMax(max);

        final int progress = (int) mPlayer.getCurrentPosition();
        setProgress(progress);

        if (isPlaying) {
//            LogUtil.d(TAG, "progress=" + progress);
//            LogUtil.d(TAG, "speed=" + mPlayer.getPlaybackParameters().speed);
            int timeToEnd = (int) ((getMax() - progress) / mPlayer.getPlaybackParameters().speed);
//            LogUtil.d(TAG, "timeToEnd=" + timeToEnd);
            timeToEnd = Math.max(timeToEnd, 0);

            mProgressAnimator = ValueAnimator.ofInt(progress, getMax())
                    .setDuration(timeToEnd);
            mProgressAnimator.setInterpolator(new LinearInterpolator());
            mProgressAnimator.addUpdateListener(this);
            mProgressAnimator.start();
        }
    }

    private class PlayerListener implements Player.Listener {
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            handleAnimator(isPlaying);
        }

        @Override
        public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
            setProgress(0);
            int max = (int) mPlayer.getDuration();
//            LogUtil.d(TAG, "max=" + max);
            setMax(max);
        }
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        // If the user is changing the slider, cancel the animation.
        if (mIsTracking) {
            valueAnimator.cancel();
            return;
        }

        final int animatedIntValue = (int) valueAnimator.getAnimatedValue();
//            LogUtil.d(TAG, "animatedIntValue=" + animatedIntValue);
        setProgress(animatedIntValue);
    }
}
