LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
#模块名称
LOCAL_MODULE := jni-test
#要参与编译的源文件
LOCAL_SRC_FILES := jni-test.cpp
include $(BUILD_SHARED_LIBRARY)