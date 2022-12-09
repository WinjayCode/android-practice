LOCAL_PATH := $(call my-dir)

###########################
#
# FFmpeg shared library
#
###########################

include $(CLEAR_VARS)

LOCAL_MODULE:= ffmpeg


LOCAL_SRC_FILES:= $(LOCAL_PATH)/libs/armeabi-v7a/libffmpeg.so

LOCAL_EXPORT_C_INCLUDES:= $(LOCAL_PATH)/include

include $(PREBUILT_SHARED_LIBRARY)