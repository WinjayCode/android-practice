package com.winjay.mirrorcast;

public class Constants {

    public static final int IP_PORT = 1994;

    public static final int APP_SOCKET_PORT = 13346;

    public static final int CAR_LAUNCHER_MIRROR_CAST_SERVER_PORT = 12345;
    public static final int PHONE_MAIN_SCREEN_MIRROR_CAST_SERVER_PORT = 12346;
    public static final int PHONE_APP_MIRROR_CAST_SERVER_PORT = 12347;

    public static final String COMMAND_SPLIT = "/";


    public static final String APP_COMMAND_CREATE_VIRTUAL_DISPLAY = "APP_COMMAND_CREATE_VIRTUAL_DISPLAY";
    public static final String APP_COMMAND_SHOW_TIPS = "APP_COMMAND_SHOW_TIPS";
    public static final String APP_COMMAND_PHONE_MAIN_SCREEN_MIRROR_CAST = "APP_COMMAND_PHONE_MAIN_SCREEN_MIRROR_CAST";
    public static final String APP_COMMAND_PHONE_APP_MIRROR_CAST = "APP_COMMAND_PHONE_APP_MIRROR_CAST";
    public static final String APP_COMMAND_RETURN_CAR_SYSTEM = "APP_COMMAND_RETURN_CAR_SYSTEM";

    public static final String APP_REPLY_VIRTUAL_DISPLAY_ID = "APP_REPLY_VIRTUAL_DISPLAY_ID";
    public static final String APP_REPLY_CHECK_SCRCPY_SERVER_JAR = "APP_REPLY_CHECK_SCRCPY_SERVER_JAR";


    public static final String SCRCPY_COMMAND_START_PHONE_APP_MIRROR_CAST = "SCRCPY_COMMAND_START_PHONE_APP_MIRROR_CAST";
    public static final String SCRCPY_COMMAND_MOVE_PHONE_APP_STACK_MIRROR_CAST = "SCRCPY_COMMAND_MOVE_PHONE_APP_STACK_MIRROR_CAST";
    public static final String SCRCPY_COMMAND_MOTION_EVENT = "SCRCPY_COMMAND_MOTION_EVENT";

    public static final String SCRCPY_REPLY_VIDEO_SIZE = "SCRCPY_REPLY_VIDEO_SIZE";
}
