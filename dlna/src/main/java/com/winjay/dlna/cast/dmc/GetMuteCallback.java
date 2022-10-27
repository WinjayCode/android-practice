package com.winjay.dlna.cast.dmc;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.renderingcontrol.callback.GetMute;

public class GetMuteCallback extends GetMute {
    private static final String TAG = "GetMuteCallback";
    private Handler handler;

    public GetMuteCallback(Service paramService, Handler paramHandler) {
        super(paramService);
        this.handler = paramHandler;
    }

    public void failure(ActionInvocation paramActionInvocation,
                        UpnpResponse paramUpnpResponse, String paramString) {
        LogUtil.w(TAG, "paramUpnpResponse=" + paramUpnpResponse.toString());
        LogUtil.w(TAG, "paramString=" + paramString);
    }

    public void received(ActionInvocation paramActionInvocation, boolean paramBoolean) {
        LogUtil.d(TAG, "paramActionInvocation=" + paramActionInvocation.toString());
        LogUtil.d(TAG, "get mute status:" + paramBoolean);
        Message localMessage = new Message();
        localMessage.what = DMCControlMessage.SETMUTE;
        Bundle localBundle = new Bundle();
        localBundle.putBoolean("mute", paramBoolean);
        localMessage.setData(localBundle);
        handler.sendMessage(localMessage);
    }

}
