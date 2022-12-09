LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := main

SDL_PATH := ../sdl
FFMPEG_PATH := ../ffmpeg

LOCAL_C_INCLUDES := $(LOCAL_PATH)/$(SDL_PATH)/include \
                    $(LOCAL_PATH)/$(FFMPEG_PATH)/include \
                    $(LOCAL_PATH)/$(FFMPEG_PATH)/include/libavcodec

# Add your application source files here...
LOCAL_SRC_FILES := native_render.c

LOCAL_SHARED_LIBRARIES := SDL2 ffmpeg

LOCAL_LDLIBS := -lGLESv1_CM -lGLESv2 -lOpenSLES -llog -landroid

include $(BUILD_SHARED_LIBRARY)
