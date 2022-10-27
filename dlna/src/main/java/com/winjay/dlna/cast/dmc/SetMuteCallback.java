package com.winjay.dlna.cast.dmc;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;

public class SetMuteCallback extends SetMute {
    private static final String TAG = "SetMuteCallback";
    private boolean desiredMute;

    private Handler handler;

    public SetMuteCallback(Service paramService, boolean paramBoolean,
                           Handler paramHandler) {
        super(paramService, paramBoolean);
        this.handler = paramHandler;
        this.desiredMute = paramBoolean;
    }

    public void failure(ActionInvocation paramActionInvocation,
                        UpnpResponse paramUpnpResponse, String paramString) {
        LogUtil.w(TAG, "paramUpnpResponse=" + paramUpnpResponse.toString());
        LogUtil.w(TAG, "paramString=" + paramString);
    }

    public void success(ActionInvocation paramActionInvocation) {
        LogUtil.d(TAG, "paramActionInvocation=" + paramActionInvocation.toString());
        if (desiredMute) {
            desiredMute = false;
        }
        Message localMessage = new Message();
        localMessage.what = DMCControlMessage.SETMUTESUC;
        Bundle localBundle = new Bundle();
        localBundle.putBoolean("mute", desiredMute);
        localMessage.setData(localBundle);
        this.handler.sendMessage(localMessage);
    }
}
