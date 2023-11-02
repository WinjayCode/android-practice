package com.winjay.practice.media.media3.medialibraryservice.data;

import android.content.res.AssetManager;
import android.net.Uri;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.google.common.collect.ImmutableList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Winjay
 * @date 2023-10-23
 */
public class MediaItemTree {
    private static final String TAG = MediaItemTree.class.getSimpleName();
    private static Map<String, MediaItemNode> treeNodes = new HashMap<>();
    private static Map<String, MediaItemNode> titleMap = new HashMap<>();
    private static boolean isInitialized = false;
    private static final String ROOT_ID = "[rootID]";
    private static final String ALBUM_ID = "[albumID]";
    private static final String GENRE_ID = "[genreID]";
    private static final String ARTIST_ID = "[artistID]";
    private static final String ALBUM_PREFIX = "[album]";
    private static final String GENRE_PREFIX = "[genre]";
    private static final String ARTIST_PREFIX = "[artist]";
    private static final String ITEM_PREFIX = "[item]";

    private static class MediaItemNode {
        private MediaItem mediaItem;
        private List<MediaItem> children = new ArrayList<>();

        public MediaItemNode(MediaItem mediaItem) {
            this.mediaItem = mediaItem;
        }

        public void addChild(String childID) {
            MediaItemNode mediaItemNode = treeNodes.get(childID);
            if (mediaItemNode != null) {
                children.add(mediaItemNode.mediaItem);
            }
        }

        public List<MediaItem> getChildren() {
            return ImmutableList.copyOf(children);
        }
    }

    private static MediaItem buildMediaItem(String title, String mediaId, boolean isPlayable,
                                            boolean isBrowsable, int mediaType,
                                            List<MediaItem.SubtitleConfiguration> subtitleConfigurations,
                                            String album, String artist, String genre, Uri sourceUri,
                                            Uri imageUri) {
        MediaMetadata metadata = new MediaMetadata.Builder()
                .setAlbumTitle(album)
                .setTitle(title)
                .setArtist(artist)
                .setGenre(genre)
                .setIsBrowsable(isBrowsable)
                .setIsPlayable(isPlayable)
                .setArtworkUri(imageUri)
                .setMediaType(mediaType)
                .build();

        return new MediaItem.Builder()
                .setMediaId(mediaId)
                .setSubtitleConfigurations(subtitleConfigurations)
                .setMediaMetadata(metadata)
                .setUri(sourceUri)
                .build();
    }

    public static void initialize(AssetManager assetManager) {
        if (isInitialized) {
            return;
        }
        isInitialized = true;
        // create root and folders for album/artist/genre.
        MediaItemNode rootMediaItemNode = new MediaItemNode(buildMediaItem("Root Folder", ROOT_ID,
                false, true, MediaMetadata.MEDIA_TYPE_FOLDER_MIXED,
                new ArrayList<>(), null, null, null, null, null));
        treeNodes.put(ROOT_ID, rootMediaItemNode);

        MediaItemNode albumMediaItemNode = new MediaItemNode(buildMediaItem("Album Folder", ALBUM_ID,
                false, true, MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS,
                new ArrayList<>(), null, null, null, null, null));
        treeNodes.put(ALBUM_ID, albumMediaItemNode);

        MediaItemNode artistMediaItemNode = new MediaItemNode(buildMediaItem("Artist Folder", ARTIST_ID,
                false, true, MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS,
                new ArrayList<>(), null, null, null, null, null));
        treeNodes.put(ARTIST_ID, artistMediaItemNode);

        MediaItemNode genreMediaItemNode = new MediaItemNode(buildMediaItem("Genre Folder", GENRE_ID,
                false, true, MediaMetadata.MEDIA_TYPE_FOLDER_GENRES,
                new ArrayList<>(), null, null, null, null, null));
        treeNodes.put(GENRE_ID, genreMediaItemNode);

        if (treeNodes.get(ROOT_ID) != null) {
            treeNodes.get(ROOT_ID).addChild(ALBUM_ID);
            treeNodes.get(ROOT_ID).addChild(ARTIST_ID);
            treeNodes.get(ROOT_ID).addChild(GENRE_ID);
        }

        // Here, parse the json file in asset for media list.
        // We use a file in asset for demo purpose
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(assetManager));
            JSONArray mediaList = jsonObject.getJSONArray("media");

            for (int i = 0; i < mediaList.length(); i++) {
                JSONObject media = mediaList.getJSONObject(i);
                addNodeToTree(media);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void addNodeToTree(JSONObject mediaObject) {
        try {
            String id = mediaObject.getString("id");
            String album = mediaObject.getString("album");
            String title = mediaObject.getString("title");
            String artist = mediaObject.getString("artist");
            String genre = mediaObject.getString("genre");
            List<MediaItem.SubtitleConfiguration> subtitleConfigurations = new ArrayList<>();
            if (mediaObject.has("subtitles")) {
                JSONArray subtitlesJson = mediaObject.getJSONArray("subtitles");
                for (int i = 0; i < subtitlesJson.length(); i++) {
                    JSONObject subtitleObject = subtitlesJson.getJSONObject(i);
                    subtitleConfigurations.add(new MediaItem.SubtitleConfiguration.Builder(
                            Uri.parse(subtitleObject.getString("subtitle_uri")))
                            .setMimeType(subtitleObject.getString("subtitle_mime_type"))
                            .setLanguage(subtitleObject.getString("subtitle_lang"))
                            .build());
                }
            }
            Uri sourceUri = Uri.parse(mediaObject.getString("source"));
            Uri imageUri = Uri.parse(mediaObject.getString("image"));
            // key of such items in tree
            String idInTree = ITEM_PREFIX + id;
            String albumFolderIdInTree = ALBUM_PREFIX + album;
            String artistFolderIdInTree = ARTIST_PREFIX + artist;
            String genreFolderIdInTree = GENRE_PREFIX + genre;

            MediaItemNode idInTreeMediaItemNode = new MediaItemNode(buildMediaItem(title, idInTree,
                    true, false, MediaMetadata.MEDIA_TYPE_MUSIC,
                    subtitleConfigurations, album, artist, genre, sourceUri, imageUri));
            treeNodes.put(idInTree, idInTreeMediaItemNode);

            if (treeNodes.get(idInTree) != null) {
                titleMap.put(title.toLowerCase(), treeNodes.get(idInTree));
            }

            if (!treeNodes.containsKey(albumFolderIdInTree)) {
                MediaItemNode albumFolderIdInTreeMediaItemNode = new MediaItemNode(buildMediaItem(album, albumFolderIdInTree,
                        false, true, MediaMetadata.MEDIA_TYPE_ALBUM,
                        subtitleConfigurations, null, null, null, null, null));
                treeNodes.put(albumFolderIdInTree, albumFolderIdInTreeMediaItemNode);
                if (treeNodes.get(ALBUM_ID) != null) {
                    treeNodes.get(ALBUM_ID).addChild(albumFolderIdInTree);
                }
            }
            if (treeNodes.get(albumFolderIdInTree) != null) {
                treeNodes.get(albumFolderIdInTree).addChild(idInTree);
            }

            // add into artist folder
            if (!treeNodes.containsKey(artistFolderIdInTree)) {
                MediaItemNode artistFolderIdInTreeMediaItemNode = new MediaItemNode(buildMediaItem(artist, artistFolderIdInTree,
                        false, true, MediaMetadata.MEDIA_TYPE_ARTIST,
                        subtitleConfigurations, null, null, null, null, null));
                treeNodes.put(artistFolderIdInTree, artistFolderIdInTreeMediaItemNode);
                if (treeNodes.get(ARTIST_ID) != null) {
                    treeNodes.get(ARTIST_ID).addChild(artistFolderIdInTree);
                }
            }
            if (treeNodes.get(artistFolderIdInTree) != null) {
                treeNodes.get(artistFolderIdInTree).addChild(idInTree);
            }

            // add into genre folder
            if (!treeNodes.containsKey(genreFolderIdInTree)) {
                MediaItemNode genreFolderIdInTreeMediaItemNode = new MediaItemNode(buildMediaItem(genre, genreFolderIdInTree,
                        false, true, MediaMetadata.MEDIA_TYPE_GENRE,
                        subtitleConfigurations, null, null, null, null, null));
                treeNodes.put(genreFolderIdInTree, genreFolderIdInTreeMediaItemNode);
                if (treeNodes.get(GENRE_ID) != null) {
                    treeNodes.get(GENRE_ID).addChild(genreFolderIdInTree);
                }
            }
            if (treeNodes.get(genreFolderIdInTree) != null) {
                treeNodes.get(genreFolderIdInTree).addChild(idInTree);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String loadJSONFromAsset(AssetManager assetManager) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = assetManager.open("catalog.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static MediaItem getRootItem() {
        if (treeNodes.get(ROOT_ID) != null) {
            return treeNodes.get(ROOT_ID).mediaItem;
        }
        return null;
    }

    public static MediaItem getItem(String id) {
        if (treeNodes.get(id) != null) {
            return treeNodes.get(id).mediaItem;
        }
        return null;
    }

    public static List<MediaItem> getChildren(String id) {
        if (treeNodes.get(id) != null) {
            return treeNodes.get(id).getChildren();
        }
        return null;
    }

    public static MediaItem getItemFromTitle(String title) {
        if (titleMap.get(title) != null) {
            return titleMap.get(title).mediaItem;
        }
        return null;
    }

    public static MediaItem getRandomItem() {
        MediaItem curRoot = getRootItem();
        while (curRoot.mediaMetadata.isBrowsable) {
            List<MediaItem> children = getChildren(curRoot.mediaId);
            curRoot = children.get((int) (Math.random() * children.size()));
        }
        return curRoot;
    }
}