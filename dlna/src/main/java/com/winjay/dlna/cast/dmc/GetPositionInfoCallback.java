package com.winjay.dlna.cast.dmc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.winjay.dlna.cast.util.Action;
import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.model.PositionInfo;

public class GetPositionInfoCallback extends GetPositionInfo {
    private static final String TAG = "GetPositionInfoCallback";

    private Activity activity;

    private Handler handler;

    public GetPositionInfoCallback(Service paramService, Handler paramHandler, Activity paramActivity) {
        super(paramService);
        this.handler = paramHandler;
        this.activity = paramActivity;
    }

    public void failure(ActionInvocation paramActionInvocation,
                        UpnpResponse paramUpnpResponse, String paramString) {
        LogUtil.w(TAG, "paramUpnpResponse=" + paramUpnpResponse.toString());
        LogUtil.w(TAG, "paramString=" + paramString);
    }

    public void received(ActionInvocation paramActionInvocation, PositionInfo paramPositionInfo) {
        LogUtil.d(TAG, "duration=" + paramPositionInfo.getTrackDuration());
        LogUtil.d(TAG, "currentTime=" + paramPositionInfo.getRelTime());

        Bundle localBundle = new Bundle();
        localBundle.putString("TrackDuration", paramPositionInfo.getTrackDuration());
        localBundle.putString("RelTime", paramPositionInfo.getRelTime());
        Intent localIntent = new Intent(Action.PLAY_UPDATE);
        localIntent.putExtras(localBundle);
        activity.sendBroadcast(localIntent);
    }

    public void success(ActionInvocation paramActionInvocation) {
        super.success(paramActionInvocation);
        LogUtil.d(TAG, "paramActionInvocation=" + paramActionInvocation.toString());
    }

}
