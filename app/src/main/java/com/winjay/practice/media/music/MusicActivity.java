package com.winjay.practice.media.music;

import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.winjay.practice.Constants;
import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;
import com.winjay.practice.media.MediaSessionHelper;
import com.winjay.practice.media.audio_focus.AudioFocusManager;
import com.winjay.practice.media.bean.AudioBean;
import com.winjay.practice.media.interfaces.IMediaStatus;
import com.winjay.practice.media.interfaces.MediaType;
import com.winjay.practice.media.interfaces.SourceType;
import com.winjay.practice.media.notification.MusicNotificationManager;
import com.winjay.practice.media.receiver.MediaNotificationReceiver;
import com.winjay.practice.utils.LogUtil;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 音乐播放器
 * <p>
 * assets目录下的资源无法直接获取资源本身信息(最好拷贝到具体目录下)
 * <p>
 *
 * @author Winjay
 * @date 2021-02-05
 */
public class MusicActivity extends BaseActivity implements IMediaStatus {
    private static final String TAG = MusicActivity.class.getSimpleName();
    private final String assetsDir = "audio";
    private MediaPlayer musicPlayer;
    private AudioFocusManager mAudioFocusManager;
    private MediaSessionHelper mMediaSessionHelper;
    private MediaNotificationReceiver mMediaNotificationReceiver;
    private String[] mAssetsMusicList;
    private int mCurrentIndex = 0;

    @BindView(R.id.music_title_tv)
    TextView mMusicTitleTV;

    @BindView(R.id.play_pause_iv)
    ImageView play_pause_iv;

    @BindView(R.id.album_iv)
    ImageView album_iv;

    private int mSourceType;

    @Override
    protected int getLayoutId() {
        return R.layout.music_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerObserver();
        registerReceiver();
        musicPlayer = new MediaPlayer();
        mAudioFocusManager = new AudioFocusManager(this, MediaType.MUSIC);
        mMediaSessionHelper = new MediaSessionHelper(this);
        mAudioFocusManager.setOnAudioFocusChangeListener(new AudioFocusManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        LogUtil.d(TAG, "media:AUDIOFOCUS_GAIN");
                        if (musicPlayer != null) {
                            musicPlayer.start();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        LogUtil.d(TAG, "media:AUDIOFOCUS_LOSS");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        LogUtil.d(TAG, "media:AUDIOFOCUS_LOSS_TRANSIENT");
                        if (musicPlayer != null) {
                            musicPlayer.pause();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        LogUtil.d(TAG, "media:AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        if (musicPlayer != null) {
                            musicPlayer.pause();
                        }
                        break;
                }
            }
        });

        if (getIntent().hasExtra("audio")) {
            mSourceType = SourceType.USB_TYPE;
            AudioBean audio = (AudioBean) getIntent().getSerializableExtra("audio");
            if (audio != null && !TextUtils.isEmpty(audio.getPath())) {
                if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioFocusManager.requestFocus()) {
                    mMediaSessionHelper.registerMediaButton(AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
                    setLocalSource(audio.getPath());
                }
                mMusicTitleTV.setText(audio.getDisplayName());
                MusicNotificationManager.getInstance(this).setMediaData(audio);
            }
        } else {
            mSourceType = SourceType.ASSETS_TYPE;
            getAssetsSource();
            if (mAssetsMusicList.length > 0) {
                if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioFocusManager.requestFocus()) {
                    mMediaSessionHelper.registerMediaButton(AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
                    setAssetsSource();
                }
            }
        }
    }

    private void setLocalSource(String path) {
        try {
            musicPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        playMusic();
    }

    private void getAssetsSource() {
        try {
            mAssetsMusicList = getAssets().list(assetsDir);
            LogUtil.d(TAG, "mAssetsMusicList=" + mAssetsMusicList.length);
            for (String file : mAssetsMusicList) {
                LogUtil.d(TAG, "assetsFile=" + file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAssetsSource() {
        LogUtil.d(TAG, "assets=" + mAssetsMusicList[mCurrentIndex]);
        musicPlayer.reset();
        try {
            AssetFileDescriptor afd = getAssets().openFd(assetsDir + File.separator + mAssetsMusicList[mCurrentIndex]);
            musicPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        playMusic();
        mMusicTitleTV.setText(mAssetsMusicList[mCurrentIndex]);

        AudioBean audioBean = new AudioBean();
        audioBean.setDisplayName(mAssetsMusicList[mCurrentIndex]);
        MusicNotificationManager.getInstance(this).setMediaData(audioBean);
    }

    private void playMusic() {
        musicPlayer.prepareAsync();
        musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtil.d(TAG, "duration=" + musicPlayer.getDuration());
                musicPlayer.start();
                MusicNotificationManager.getInstance(MusicActivity.this).showMusicNotification(true);
            }
        });
        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtil.d(TAG);
                next();
//                mAudioFocusManager.releaseAudioFocus();
            }
        });
        musicPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e(TAG, "what=" + what + ", extra=" + extra);
                return false;
            }
        });
    }

    @OnClick(R.id.play_pause_iv)
    void playPauseSwitch() {
        if (musicPlayer.isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    @OnClick(R.id.prev_iv)
    void prevClick() {
        prev();
    }

    @OnClick(R.id.next_iv)
    void nextClick() {
        next();
    }

    @Override
    public void prev() {
        LogUtil.d(TAG);
        --mCurrentIndex;
        if (mSourceType == SourceType.ASSETS_TYPE) {
            if (mCurrentIndex < 0) {
                mCurrentIndex = mAssetsMusicList.length;
            }
            setAssetsSource();
        } else if (mSourceType == SourceType.USB_TYPE) {

        }
    }

    @Override
    public void next() {
        LogUtil.d(TAG);
        ++mCurrentIndex;
        if (mSourceType == SourceType.ASSETS_TYPE) {
            if (mCurrentIndex > mAssetsMusicList.length - 1) {
                mCurrentIndex = 0;
            }
            setAssetsSource();
        } else if (mSourceType == SourceType.USB_TYPE) {

        }
    }

    @Override
    public void play() {
        LogUtil.d(TAG);
        musicPlayer.start();
        play_pause_iv.setImageResource(android.R.drawable.ic_media_pause);
        MusicNotificationManager.getInstance(this).showMusicNotification(true);
    }

    @Override
    public void pause() {
        LogUtil.d(TAG);
        musicPlayer.pause();
        play_pause_iv.setImageResource(android.R.drawable.ic_media_play);
        MusicNotificationManager.getInstance(this).showMusicNotification(false);
    }

    private void registerReceiver() {
        if (mMediaNotificationReceiver == null) {
            mMediaNotificationReceiver = new MediaNotificationReceiver(this);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PREV);
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PAUSE);
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_PLAY);
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_NEXT);
        intentFilter.addAction(Constants.ACTION_MEDIA_NOTIFICATION_PENDINGINTENT_CLOSE);
        registerReceiver(mMediaNotificationReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        if (mMediaNotificationReceiver != null) {
            unregisterReceiver(mMediaNotificationReceiver);
            mMediaNotificationReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicPlayer != null) {
            if (musicPlayer.isPlaying()) {
                musicPlayer.stop();
            }
            musicPlayer.release();
            musicPlayer = null;
        }
        mAudioFocusManager.releaseAudioFocus();
        mMediaSessionHelper.unRegisterMediaButton();
        unregisterReceiver();
        MusicNotificationManager.getInstance(getApplicationContext()).cancel();

        unRegisterObserver();
    }


    private MediaStoreChangeObserver mMediaStoreChangeObserver;

    private void registerObserver() {
        if (mMediaStoreChangeObserver == null) {
            mMediaStoreChangeObserver = new MediaStoreChangeObserver();
            getContentResolver().registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, false, mMediaStoreChangeObserver);
        }
    }

    private void unRegisterObserver() {
        if (mMediaStoreChangeObserver != null) {
            getContentResolver().unregisterContentObserver(mMediaStoreChangeObserver);
            mMediaStoreChangeObserver = null;
        }
    }

    private class MediaStoreChangeObserver extends ContentObserver {

        public MediaStoreChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            LogUtil.d(TAG, "selfChange=" + selfChange + ", uri=" + uri.toString());
            if (uri.compareTo(MediaStore.Video.Media.EXTERNAL_CONTENT_URI) == 0) {
                scanMusicFile();
            }
        }
    }

    private void scanMusicFile() {
        String sortOrder;
        String[] cursorCols;

        cursorCols = new String[]{MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE};

//        StringBuilder where = new StringBuilder();
//        where.delete(0, where.length());
//        where.append(MediaStore.Audio.Media.TITLE + " != ''");
//        where.append(" AND (" + MediaStore.Audio.Media.MIME_TYPE + " LIKE '%audio/%')");
//        // 不读取内置sd卡
//        where.append(" AND (" + MediaStore.Audio.Media.DATA + " NOT LIKE '%" + INTERNAL_STORAGE_PATH + "%')");
//        LogUtil.d(TAG, "where: " + where.toString());

//        sortOrder = MediaStore.Audio.VideoColumns.DATE_TAKEN + " DESC, " + BaseColumns._ID + " DESC ";

        // audio
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorCols, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
                long dateModified = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                int orientation = 0;//cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));

                LogUtil.d(TAG, "path=" + path);

                cursor.moveToNext();
            }
        } else {
            LogUtil.d(TAG, "cursor size is null or zero");
        }
        cursor.close();
    }

    /**
     * 获取音乐资源信息
     *
     * @param filePath
     */
    private void getMusicInfo(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象mmr
        mmr.setDataSource(filePath);//设置mmr对象的数据源为上面file对象的绝对路径
        String ablumString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);//获得音乐专辑的标题
        String artistString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);//获取音乐的艺术家信息
        String titleString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);//获取音乐标题信息
        String mimetypeString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);//获取音乐mime类型
        String durationString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//获取音乐持续时间
        String bitrateString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);//获取音乐比特率，位率
        String dateString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);//获取音乐的日期
        LogUtil.d(TAG, "ablumString=" + ablumString
                + "\n" + "artistString=" + artistString
                + "\n" + "titleString=" + titleString
                + "\n" + "mimetypeString=" + mimetypeString
                + "\n" + "durationString=" + durationString
                + "\n" + "bitrateString=" + bitrateString
                + "\n" + "dateString=" + dateString);

        /* 设置文本的内容 */
//        ablum.setText("专辑标题为："+ablumString);
//        artist.setText("艺术家名称为："+artistString);
//        title.setText("音乐标题为："+titleString);
//        mimetype.setText("音乐的MIME类型为："+mimetypeString);
//        duration.setText("duration为："+durationString);
//        bitrate.setText("bitrate为："+bitrateString);
//        date.setText("date为："+dateString);
    }

    /**
     * 加载专辑图片
     *
     * @param path 资源路径
     */
    private void loadCover(String path) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte[] cover = mediaMetadataRetriever.getEmbeddedPicture();
        if (cover.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(cover, 0, cover.length);
            album_iv.setImageBitmap(bitmap);
        }
    }
}
