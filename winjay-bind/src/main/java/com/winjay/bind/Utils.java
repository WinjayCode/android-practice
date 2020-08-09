package com.winjay.bind;

import android.app.Activity;
import android.view.View;

public class Utils {
    public static <T extends View> T findViewById(Activity activity, int viewId) {
        return activity.findViewById(viewId);
    }
}
