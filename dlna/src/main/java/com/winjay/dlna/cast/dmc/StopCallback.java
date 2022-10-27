
package com.winjay.dlna.cast.dmc;

import android.os.Handler;

import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.Stop;

public class StopCallback extends Stop {
    private static final String TAG = "StopCallback";
    private Handler handler;

    private Boolean isRePlay = false;

    private int type;

    public StopCallback(Service paramService, Handler paramHandler, Boolean paramBoolean,
                        int paramInt) {
        super(paramService);
        this.handler = paramHandler;
        this.isRePlay = paramBoolean;
        this.type = paramInt;
    }

    public void failure(ActionInvocation paramActionInvocation, UpnpResponse paramUpnpResponse,
                        String paramString) {
        LogUtil.w(TAG, "paramUpnpResponse=" + paramUpnpResponse.toString());
        LogUtil.w(TAG, "paramString=" + paramString);
        LogUtil.d(TAG, "type=" + type);
        if (this.type == 1)
            this.handler.sendEmptyMessage(DMCControlMessage.PLAYIMAGEFAILED);
        if (this.type == 2)
            this.handler.sendEmptyMessage(DMCControlMessage.PLAYAUDIOFAILED);
        if (this.type == 3)
            this.handler.sendEmptyMessage(DMCControlMessage.PLAYVIDEOFAILED);
    }

    public void success(ActionInvocation paramActionInvocation) {
        super.success(paramActionInvocation);
        LogUtil.d(TAG, "paramActionInvocation=" + paramActionInvocation.toString());
        if (!isRePlay.booleanValue()) {
            this.handler.sendEmptyMessage(DMCControlMessage.SETURL);
        } else {
            this.handler.sendEmptyMessage(DMCControlMessage.GETTRANSPORTINFO);
        }
    }

}
