package com.winjay.mirrorcast;

public class Constants {
    public static final int APP_SOCKET_PORT = 13346;

    public static final int CAR_LAUNCHER_MIRROR_CAST_SERVER_PORT = 12345;
    public static final int PHONE_MAIN_SCREEN_MIRROR_CAST_SERVER_PORT = 12346;
    public static final int PHONE_APP_MIRROR_CAST_SERVER_PORT = 12347;

    public static final String COMMAND_SPLIT = "/";


    /**
     * Vehicle -> Phone App 检查手机/data/local/tmp目录下是否存在Scrcpy的包
     */
    public static final String APP_COMMAND_CHECK_SCRCPY_SERVER_JAR = "APP_COMMAND_CHECK_SCRCPY_SERVER_JAR";
    /**
     * Vehicle -> Phone App 创建虚拟屏
     */
    public static final String APP_COMMAND_CREATE_VIRTUAL_DISPLAY = "APP_COMMAND_CREATE_VIRTUAL_DISPLAY";
    /**
     * Vehicle -> Phone App 手机显示Tips页面
     */
    public static final String APP_COMMAND_SHOW_TIPS = "APP_COMMAND_SHOW_TIPS";
    /**
     * Vehicle -> Phone App AOA有线连接时车机通知手机PhoneAOASocketServer需要监听的端口
     */
    public static final String APP_COMMAND_AOA_SERVER_PORT = "APP_COMMAND_AOA_SERVER_PORT";

    /**
     * Phone App -> Vehicle 告知车机启动手机主屏幕镜像投屏
     */
    public static final String APP_COMMAND_PHONE_MAIN_SCREEN_MIRROR_CAST = "APP_COMMAND_PHONE_MAIN_SCREEN_MIRROR_CAST";
    /**
     * Phone App -> Vehicle 告知车机启动手机应用镜像投屏
     */
    public static final String APP_COMMAND_PHONE_APP_MIRROR_CAST = "APP_COMMAND_PHONE_APP_MIRROR_CAST";
    /**
     * Phone App -> Vehicle 告知车机需要退出到车机系统页面
     */
    public static final String APP_COMMAND_RETURN_CAR_SYSTEM = "APP_COMMAND_RETURN_CAR_SYSTEM";
    /**
     * Phone App -> Vehicle 告知车机手机上新创建的虚拟屏ID
     */
    public static final String APP_REPLY_VIRTUAL_DISPLAY_ID = "APP_REPLY_VIRTUAL_DISPLAY_ID";
    /**
     * Phone App -> Vehicle 告知车机当前手机/data/local/tmp目录下是否存在Scrcpy的包
     */
    public static final String APP_REPLY_CHECK_SCRCPY_SERVER_JAR = "APP_REPLY_CHECK_SCRCPY_SERVER_JAR";


    /**
     * Vehicle Decoder -> Scrcpy 启动手机上的app页面到虚拟屏上
     */
    public static final String SCRCPY_COMMAND_START_PHONE_APP_MIRROR_CAST = "SCRCPY_COMMAND_START_PHONE_APP_MIRROR_CAST";
    /**
     * Vehicle Decoder -> Scrcpy 通知手机上的当前app页面移栈
     */
    public static final String SCRCPY_COMMAND_MOVE_PHONE_APP_STACK_MIRROR_CAST = "SCRCPY_COMMAND_MOVE_PHONE_APP_STACK_MIRROR_CAST";
    /**
     * Vehicle Decoder -> Scrcpy 通知Scrcpy处理触摸事件
     */
    public static final String SCRCPY_COMMAND_MOTION_EVENT = "SCRCPY_COMMAND_MOTION_EVENT";

    /**
     * Scrcpy -> Vehicle Decoder 告诉解码器投屏宽高比信息
     */
    public static final String SCRCPY_REPLY_VIDEO_SIZE = "SCRCPY_REPLY_VIDEO_SIZE";


    /**
     * WIFI直连
     */
    public static final int CONNECT_TYPE_WIFI_DIRECT = 1;
    /**
     * AOA有线连接
     */
    public static final int CONNECT_TYPE_AOA = 2;
}
