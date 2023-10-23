package com.winjay.practice.media.media3.medialibraryservice.data;

import android.content.res.AssetManager;

import androidx.media3.common.MediaItem;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Winjay
 * @date 2023-10-23
 */
public class MediaItemTree {
    private static Map<String, MediaItemNode> treeNodes = new HashMap<>();
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

    private static MediaItem buildMediaItem(String title, String mediaId) {
        return null;
    }

    public static void initialize(AssetManager assetManager) {
        if (isInitialized) {
            return;
        }
        isInitialized = true;
        // create root and folders for album/artist/genre.
//        treeNodes.get(ROOT_ID) = new MediaItemNode(buildMediaItem("Root Folder",
//                ROOT_ID));
    }

    public static MediaItem getRootItem() {
        return null;
    }

    public static MediaItem getItem(String id) {
        return null;
    }

    public static List<MediaItem> getChildren(String id) {
        return null;
    }

    public static MediaItem getItemFromTitle(String title) {
        return null;
    }

    public static MediaItem getRandomItem() {
        return null;
    }
}