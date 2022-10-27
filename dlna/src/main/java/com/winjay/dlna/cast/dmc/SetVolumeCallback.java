package com.winjay.dlna.cast.dmc;

import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;

public class SetVolumeCallback extends SetVolume {
    private static final String TAG = "SetVolumeCallback";

    public SetVolumeCallback(Service paramService, long paramLong) {
        super(paramService, paramLong);
    }

    public void failure(ActionInvocation paramActionInvocation, UpnpResponse paramUpnpResponse, String paramString) {
        LogUtil.w(TAG, "paramUpnpResponse=" + paramUpnpResponse.toString());
        LogUtil.w(TAG, "paramString=" + paramString);
    }

    public void success(ActionInvocation paramActionInvocation) {
        LogUtil.d(TAG, "paramActionInvocation=" + paramActionInvocation.toString());
        super.success(paramActionInvocation);
    }

}
