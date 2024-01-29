package com.winjay.scrcpy;

public class Constants {
    public static final String COMMAND_SPLIT = "/";

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
}
