#include <jni.h>
#include <stdio.h>

#ifdef __cplusplus
extern "C" {
#endif

jstring Java_com_winjay_practice_jni_JniTestActivity_get(JNIEnv *env, jobject thiz) {
    printf("invoke get in c++\n");
    return env->NewStringUTF("Hello from JNI in libjni-test.so");
}

void Java_com_winjay_practice_jni_JniTestActivity_set(JNIEnv *env, jobject thiz, jstring string) {
    printf("invoke set from c++\n");
    char* str = (char*)env->GetStringUTFChars(string, NULL);
    printf("%s\n", str);
    env->ReleaseStringUTFChars(string, str);
}

#ifdef __cplusplus
}
#endif