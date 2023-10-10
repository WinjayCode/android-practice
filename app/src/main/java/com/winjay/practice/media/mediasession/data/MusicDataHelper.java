package com.winjay.practice.media.mediasession.data;

import static android.support.v4.media.MediaDescriptionCompat.STATUS_NOT_DOWNLOADED;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.google.android.exoplayer2.C;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.GsonUtil;
import com.winjay.practice.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 音频资源获取
 *
 * @author Winjay
 * @date 2023-02-27
 */
public class MusicDataHelper {
    private static final String TAG = MusicDataHelper.class.getSimpleName();

    // 测试在线音频列表地址
    private static final String REMOTE_JSON_SOURCE = "https://storage.googleapis.com/uamp/catalog.json";

    public static final String ASSETS_DIR = "audio";
    private static final List<MediaMetadataCompat> mMediaMetadataItemsFromAssets = new ArrayList<>();
    private static final List<MediaMetadataCompat> mMediaMetadataItemsFromUri = new ArrayList<>();

    public static final boolean SOURCES_FROM_NET = true;

    public interface GetDataCallback {
        void onLoadFinished(List<MediaBrowserCompat.MediaItem> mediaItems);
    }

    public static void getMediaBrowserItems(Context context, GetDataCallback getDataCallback) {
        if (SOURCES_FROM_NET) {
            getMediaBrowserItemsFromUri(getDataCallback);
        } else {
            getMediaBrowserItemsFromAssets(context, getDataCallback);
        }
    }

    public static List<MediaMetadataCompat> getMediaMetadataItems() {
        if (SOURCES_FROM_NET) {
            return mMediaMetadataItemsFromUri;
        } else {
            return mMediaMetadataItemsFromAssets;
        }
    }

    private static void getMediaBrowserItemsFromAssets(Context context, GetDataCallback getDataCallback) {
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
                mMediaMetadataItemsFromAssets.add(mediaMetadata);
                result.add(createMediaItem(mediaMetadata));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        getDataCallback.onLoadFinished(result);
    }

    private static MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata) {
        return new MediaBrowserCompat.MediaItem(metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    private static void getMediaBrowserItemsFromUri(GetDataCallback getDataCallback) {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        downloadJson(new DownloadCallback() {
            @Override
            public void onJsonDownloaded(JsonCatalog jsonCatalog) {
                if (jsonCatalog != null) {
                    for (JsonMusic jsonMusic : jsonCatalog.music) {
                        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();

                        // The duration from the JSON is given in seconds, but the rest of the code works in
                        // milliseconds. Here's where we convert to the proper units.
                        long durationMs = TimeUnit.SECONDS.toMillis(jsonMusic.duration);

                        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, jsonMusic.id);
                        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, jsonMusic.title);
                        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, jsonMusic.artist);
                        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, jsonMusic.album);
                        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, durationMs);
                        builder.putString(MediaMetadataCompat.METADATA_KEY_GENRE, jsonMusic.genre);
                        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, jsonMusic.source);
                        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, jsonMusic.image);
                        builder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, jsonMusic.trackNumber);
                        builder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, jsonMusic.totalTrackCount);

                        // To make things easier for *displaying* these, set the display properties as well.
                        builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, jsonMusic.title);
                        builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, jsonMusic.artist);
                        builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, jsonMusic.album);
                        builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, jsonMusic.image);

                        // Add downloadStatus to force the creation of an "extras" bundle in the resulting
                        // MediaMetadataCompat object. This is needed to send accurate metadata to the
                        // media session during updates.
                        builder.putLong(MediaMetadataCompat.METADATA_KEY_DOWNLOAD_STATUS, STATUS_NOT_DOWNLOADED);

                        MediaMetadataCompat mediaMetadata = builder.build();
                        mMediaMetadataItemsFromUri.add(mediaMetadata);
                        result.add(createMediaItem(mediaMetadata));
                    }
                }

                getDataCallback.onLoadFinished(result);
            }

            @Override
            public void onJsonDownloadFailed() {
                getDataCallback.onLoadFinished(result);
            }
        });
    }

    private static void downloadJson(DownloadCallback downloadCallback) {
        HandlerManager.getInstance().postOnSubThread(new Runnable() {
            @Override
            public void run() {
                Uri catalogUri = Uri.parse(REMOTE_JSON_SOURCE);
                try {
                    URL catalogConn = new URL(catalogUri.toString());
                    Reader reader = new BufferedReader(new InputStreamReader(catalogConn.openStream()));
                    JsonCatalog jsonCatalog = GsonUtil.fromJson(reader, JsonCatalog.class);
                    downloadCallback.onJsonDownloaded(jsonCatalog);
                } catch (IOException e) {
                    e.printStackTrace();
                    downloadCallback.onJsonDownloadFailed();
                }
            }
        });
    }

    public interface DownloadCallback {
        void onJsonDownloaded(JsonCatalog jsonCatalog);
        void onJsonDownloadFailed();
    }

    static class JsonCatalog {
        private final List<JsonMusic> music = new ArrayList<>();
    }

    static class JsonMusic {
        String id;
        String title;
        String album;
        String artist;
        String genre;
        String source;
        String image;
        Long trackNumber;
        Long totalTrackCount;
        Long duration = C.TIME_UNSET;
        String site;
    }

}
