/*
package com.winjay.practice.media.music;


import android.content.Context;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;


import java.util.ArrayList;
import java.util.List;

public class MusicListData {

    */
/**
     * rwa 转换成Uri
     *
     * @param context context
     * @param id      资源 id
     * @return Uri
     *//*

    public static Uri rawToUri(Context context, int id) {
        String uriStr = "android.resource://" + context.getPackageName() + "/" + id;
        return Uri.parse(uriStr);
    }

    public static List<MusicData> getPlayList() {
        List<MusicData> list = new ArrayList<>();

        MusicData playBean = new MusicData();
        playBean.mediaId = R.raw.cesinspire;
        playBean.tilte = "cesinspire.mp3";
        playBean.artist = "test";
        list.add(playBean);

        MusicData playBean2 = new MusicData();
        playBean2.mediaId = R.raw.bigbigbig;
        playBean2.tilte = "bigbigbig";
        playBean2.artist = "罗罗罗罗罗罗";
        list.add(playBean2);

        return list;
    }


    public static List<MusicData> getPlayListUpdate() {
        List<MusicData> list = new ArrayList<>();

        MusicData playBean = new MusicData();
        playBean.mediaId = R.raw.cesinspire;
        playBean.tilte = "cesinspire.mp3";
        playBean.artist = "test";
        list.add(playBean);

        MusicData playBean2 = new MusicData();
        playBean2.mediaId = R.raw.bigbigbig;
        playBean2.tilte = "bigbigbig";
        playBean2.artist = "罗罗罗罗罗罗";
        list.add(playBean2);

        MusicData playBean3 = new MusicData();
        playBean3.mediaId = R.raw.fengtimo_shuosanjiusan;
        playBean3.tilte = "说散就散";
        playBean3.artist = "fengtimo";
        list.add(playBean3);

        return list;
    }


    */
/**
     * 数据转换
     *
     * @param playBeanList playBeanList
     * @return MediaSession 数据实体
     *//*

    public static ArrayList<MediaBrowserCompat.MediaItem> transformPlayList(List<MusicData> playBeanList) {
        //我们模拟获取数据的过程，真实情况应该是异步从网络或本地读取数据
        ArrayList<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        for (int i = 0; i < playBeanList.size(); i++) {
            MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + playBeanList.get(i).mediaId)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, playBeanList.get(i).tilte)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, playBeanList.get(i).artist)
                    .build();
            mediaItems.add(createMediaItem(metadata));
        }
        return mediaItems;
    }


    public static MediaMetadataCompat transformPlayBean(MusicData bean) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + bean.mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, bean.tilte)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, bean.artist)
                .build();
    }

    private static MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata) {
        return new MediaBrowserCompat.MediaItem(metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

}

*/
