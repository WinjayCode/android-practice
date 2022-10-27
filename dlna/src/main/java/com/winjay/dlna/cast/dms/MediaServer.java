
package com.winjay.dlna.cast.dms;

import android.content.Context;

import com.winjay.dlna.cast.activity.SettingActivity;
import com.winjay.dlna.cast.application.BaseApplication;
import com.winjay.dlna.cast.util.FileUtil;
import com.winjay.dlna.cast.util.UpnpUtil;
import com.winjay.dlna.cast.util.Utils;
import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

import java.io.IOException;

public class MediaServer {

    private UDN udn;

    private LocalDevice localDevice;

    private final static String deviceType = "MediaServer";

    private final static int version = 1;

    private final static String TAG = "MediaServer";

    public final static int PORT = 8192;
    private Context mContext;
    private HttpServer httpServer;

    public MediaServer(Context context) throws ValidationException {
        mContext = context;
        DeviceType type = new UDADeviceType(deviceType, version);

        DeviceDetails details = new DeviceDetails(SettingActivity.getDeviceName(context) + " ("
                + android.os.Build.MODEL + ")", new ManufacturerDetails(
                android.os.Build.MANUFACTURER), new ModelDetails(android.os.Build.MODEL,
                Utils.DMS_DESC, "v1"));

        LocalService service = new AnnotationLocalServiceBinder().read(ContentDirectoryService.class);

        service.setManager(new DefaultServiceManager<ContentDirectoryService>(service, ContentDirectoryService.class));

        udn = UpnpUtil.uniqueSystemIdentifier("msidms");

        localDevice = new LocalDevice(new DeviceIdentity(udn), type, details, createDefaultDeviceIcon(), service);

        LogUtil.d(TAG, "MediaServer device created: ");
        LogUtil.d(TAG, "friendly name: " + details.getFriendlyName());
        LogUtil.d(TAG, "manufacturer: " + details.getManufacturerDetails().getManufacturer());
        LogUtil.d(TAG, "model: " + details.getModelDetails().getModelName());

        // start http server
        try {
            httpServer = new HttpServer(PORT);
        } catch (IOException ioe) {
            LogUtil.e(TAG, "Couldn't start server:\n" + ioe);
        }

        LogUtil.d(TAG, "Started Http Server on port " + PORT);
    }

    public void stopHttpServer() {
        httpServer.stop();
    }

    public LocalDevice getDevice() {
        return localDevice;
    }

    public String getAddress() {
        return BaseApplication.getHostAddress() + ":" + PORT;
    }

    protected Icon createDefaultDeviceIcon() {
        try {
            return new Icon("image/png", 48, 48, 32, "msi.png", mContext.getResources().getAssets()
                    .open(FileUtil.LOGO));
        } catch (IOException e) {
            LogUtil.w(TAG, "createDefaultDeviceIcon IOException");
            return null;
        }
    }

}
