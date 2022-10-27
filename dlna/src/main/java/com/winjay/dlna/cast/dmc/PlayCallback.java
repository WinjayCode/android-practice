package com.winjay.dlna.cast.dmc;

import android.os.Handler;

import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.Play;

public class PlayCallback extends Play {
    private static final String TAG = "PlayCallback";

    private Handler handler;

    public PlayCallback(Service paramService, Handler paramHandler) {
        super(paramService);
        this.handler = paramHandler;
    }

    public void failure(ActionInvocation paramActionInvocation, UpnpResponse paramUpnpResponse, String paramString) {
        LogUtil.w(TAG, "paramUpnpResponse=" + paramUpnpResponse.toString());
        LogUtil.w(TAG, "paramString=" + paramString);
        handler.sendEmptyMessage(DMCControlMessage.PLAYVIDEOFAILED);
    }

    public void success(ActionInvocation paramActionInvocation) {
        super.success(paramActionInvocation);
        LogUtil.d(TAG, "paramActionInvocation=" + paramActionInvocation.toString());
        handler.sendEmptyMessage(DMCControlMessage.GETMEDIA);
    }

}
