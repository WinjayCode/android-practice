package com.winjay.practice.media.mediasession.data;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.winjay.practice.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Winjay
 * @date 2023-02-27
 */
public class MusicDataHelper {
    private static final String TAG = MusicDataHelper.class.getSimpleName();
    public static final String ASSETS_DIR = "audio";
    private static List<MediaMetadataCompat> mMediaMetadataItems = new ArrayList<>();

    public static List<MediaBrowserCompat.MediaItem> getMediaBrowserItemsFromAssets(Context context) {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        try {
            String[] mAssetsMusicList = context.getAssets().list(ASSETS_DIR);
            LogUtil.d(TAG, "mAssetsMusicList=" + mAssetsMusicList.length);
            for (String title : mAssetsMusicList) {
                LogUtil.d(TAG, "assetsFile=" + title);
                MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
                builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, title);
                builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, title);
                MediaMetadataCompat mediaMetadata = builder.build();
                mMediaMetadataItems.add(mediaMetadata);
                result.add(createMediaItem(mediaMetadata));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata) {
        return new MediaBrowserCompat.MediaItem(metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    public static List<MediaMetadataCompat> getMediaMetadataItemsFromAssets() {
        return mMediaMetadataItems;
    }
}
