#include "SDL.h"
#include <libavcodec/avcodec.h>

#ifdef __ANDROID__
#include <jni.h>
#include <android/log.h>

#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR,"ERROR: ", __VA_ARGS__)
#define ALOGI(...) __android_log_print(ANDROID_LOG_INFO,"INFO: ", __VA_ARGS__)
#else
#define ALOGE(format, ...) printf("ERROR: " format "\n",##__VA_ARGS__)
#define ALOGI(format, ...) printf("INFO: " format "\n",##__VA_ARGS__)
#endif

int main(int argc, char *argv[]) {
    // 打印ffmpeg信息
    const char *str = avcodec_configuration();
    ALOGI("avcodec_configuration: %s", str);

    SDL_Window *window;
    SDL_Renderer *renderer;
    SDL_Event event;

    //配置一个图像缩放的效果,linear效果更平滑,也叫抗锯齿
    //SDL_setenv(SDL_HINT_RENDER_SCALE_QUALITY,"linear",0);

    // 初始化SDL
    if (SDL_Init(SDL_INIT_VIDEO) < 0)
        return 1;

    // 创建一个窗口
    window = SDL_CreateWindow("SDL_RenderClear", SDL_WINDOWPOS_CENTERED,
                              SDL_WINDOWPOS_CENTERED, 0, 0, SDL_WINDOW_SHOWN);

    // 创建一个渲染器
    renderer = SDL_CreateRenderer(window, -1, 0);

    // 创建一个Surface
    SDL_Surface *bmp = SDL_LoadBMP("image.bmp");

    //设置图片中的白色为透明色
    SDL_SetColorKey(bmp, SDL_TRUE, 0xffffff);

    // 创建一个Texture
    SDL_Texture *texture = SDL_CreateTextureFromSurface(renderer, bmp);

    //清除所有事件
    SDL_FlushEvents(SDL_FIRSTEVENT, SDL_LASTEVENT);

    //进入主循环
    while (1) {
        if (SDL_PollEvent(&event)) {
            if (event.type == SDL_QUIT || event.type == SDL_KEYDOWN || event.type == SDL_FINGERDOWN)
                break;
        }
        //使用红色填充背景
        SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);
        SDL_RenderClear(renderer);
        // 将纹理布置到渲染器
        SDL_RenderCopy(renderer, texture, NULL, NULL);
        // 刷新屏幕
        SDL_RenderPresent(renderer);
    }

    // 释放Surface
    SDL_FreeSurface(bmp);

    //  释放Texture
    SDL_DestroyTexture(texture);

    //释放渲染器
    SDL_DestroyRenderer(renderer);

    //释放窗口
    SDL_DestroyWindow(window);

    //延时
    //SDL_Delay(8000);

    //退出
    SDL_Quit();
    return 0;
}
