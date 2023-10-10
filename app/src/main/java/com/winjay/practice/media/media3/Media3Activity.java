package com.winjay.practice.media.media3;

import android.content.ComponentName;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.databinding.ActivityMusicPlayBinding;
import com.winjay.practice.media.media3.service.Media3Service;

import java.util.concurrent.ExecutionException;

/**
 * androidx.media3 学习
 * @author Winjay
 * @date 2023-10-10
 */
public class Media3Activity extends BaseActivity {
    private static final String TAG = Media3Activity.class.getSimpleName();
    private ActivityMusicPlayBinding binding;

    private ListenableFuture<MediaController> mControllerFuture;
    private Player mPlayer;


    @Override
    public boolean useViewBinding() {
        return true;
    }

    @Override
    public View viewBinding() {
        binding = ActivityMusicPlayBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, Media3Service.class));
        mControllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();

        mControllerFuture.addListener(() -> {
            // Call controllerFuture.get() to retrieve the MediaController.
            // MediaController implements the Player interface, so it can be
            // attached to the PlayerView UI component.
//            playerView.setPlayer(mControllerFuture.get());
            try {
                mPlayer = mControllerFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, MoreExecutors.directExecutor());
    }

    @Override
    protected void onDestroy() {
        MediaController.releaseFuture(mControllerFuture);

        super.onDestroy();
    }
}
