package com.winjay.practice.usb;

public class VolumeInfo {
    public static final String ACTION_VOLUME_STATE_CHANGED = "android.os.storage.action.VOLUME_STATE_CHANGED";

    public static final String EXTRA_VOLUME_ID = "android.os.storage.extra.VOLUME_ID";

    public static final String EXTRA_VOLUME_STATE = "android.os.storage.extra.VOLUME_STATE";

    public static final int STATE_MOUNTED = 2;

    public static final int STATE_MOUNTED_READ_ONLY = 3;

    public static final int STATE_UNMOUNTED = 0;

    public static final int STATE_BAD_REMOVAL = 8;
}
