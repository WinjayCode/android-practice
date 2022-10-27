
package com.winjay.dlna.cast.dmc;


import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.Pause;

public class PauseCallback extends Pause {
    private static final String TAG = "PauseCallback";

    public PauseCallback(Service paramService) {
        super(paramService);
    }

    public void failure(ActionInvocation paramActionInvocation, UpnpResponse paramUpnpResponse,
                        String paramString) {
        LogUtil.w(TAG, "paramUpnpResponse=" + paramUpnpResponse.toString());
        LogUtil.w(TAG, "paramString=" + paramString);
    }

    public void success(ActionInvocation paramActionInvocation) {
        super.success(paramActionInvocation);
        LogUtil.d(TAG, "paramActionInvocation=" + paramActionInvocation.toString());
    }

}
