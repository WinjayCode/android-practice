
package com.winjay.dlna.cast.dmc;

import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo;
import org.fourthline.cling.support.model.MediaInfo;

public class GetMediaInfoCallback extends GetMediaInfo {
    private static final String TAG = "GetMediaInfoCallback";

    public GetMediaInfoCallback(Service service) {
        super(service);
    }

    @Override
    public void received(ActionInvocation paramActionInvocation, MediaInfo paramMediaInfo) {
        LogUtil.d(TAG, "paramActionInvocation=" + paramActionInvocation.toString());
        LogUtil.d(TAG, "getCurrentURI=" + paramMediaInfo.getCurrentURI());

    }

    @Override
    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
        LogUtil.w(TAG, "operation=" + operation.toString());
        LogUtil.w(TAG, "defaultMsg=" + defaultMsg);
    }

    public void success(ActionInvocation paramActionInvocation) {
        LogUtil.d(TAG, "paramActionInvocation=" + paramActionInvocation.toString());
    }
}
