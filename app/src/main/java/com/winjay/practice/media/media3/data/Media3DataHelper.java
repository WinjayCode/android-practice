package com.winjay.practice.media.media3.data;

import android.net.Uri;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.google.android.exoplayer2.C;
import com.winjay.practice.thread.HandlerManager;
import com.winjay.practice.utils.GsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Winjay
 * @date 2023-10-13
 */
public class Media3DataHelper {
    // 测试在线音频列表地址
    private static final String REMOTE_JSON_SOURCE = "https://storage.googleapis.com/uamp/catalog.json";

    public interface GetDataCallback {
        void onLoadFinished(List<MediaItem> mediaItems);
    }

    public void getMediaItems(GetDataCallback getDataCallback) {
        getMediaBrowserItemsFromUri(getDataCallback);
    }

    private void getMediaBrowserItemsFromUri(GetDataCallback getDataCallback) {
        List<MediaItem> result = new ArrayList<>();
        downloadJson(new DownloadCallback() {
            @Override
            public void onJsonDownloaded(JsonCatalog jsonCatalog) {
                if (jsonCatalog != null) {
                    for (JsonMusic jsonMusic : jsonCatalog.music) {
                        MediaMetadata.Builder builder = new MediaMetadata.Builder()
                                .setAlbumTitle(jsonMusic.album)
                                .setTitle(jsonMusic.title)
                                .setArtist(jsonMusic.artist)
                                .setGenre(jsonMusic.genre)
                                .setIsBrowsable(false)
                                .setIsPlayable(true)
                                .setArtworkUri(Uri.parse(jsonMusic.image))
                                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC);
                        MediaMetadata mediaMetadata = builder.build();
                        MediaItem mediaItem = new MediaItem.Builder()
                                .setMediaId(jsonMusic.id)
                                .setMediaMetadata(mediaMetadata)
                                .setUri(Uri.parse(jsonMusic.source))
                                .build();
                        result.add(mediaItem);
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

    private void downloadJson(DownloadCallback downloadCallback) {
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

    class JsonCatalog {
        private final List<JsonMusic> music = new ArrayList<>();
    }

    class JsonMusic {
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
