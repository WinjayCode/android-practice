package com.winjay.dlna.cast.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.winjay.dlna.R;
import com.winjay.dlna.cast.application.BaseApplication;
import com.winjay.dlna.cast.dmp.DeviceItem;
import com.winjay.dlna.cast.dmr.ZxtMediaRenderer;
import com.winjay.dlna.cast.dms.ContentNode;
import com.winjay.dlna.cast.dms.ContentTree;
import com.winjay.dlna.cast.dms.MediaServer;
import com.winjay.dlna.cast.util.FileUtil;
import com.winjay.dlna.cast.util.FixedAndroidHandler;
import com.winjay.dlna.cast.util.ImageUtil;
import com.winjay.dlna.cast.util.Utils;
import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.DIDLObject.Property;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.WriteStatus;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.fourthline.cling.support.model.item.VideoItem;
import org.seamless.util.MimeType;
import org.seamless.util.logging.LoggingUtil;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DevicesActivity extends AppCompatActivity {

    private final static String TAG = "DevicesActivity";

    public static final int DMR_GET_NO = 0;

    public static final int DMR_GET_SUC = 1;

    private static boolean serverPrepared = false;

    private String fileName;

    private ListView mDMSLv;

    private ListView mDMRLv;

    private ArrayList<DeviceItem> mDMSDeviceList = new ArrayList<DeviceItem>();

    private ArrayList<DeviceItem> mDMRDeviceList = new ArrayList<DeviceItem>();

    private int mImageContaierId = Integer.valueOf(ContentTree.IMAGE_ID) + 1;

    private long exitTime = 0;

    private DMSDevAdapter mDMSDevAdapter;

    //    private DevAdapter mDmrDevAdapter;
    private DMRDevAdapter mDmrDevAdapter;

    private AndroidUpnpService upnpService;

    private DeviceListRegistryListener deviceListRegistryListener;

    private MediaServer mediaServer;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {

            mDMSDeviceList.clear();
            mDMRDeviceList.clear();

            upnpService = (AndroidUpnpService) service;
            BaseApplication.upnpService = upnpService;

            LogUtil.d(TAG, "Connected to UPnP Service");

            if (mediaServer == null && SettingActivity.getDmsOn(DevicesActivity.this)) {
                try {
                    mediaServer = new MediaServer(DevicesActivity.this);
                    upnpService.getRegistry().addDevice(mediaServer.getDevice());
                    DeviceItem localDevItem = new DeviceItem(mediaServer.getDevice());

                    deviceListRegistryListener.deviceAdded(localDevItem);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            prepareMediaServer();
                        }
                    }).start();

                } catch (Exception ex) {
                    // TODO: handle exception
                    LogUtil.e(TAG, "Creating demo device failed", ex);
                    Toast.makeText(DevicesActivity.this, R.string.create_demo_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (SettingActivity.getRenderOn(DevicesActivity.this)) {
                ZxtMediaRenderer mediaRenderer = new ZxtMediaRenderer(1, DevicesActivity.this);
                upnpService.getRegistry().addDevice(mediaRenderer.getDevice());
                deviceListRegistryListener.dmrAdded(new DeviceItem(mediaRenderer.getDevice()));
            }

            // xgf
            for (Device device : upnpService.getRegistry().getDevices()) {
                if (device.getType().getNamespace().equals("schemas-upnp-org")
                        && device.getType().getType().equals("MediaServer")) {
                    final DeviceItem display = new DeviceItem(device, device
                            .getDetails().getFriendlyName(),
                            device.getDisplayString(), "(REMOTE) "
                            + device.getType().getDisplayString());
                    deviceListRegistryListener.deviceAdded(display);
                }
            }

            // Getting ready for future device advertisements
            upnpService.getRegistry().addListener(deviceListRegistryListener);
            // Refresh device list
            upnpService.getControlPoint().search();

            // select first device by default
            if (null != mDMSDeviceList && mDMSDeviceList.size() > 0 && null == BaseApplication.dmsDeviceItem) {
                BaseApplication.dmsDeviceItem = mDMSDeviceList.get(0);
            }
//            if (null != mDMRDeviceList && mDMRDeviceList.size() > 0 && null == BaseApplication.dmrDeviceItem) {
//                BaseApplication.dmrDeviceItem = mDMRDeviceList.get(0);
//            }
        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Fix the logging integration between java.util.logging and Android
        // internal logging
        LoggingUtil.resetRootHandler(new FixedAndroidHandler());
        Logger.getLogger("org.teleal.cling").setLevel(Level.INFO);

        setContentView(R.layout.devices);
        init();

        deviceListRegistryListener = new DeviceListRegistryListener();

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    private void init() {

        mDMSLv = (ListView) findViewById(R.id.media_server_list);

        if (null != mDMSDeviceList && mDMSDeviceList.size() > 0) {
            BaseApplication.dmsDeviceItem = mDMSDeviceList.get(0);
        }

        mDMSDevAdapter = new DMSDevAdapter(DevicesActivity.this, 0, mDMSDeviceList);
        mDMSLv.setAdapter(mDMSDevAdapter);
        mDMSLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if (null != mDMSDeviceList && mDMSDeviceList.size() > 0) {
                    BaseApplication.dmsDeviceItem = mDMSDeviceList.get(arg2);
                    mDMSDevAdapter.notifyDataSetChanged();
                }
            }
        });

        mDMRLv = (ListView) findViewById(R.id.renderer_list);

//        if (null != mDMRDeviceList && mDMRDeviceList.size() > 0) {
//            BaseApplication.dmrDeviceItem = mDMRDeviceList.get(0);
//        }

        mDmrDevAdapter = new DMRDevAdapter(DevicesActivity.this, 0, mDMRDeviceList);
        mDMRLv.setAdapter(mDmrDevAdapter);
        mDMRLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (null != mDMRDeviceList && mDMRDeviceList.size() > 0) {
                    if (null != mDMRDeviceList.get(position).getDevice()
                            && null != BaseApplication.dmsDeviceItem
                            && null != mDMRDeviceList.get(position).getDevice().getDetails().getModelDetails()
                            && Utils.DMR_NAME.equals(mDMRDeviceList.get(position).getDevice().getDetails().getModelDetails().getModelName())
                            && Utils.getDevName(mDMRDeviceList.get(position).getDevice().getDetails().getFriendlyName())
                            .equals(Utils.getDevName(BaseApplication.dmsDeviceItem.getDevice().getDetails().getFriendlyName()))) {
//                        BaseApplication.isLocalDmr = true;
                        // local dmr 不处理
                    } else {
//                        BaseApplication.isLocalDmr = false;

                        DeviceItem dmrDeviceItem = mDMRDeviceList.get(position);
                        if (BaseApplication.dmrDeviceList.contains(dmrDeviceItem)) {
                            BaseApplication.dmrDeviceList.remove(dmrDeviceItem);
                            LogUtil.d(TAG, "(remove)dmrDeviceList size=" + BaseApplication.dmrDeviceList.size());
                        } else {
                            BaseApplication.dmrDeviceList.add(dmrDeviceItem);
                            LogUtil.d(TAG, "(add)dmrDeviceList size=" + BaseApplication.dmrDeviceList.size());
                        }
                    }
//                    BaseApplication.dmrDeviceItem = mDmrList.get(position);
                    mDmrDevAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaServer.stopHttpServer();
        if (upnpService != null) {
            upnpService.getRegistry().removeListener(deviceListRegistryListener);
        }
        getApplicationContext().unbindService(serviceConnection);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.search_lan).setIcon(android.R.drawable.ic_menu_search);
        menu.add(0, 1, 0, R.string.menu_exit).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                searchNetwork();
                break;
            case 1:
                finish();
//                System.exit(0);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                Toast.makeText(getApplicationContext(), R.string.exit,
//                        Toast.LENGTH_SHORT).show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//                System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    protected void searchNetwork() {
        if (upnpService == null) return;
        Toast.makeText(this, R.string.searching_lan, Toast.LENGTH_SHORT).show();
        upnpService.getRegistry().removeAllRemoteDevices();
        upnpService.getControlPoint().search();
    }

    public class DeviceListRegistryListener extends DefaultRegistryListener {

        /* Discovery performance optimization for very slow Android devices! */

        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry,
                                                 RemoteDevice device) {
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry,
                                                final RemoteDevice device, final Exception ex) {
        }

        /*
         * End of optimization, you can remove the whole block if your Android
         * handset is fast (>= 600 Mhz)
         */

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            LogUtil.d(TAG, "remoteDeviceAdded:" + device.toString() + device.getType().getType());

            if (device.getType().getNamespace().equals("schemas-upnp-org") && device.getType().getType().equals("MediaServer")) {
                final DeviceItem display = new DeviceItem(device, device.getDetails().getFriendlyName(),
                        device.getDisplayString(), "(REMOTE) " + device.getType().getDisplayString());
                deviceAdded(display);
            }

            if (device.getType().getNamespace().equals("schemas-upnp-org") && device.getType().getType().equals("MediaRenderer")) {
                final DeviceItem dmrDisplay = new DeviceItem(device, device.getDetails().getFriendlyName(),
                        device.getDisplayString(), "(REMOTE) " + device.getType().getDisplayString());
                dmrAdded(dmrDisplay);
            }
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            final DeviceItem display = new DeviceItem(device, device.getDisplayString());
            deviceRemoved(display);

            if (device.getType().getNamespace().equals("schemas-upnp-org") && device.getType().getType().equals("MediaRenderer")) {
                final DeviceItem dmrDisplay = new DeviceItem(device, device.getDetails().getFriendlyName(),
                        device.getDisplayString(), "(REMOTE) " + device.getType().getDisplayString());
                dmrRemoved(dmrDisplay);
            }
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            LogUtil.d(TAG, "localDeviceAdded:" + device.toString() + device.getType().getType());

            final DeviceItem display = new DeviceItem(device, device.getDetails().getFriendlyName(),
                    device.getDisplayString(), "(REMOTE) " + device.getType().getDisplayString());
            deviceAdded(display);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            LogUtil.d(TAG, "localDeviceRemoved:" + device.toString() + device.getType().getType());

            final DeviceItem display = new DeviceItem(device, device.getDisplayString());
            deviceRemoved(display);
        }

        public void deviceAdded(final DeviceItem di) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (!mDMSDeviceList.contains(di)) {
                        mDMSDeviceList.add(di);
                        mDMSDevAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        public void deviceRemoved(final DeviceItem di) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mDMSDeviceList.remove(di);
                    mDMSDevAdapter.notifyDataSetChanged();
                }
            });
        }

        public void dmrAdded(final DeviceItem di) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (!mDMRDeviceList.contains(di)) {
                        mDMRDeviceList.add(di);
                        mDmrDevAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        public void dmrRemoved(final DeviceItem di) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mDMRDeviceList.remove(di);
                    mDmrDevAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private String[] imageThumbColumns = new String[]{
            MediaStore.Images.Thumbnails.IMAGE_ID,
            MediaStore.Images.Thumbnails.DATA};

    private void prepareMediaServer() {

        if (serverPrepared)
            return;
        ContentNode rootNode = ContentTree.getRootNode();
        // Video Container
        Container videoContainer = new Container();
        videoContainer.setClazz(new DIDLObject.Class("object.container"));
        videoContainer.setId(ContentTree.VIDEO_ID);
        videoContainer.setParentID(ContentTree.ROOT_ID);
        videoContainer.setTitle("Videos");
        videoContainer.setRestricted(true);
        videoContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
        videoContainer.setChildCount(0);

        rootNode.getContainer().addContainer(videoContainer);
        rootNode.getContainer().setChildCount(
                rootNode.getContainer().getChildCount() + 1);
        ContentTree.addNode(ContentTree.VIDEO_ID, new ContentNode(
                ContentTree.VIDEO_ID, videoContainer));

        Cursor cursor;
        String[] videoColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.DESCRIPTION};
        cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoColumns, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = ContentTree.VIDEO_PREFIX
                        + cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Video.Media._ID));
                String title = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String creator = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                String filePath = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

                String mimeType = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                long size = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                long duration = cursor
                        .getLong(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                String resolution = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));

                String description = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DESCRIPTION));

                Res res = new Res(new MimeType(mimeType.substring(0,
                        mimeType.indexOf('/')), mimeType.substring(mimeType
                        .indexOf('/') + 1)), size, "http://"
                        + mediaServer.getAddress() + "/" + id);

                res.setDuration(duration / (1000 * 60 * 60) + ":"
                        + (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
                        + (duration % (1000 * 60)) / 1000);
                res.setResolution(resolution);

                VideoItem videoItem = new VideoItem(id, ContentTree.VIDEO_ID, title, creator, res);

                // add video thumb Property
                String videoSavePath = ImageUtil.getSaveVideoFilePath(filePath, id);
                DIDLObject.Property albumArtURI = new DIDLObject.Property.UPNP.ALBUM_ART_URI(
                        URI.create("http://" + mediaServer.getAddress() + videoSavePath));
                Property[] properties = {albumArtURI};
                videoItem.addProperties(properties);
                videoItem.setDescription(description);
                videoContainer.addItem(videoItem);
                videoContainer.setChildCount(videoContainer.getChildCount() + 1);
                ContentTree.addNode(id, new ContentNode(id, videoItem, filePath));

                // Log.v(LOGTAG, "added video item " + title + "from " +
                // filePath);
            } while (cursor.moveToNext());
        }

        // Audio Container
        Container audioContainer = new Container(ContentTree.AUDIO_ID,
                ContentTree.ROOT_ID, "Audios", "GNaP MediaServer",
                new DIDLObject.Class("object.container"), 0);
        audioContainer.setRestricted(true);
        audioContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
        rootNode.getContainer().addContainer(audioContainer);
        rootNode.getContainer().setChildCount(
                rootNode.getContainer().getChildCount() + 1);
        ContentTree.addNode(ContentTree.AUDIO_ID, new ContentNode(
                ContentTree.AUDIO_ID, audioContainer));

        String[] audioColumns = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM};
        cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                audioColumns, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = ContentTree.AUDIO_PREFIX
                        + cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String creator = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String filePath = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String mimeType = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
                long size = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                long duration = cursor
                        .getLong(cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String album = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                Res res = null;
                try {
                    res = new Res(new MimeType(mimeType.substring(0,
                            mimeType.indexOf('/')), mimeType.substring(mimeType
                            .indexOf('/') + 1)), size, "http://"
                            + mediaServer.getAddress() + "/" + id);
                } catch (Exception e) {
                    LogUtil.w(TAG, "Exception1", e);
                }

                if (null == res) {
                    break;
                }
                res.setDuration(duration / (1000 * 60 * 60) + ":"
                        + (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
                        + (duration % (1000 * 60)) / 1000);

                // Music Track must have `artist' with role field, or
                // DIDLParser().generate(didl) will throw nullpointException
                MusicTrack musicTrack = new MusicTrack(id,
                        ContentTree.AUDIO_ID, title, creator, album,
                        new PersonWithRole(creator, "Performer"), res);
                audioContainer.addItem(musicTrack);
                audioContainer.setChildCount(audioContainer.getChildCount() + 1);
                ContentTree.addNode(id, new ContentNode(id, musicTrack, filePath));

                // Log.v(LOGTAG, "added audio item " + title + "from " +
                // filePath);
            } while (cursor.moveToNext());
        }

        // get image thumbnail
        Cursor thumbCursor = this.managedQuery(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                imageThumbColumns, null, null, null);
        HashMap<Integer, String> imageThumbs = new HashMap<Integer, String>();
        if (null != thumbCursor && thumbCursor.moveToFirst()) {
            do {
                imageThumbs.put(thumbCursor.getInt(0), thumbCursor.getString(1));
            } while (thumbCursor.moveToNext());

            if (Integer.parseInt(Build.VERSION.SDK) < 14) {
                thumbCursor.close();
            }
        }

        // Image Container
        Container imageContainer = new Container(ContentTree.IMAGE_ID,
                ContentTree.ROOT_ID, "Images", "GNaP MediaServer",
                new DIDLObject.Class("object.container"), 0);
        imageContainer.setRestricted(true);
        imageContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
        rootNode.getContainer().addContainer(imageContainer);
        rootNode.getContainer().setChildCount(
                rootNode.getContainer().getChildCount() + 1);
        ContentTree.addNode(ContentTree.IMAGE_ID, new ContentNode(
                ContentTree.IMAGE_ID, imageContainer));

        String[] imageColumns = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DESCRIPTION};
        cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageColumns, null, null, MediaStore.Images.Media.DATA);

        Container typeContainer = null;
        if (cursor.moveToFirst()) {
            do {
                int imageId = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Images.Media._ID));
                String id = ContentTree.IMAGE_PREFIX
                        + cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Images.Media._ID));
                String title = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
                String creator = "unkown";
                String filePath = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                String mimeType = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                long size = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));

                String description = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DESCRIPTION));

                String url = "http://" + mediaServer.getAddress() + "/" + filePath;
                Res res = new Res(new MimeType(mimeType.substring(0,
                        mimeType.indexOf('/')), mimeType.substring(mimeType
                        .indexOf('/') + 1)), size, url);

                Container tempTypeContainer = null;
                if (null != typeContainer) {
                    tempTypeContainer = typeContainer;
                }
                if (TextUtils.isEmpty(fileName)) {
                    fileName = FileUtil.getFoldName(filePath);
                    typeContainer = new Container(
                            String.valueOf(mImageContaierId),
                            ContentTree.IMAGE_ID, fileName, "GNaP MediaServer",
                            new DIDLObject.Class("object.container"), 0);
                    typeContainer.setRestricted(true);
                    typeContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);

                    tempTypeContainer = typeContainer;
                    imageContainer.addContainer(tempTypeContainer);
                    imageContainer.setChildCount(imageContainer.getChildCount() + 1);
                    ContentTree.addNode(String.valueOf(mImageContaierId),
                            new ContentNode(String.valueOf(mImageContaierId), tempTypeContainer));

                    ImageItem imageItem = new ImageItem(id,
                            String.valueOf(mImageContaierId), title, creator, res);

                    if (imageThumbs.containsKey(imageId)) {
                        String thumb = imageThumbs.get(imageId);
                        LogUtil.d(TAG, " image thumb:" + thumb);
                        // set albumArt Property
                        DIDLObject.Property albumArtURI = new DIDLObject.Property.UPNP.ALBUM_ART_URI(
                                URI.create("http://" + mediaServer.getAddress() + thumb));
                        Property[] properties = {albumArtURI};
                        imageItem.addProperties(properties);
                    }
                    imageItem.setDescription(description);

                    tempTypeContainer.addItem(imageItem);
                    tempTypeContainer.setChildCount(tempTypeContainer.getChildCount() + 1);
                    ContentTree.addNode(id, new ContentNode(id, imageItem, filePath));
                } else {
                    if (!fileName.equalsIgnoreCase(FileUtil.getFoldName(filePath))) {
                        mImageContaierId++;
                        fileName = FileUtil.getFoldName(filePath);

                        typeContainer = new Container(
                                String.valueOf(mImageContaierId),
                                ContentTree.IMAGE_ID, fileName,
                                "GNaP MediaServer", new DIDLObject.Class(
                                "object.container"), 0);
                        typeContainer.setRestricted(true);
                        typeContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);

                        tempTypeContainer = typeContainer;
                        imageContainer.addContainer(tempTypeContainer);
                        imageContainer.setChildCount(imageContainer.getChildCount() + 1);
                        ContentTree.addNode(
                                String.valueOf(mImageContaierId),
                                new ContentNode(String.valueOf(mImageContaierId), tempTypeContainer));

                        ImageItem imageItem = new ImageItem(id,
                                String.valueOf(mImageContaierId), title, creator, res);

                        if (imageThumbs.containsKey(imageId)) {
                            String thumb = imageThumbs.get(imageId);
                            LogUtil.d(TAG, " image thumb:" + thumb);
                            // set albumArt Property
                            DIDLObject.Property albumArtURI = new DIDLObject.Property.UPNP.ALBUM_ART_URI(
                                    URI.create("http://" + mediaServer.getAddress() + thumb));
                            Property[] properties = {albumArtURI};
                            imageItem.addProperties(properties);
                        }
                        imageItem.setDescription(description);
                        tempTypeContainer.addItem(imageItem);
                        tempTypeContainer.setChildCount(typeContainer.getChildCount() + 1);
                        ContentTree.addNode(id, new ContentNode(id, imageItem, filePath));
                    } else {
                        ImageItem imageItem = new ImageItem(id,
                                String.valueOf(mImageContaierId), title,
                                creator, res);

                        if (imageThumbs.containsKey(imageId)) {
                            String thumb = imageThumbs.get(imageId);
                            LogUtil.d(TAG, " image thumb:" + thumb);
                            // set albumArt Property
                            DIDLObject.Property albumArtURI = new DIDLObject.Property.UPNP.ALBUM_ART_URI(
                                    URI.create("http://" + mediaServer.getAddress() + thumb));
                            Property[] properties = {albumArtURI};
                            imageItem.addProperties(properties);
                        }
                        imageItem.setDescription(description);
                        tempTypeContainer.addItem(imageItem);
                        tempTypeContainer.setChildCount(typeContainer.getChildCount() + 1);
                        ContentTree.addNode(id, new ContentNode(id, imageItem, filePath));
                    }
                }

                // imageContainer.addItem(imageItem);
                // imageContainer
                // .setChildCount(imageContainer.getChildCount() + 1);
                // ContentTree.addNode(id,
                // new ContentNode(id, imageItem, filePath));

                LogUtil.d(TAG, "added image item " + title + "from " + filePath);
            } while (cursor.moveToNext());
        }
        serverPrepared = true;
    }

    class DMSDevAdapter extends ArrayAdapter<DeviceItem> {

        private static final String TAG = "DeviceAdapter";

        private LayoutInflater mInflater;

        public int dmrPosition = 0;

        private List<DeviceItem> deviceItems;

        public DMSDevAdapter(Context context, int textViewResourceId, List<DeviceItem> objects) {
            super(context, textViewResourceId, objects);
            this.mInflater = LayoutInflater.from(context);
            this.deviceItems = objects;
        }

        public int getCount() {
            return this.deviceItems.size();
        }

        public DeviceItem getItem(int paramInt) {
            return this.deviceItems.get(paramInt);
        }

        public long getItemId(int paramInt) {
            return paramInt;
        }

        public View getView(int position, View view, ViewGroup viewGroup) {

            DevHolder holder;
            if (view == null) {
                view = this.mInflater.inflate(R.layout.dmr_item, null);
                holder = new DevHolder();
                holder.filename = ((TextView) view.findViewById(R.id.dmr_name_tv));
                holder.checkBox = ((CheckBox) view.findViewById(R.id.dmr_cb));
                view.setTag(holder);
            } else {
                holder = (DevHolder) view.getTag();
            }

            DeviceItem item = (DeviceItem) this.deviceItems.get(position);
            holder.filename.setText(item.toString());
            if (null != BaseApplication.dmsDeviceItem && BaseApplication.dmsDeviceItem.equals(item)) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
            return view;
        }

        public final class DevHolder {
            public TextView filename;
            public CheckBox checkBox;
        }

    }

    class DMRDevAdapter extends ArrayAdapter<DeviceItem> {

        private static final String TAG = "DMRDevAdapter";

        private LayoutInflater mInflater;

        public int dmrPosition = 0;

        private List<DeviceItem> deviceItems;

        public DMRDevAdapter(Context context, int textViewResourceId, List<DeviceItem> objects) {
            super(context, textViewResourceId, objects);
            this.mInflater = LayoutInflater.from(context);
            this.deviceItems = objects;
        }

        public int getCount() {
            return this.deviceItems.size();
        }

        public DeviceItem getItem(int paramInt) {
            return this.deviceItems.get(paramInt);
        }

        public long getItemId(int paramInt) {
            return paramInt;
        }

        public View getView(int position, View view, ViewGroup viewGroup) {

            DevHolder holder;
            if (view == null) {
                view = this.mInflater.inflate(R.layout.dmr_item, null);
                holder = new DevHolder();
                holder.filename = ((TextView) view.findViewById(R.id.dmr_name_tv));
                holder.checkBox = ((CheckBox) view.findViewById(R.id.dmr_cb));
                view.setTag(holder);
            } else {
                holder = (DevHolder) view.getTag();
            }

            DeviceItem item = (DeviceItem) this.deviceItems.get(position);
            holder.filename.setText(item.toString());

            if (BaseApplication.dmrDeviceList.contains(item)) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
            return view;
        }

        public final class DevHolder {
            public TextView filename;
            public CheckBox checkBox;
        }

    }
}
