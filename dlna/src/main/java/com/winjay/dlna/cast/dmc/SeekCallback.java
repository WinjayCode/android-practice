package com.winjay.dlna.cast.dmc;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.Seek;

public class SeekCallback extends Seek {
    private static final String TAG = "SeekCallback";
    private Activity activity;
    private Handler mHandler;

    public SeekCallback(Activity paramActivity, Service paramService,
                        String paramString, Handler paramHandler) {
        super(paramService, paramString);
        activity = paramActivity;
        mHandler = paramHandler;
    }

    public void failure(ActionInvocation paramActionInvocation,
                        UpnpResponse paramUpnpResponse, String paramString) {
        LogUtil.w(TAG, "paramUpnpResponse=" + paramUpnpResponse.toString());
        LogUtil.w(TAG, "paramString=" + paramString);
    }

    public void sendBroadcast() {
        Intent localIntent = new Intent("com.continue.display");
        this.activity.sendBroadcast(localIntent);
    }

    public void success(ActionInvocation paramActionInvocation) {
        super.success(paramActionInvocation);
        LogUtil.d(TAG, "paramActionInvocation=" + paramActionInvocation.toString());
        mHandler.sendEmptyMessage(DMCControlMessage.GETPOTITION);
    }

}
