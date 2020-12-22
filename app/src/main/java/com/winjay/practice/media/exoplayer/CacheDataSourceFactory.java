package com.winjay.practice.media.exoplayer;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

/**
 * 自定义exoplayer缓存策略
 *
 * @author Winjay
 * @date 2020/8/21
 */
public class CacheDataSourceFactory implements DataSource.Factory {
    private final Context context;
    private final DataSource.Factory dataSourceFactory;
    private final long maxFileSize, maxCacheSize;

    CacheDataSourceFactory(Context context, long maxCacheSize, long maxFileSize, DataSource.Factory dataSourceFactory) {
        super();
        this.context = context;
        this.maxCacheSize = maxCacheSize;
        this.maxFileSize = maxFileSize;
        this.dataSourceFactory = dataSourceFactory;
//        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
//        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        defaultDatasourceFactory = new DefaultDataSourceFactory(this.context,
//                bandwidthMeter,
//                new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter));
    }

    @Override
    public DataSource createDataSource() {
        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(maxCacheSize);
        SimpleCache simpleCache = new SimpleCache(new File(context.getCacheDir(), "exoplayer_cache"), evictor);
        return new CacheDataSource(simpleCache, dataSourceFactory.createDataSource(),
                new FileDataSource(), new CacheDataSink(simpleCache, maxFileSize),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
    }
}
