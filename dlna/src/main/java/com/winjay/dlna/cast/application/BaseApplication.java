package com.winjay.dlna.cast.application;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.winjay.dlna.cast.dmp.ContentItem;
import com.winjay.dlna.cast.dmp.DeviceItem;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.support.model.DIDLContent;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseApplication {

	public static DeviceItem dmsDeviceItem;

	public DIDLContent didl;

	public static List<DeviceItem> dmrDeviceList = new ArrayList<>();

	public ArrayList<ContentItem> listMusic;

	public ArrayList<ContentItem> listPhoto;

	public ArrayList<ContentItem> listPlayMusic = new ArrayList();

	public ArrayList<ContentItem> listVideo;

	public ArrayList<ContentItem> listcontent;

	public HashMap<String, ArrayList<ContentItem>> map;

	// public MediaUtils mediaUtils;

	public int position;

	public static AndroidUpnpService upnpService;

	public static Context mContext;

	private static InetAddress inetAddress;

	private static String hostAddress;

	private static String hostName;

	public void onCreate(Context context) {
		mContext = context.getApplicationContext();
	    initImageLoader(mContext);
	}

	public static Context getContext() {
		return mContext;
	}

	public static void setLocalIpAddress(InetAddress inetAddr) {
		inetAddress = inetAddr;
	}

	public static InetAddress getLocalIpAddress() {
		return inetAddress;
	}

	public static String getHostAddress() {
		return hostAddress;
	}

	public static void setHostAddress(String hostAddress) {
		BaseApplication.hostAddress = hostAddress;
	}

	public static String getHostName() {
		return hostName;
	}

	public static void setHostName(String hostName) {
		BaseApplication.hostName = hostName;
	}
	
	public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them, 
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .enableLogging() // Not necessary in common
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
