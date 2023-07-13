package com.winjay.practice.architecture_mode.mvi.intent

/**
 * MVI的Intent(页面意图)
 *
 * @author Winjay
 * @date 2023-07-13
 */
sealed class MVIIntent {
    /**
     * 获取壁纸
     */
    object GetWallpaper : MVIIntent()
}