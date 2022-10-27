package com.winjay.dlna.cast.dmc;

import android.os.Handler;

import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.connectionmanager.callback.GetProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfos;

public class GetProtocolInfoCallback extends GetProtocolInfo {

    private static final String TAG = "GetProtocolInfoCallback";

    private Handler handler;

    private boolean hasType = false;

    private String requestPlayMimeType = "";

    public GetProtocolInfoCallback(Service paramService,
                                   ControlPoint paramControlPoint, String paramString,
                                   Handler paramHandler) {
        super(paramService, paramControlPoint);
        this.requestPlayMimeType = paramString;
        this.handler = paramHandler;
    }

    public void failure(ActionInvocation paramActionInvocation,
                        UpnpResponse paramUpnpResponse, String paramString) {
        LogUtil.w(TAG, "paramUpnpResponse=" + paramUpnpResponse.toString());
        LogUtil.w(TAG, "paramString=" + paramString);
        this.handler.sendEmptyMessage(DMCControlMessage.CONNECTIONFAILED);
    }

    public void received(ActionInvocation paramActionInvocation,
                         ProtocolInfos paramProtocolInfos1, ProtocolInfos paramProtocolInfos2) {
        LogUtil.d(TAG, "GetProtocolInfo  success");
        this.handler.sendEmptyMessage(DMCControlMessage.CONNECTIONSUCESSED);
    }

    @Override
    public void success(ActionInvocation invocation) {
        super.success(invocation);
        LogUtil.d(TAG, "paramActionInvocation=" + invocation.toString());
    }
}
