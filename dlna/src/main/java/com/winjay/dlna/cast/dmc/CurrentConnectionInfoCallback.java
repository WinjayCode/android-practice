
package com.winjay.dlna.cast.dmc;


import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.connectionmanager.callback.GetCurrentConnectionInfo;
import org.fourthline.cling.support.model.ConnectionInfo;

public class CurrentConnectionInfoCallback extends GetCurrentConnectionInfo {
    private static final String TAG = "CurrentConnectionInfoCallback";

    public CurrentConnectionInfoCallback(Service paramService, ControlPoint paramControlPoint,
            int paramInt) {
        super(paramService, paramControlPoint, paramInt);
    }

    public void failure(ActionInvocation paramActionInvocation, UpnpResponse paramUpnpResponse,
            String paramString) {
        LogUtil.w(TAG, "paramUpnpResponse=" + paramUpnpResponse.toString());
        LogUtil.w(TAG, "paramString=" + paramString);
    }

    public void received(ActionInvocation paramActionInvocation, ConnectionInfo paramConnectionInfo) {
        LogUtil.d(TAG, "ConnectionID=" + paramConnectionInfo.getConnectionID());
        LogUtil.d(TAG, "ConnectionStatus=" + paramConnectionInfo.getConnectionStatus());
    }

}
