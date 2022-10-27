
package com.winjay.dlna.cast.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.Toast;

import com.winjay.dlna.R;
import com.winjay.dlna.cast.application.BaseApplication;
import com.winjay.dlna.cast.dms.ContentTree;
import com.winjay.dlna.cast.util.FileUtil;
import com.winjay.dlna.cast.util.ImageUtil;
import com.winjay.dlna.util.LogUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class StartActivity extends Activity {
    private static final String TAG = "StartActivity";
    public static final int GET_IP_FAIL = 0;

    public static final int GET_IP_SUC = 1;

    private Context mContext;

//    private ProgressDialog progDialog = null;

    private String hostName;

    private String hostAddress;

    private List<Map<String, String>> mVideoFilePaths;

    private Handler mHandle = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_IP_FAIL: {
                    Toast.makeText(mContext, R.string.ip_get_fail, Toast.LENGTH_SHORT);
                    break;
                }
                case GET_IP_SUC: {
                    if (null != msg.obj) {
                        InetAddress inetAddress = (InetAddress) msg.obj;
                        if (null != inetAddress) {
                            setIp(inetAddress);
                            setIpInfo();
                            jumpToMain();
                        }
                    } else {
                        Toast.makeText(mContext, R.string.ip_get_fail, Toast.LENGTH_SHORT);
                    }
                    break;
                }

            }

            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_lay);
        mContext = this;
        createFolder();
        getVideoFilePaths();
        createVideoThumb();

        if (BaseApplication.getLocalIpAddress() != null) {
            jumpToMain();
        } else {
            getIp();
//            Toast.makeText(mContext, R.string.ip_get_fail, Toast.LENGTH_SHORT).show();
//            finish();
        }
    }

    private void createFolder() {
        FileUtil.createSDCardDir(true);
    }

    private void getIp() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                WifiManager wifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();

                InetAddress inetAddress;
                Message message = new Message();
                try {
                    inetAddress = InetAddress.getByName(String.format("%d.%d.%d.%d",
                            (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                            (ipAddress >> 24 & 0xff)));

                    hostName = inetAddress.getHostName();
                    LogUtil.d(TAG, "hostName=" + hostName);
                    hostAddress = inetAddress.getHostAddress();
                    LogUtil.d(TAG, "hostAddress=" + hostAddress);

                    message.obj = inetAddress;
                    message.what = GET_IP_SUC;
                    mHandle.sendMessage(message);
                } catch (UnknownHostException e) {
                    mHandle.sendEmptyMessage(GET_IP_FAIL);
                }
            }
        }).start();
    }

    private void getVideoFilePaths() {
        mVideoFilePaths = new ArrayList<Map<String, String>>();
        Cursor cursor;
        String[] videoColumns = {
                MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION, MediaStore.Video.Media.RESOLUTION
        };
        cursor = mContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoColumns, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            do {
                String id = ContentTree.VIDEO_PREFIX
                        + cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String filePath = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                Map<String, String> fileInfoMap = new HashMap<String, String>();
                fileInfoMap.put(id, filePath);
                mVideoFilePaths.add(fileInfoMap);
                // Log.v(LOGTAG, "added video item " + title + "from " +
                // filePath);
            } while (cursor.moveToNext());
        }
        if (null != cursor) {
            cursor.close();
        }
    }

    private void createVideoThumb() {
        if (null != mVideoFilePaths && mVideoFilePaths.size() > 0) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < mVideoFilePaths.size(); i++) {
                        Set entries = mVideoFilePaths.get(i).entrySet();
                        if (entries != null) {
                            Iterator iterator = entries.iterator();
                            while (iterator.hasNext()) {
                                Entry entry = (Entry) iterator.next();
                                Object id = entry.getKey();
                                Object filePath = entry.getValue();

                                Bitmap videoThumb = ImageUtil.getThumbnailForVideo(filePath.toString());
                                String videoSavePath = ImageUtil.getSaveVideoFilePath(
                                        filePath.toString(), id.toString());
                                try {
                                    ImageUtil.saveBitmapWithFilePathSuffix(videoThumb, videoSavePath);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }).start();
        }
    }

    private void setIp(InetAddress inetAddress) {
        BaseApplication.setLocalIpAddress(inetAddress);
    }

    private void setIpInfo() {
        BaseApplication.setHostName(hostName);
        BaseApplication.setHostAddress(hostAddress);
    }

    private void jumpToMain() {
        Intent intent = new Intent(StartActivity.this, IndexActivity.class);
        startActivity(intent);
        this.finish();
    }
}
