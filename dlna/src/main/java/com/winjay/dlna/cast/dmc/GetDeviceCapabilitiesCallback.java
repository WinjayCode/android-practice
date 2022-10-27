
package com.winjay.dlna.cast.dmc;


import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.GetDeviceCapabilities;
import org.fourthline.cling.support.model.DeviceCapabilities;

public class GetDeviceCapabilitiesCallback extends GetDeviceCapabilities {
    private static final String TAG = "GetDeviceCapabilitiesCallback";

    public GetDeviceCapabilitiesCallback(Service paramService) {
        super(paramService);
    }

    public void failure(ActionInvocation paramActionInvocation, UpnpResponse paramUpnpResponse,
                        String paramString) {
        LogUtil.w(TAG, "paramUpnpResponse=" + paramUpnpResponse.toString());
        LogUtil.w(TAG, "paramString=" + paramString);
    }

    public void received(ActionInvocation paramActionInvocation,
                         DeviceCapabilities paramDeviceCapabilities) {
        LogUtil.d(TAG, "PlayMediaString=" + paramDeviceCapabilities.getPlayMediaString());
    }

}
