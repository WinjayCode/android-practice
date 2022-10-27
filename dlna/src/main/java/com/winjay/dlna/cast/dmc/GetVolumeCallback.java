package com.winjay.dlna.cast.dmc;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;

public class GetVolumeCallback extends GetVolume {
    private static final String TAG = "GetVolumeCallback";
    private Activity activity;

    private Handler handler;

    private int isSetVolumeFlag = 0;

    private int type;

    public GetVolumeCallback(Activity paramActivity, Handler paramHandler,
                             int paramInt1, Service paramService, int paramInt2) {
        super(paramService);
        this.activity = paramActivity;
        this.handler = paramHandler;
        this.isSetVolumeFlag = paramInt1;
        this.type = paramInt2;
    }

    public void failure(ActionInvocation paramActionInvocation,
                        UpnpResponse paramUpnpResponse, String paramString) {
        LogUtil.w(TAG, "paramUpnpResponse=" + paramUpnpResponse.toString());
        LogUtil.w(TAG, "paramString=" + paramString);
        LogUtil.d(TAG, "type=" + type);

        if (this.type == 1) {
            this.handler.sendEmptyMessage(DMCControlMessage.PLAYIMAGEFAILED);
        } else if (this.type == 2) {
            this.handler.sendEmptyMessage(DMCControlMessage.PLAYAUDIOFAILED);
        } else if (this.type == 3) {
            this.handler.sendEmptyMessage(DMCControlMessage.PLAYVIDEOFAILED);
        }
    }

    public void received(ActionInvocation paramActionInvocation, int paramInt) {
        LogUtil.d(TAG, "param=" + paramInt);
        Message localMessage = new Message();
        localMessage.what = DMCControlMessage.SETVOLUME;
        Bundle localBundle = new Bundle();
        localBundle.putLong("getVolume", paramInt);
        localBundle.putInt("isSetVolume", isSetVolumeFlag);
        localMessage.setData(localBundle);
        handler.sendMessage(localMessage);
    }

    @Override
    public void success(ActionInvocation invocation) {
        super.success(invocation);
        LogUtil.d(TAG, "paramActionInvocation=" + invocation.toString());
    }
}
