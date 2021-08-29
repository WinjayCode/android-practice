#include "com_winjay_practice_jni_JniTestActivity.h"
#include <stdio.h>

JNIEXPORT jstring JNICALL Java_com_winjay_practice_jni_JniTestActivity_get
(JNIEnv *env, jobject thiz) {
    printf("invoke get in c++\n");
    return env->NewStringUTF("Hello from JNI!");
}

JNIEXPORT void JNICALL Java_com_winjay_practice_jni_JniTestActivity_set
(JNIEnv *env, jobject thiz, jstring string) {
    printf("invoke set in c++\n");
    char* str = (char*) env->GetStringUFTChars(string, NULL);
    printf("%s\n", str);
    env->ReleaseStringUTFChars(string, str);
}