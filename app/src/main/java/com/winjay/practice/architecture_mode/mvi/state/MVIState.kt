package com.winjay.practice.architecture_mode.mvi.state

import com.winjay.practice.architecture_mode.mvi.bean.Wallpaper


/**
 * MVI的State(页面状态)
 *
 * @author Winjay
 * @date 2023-07-12
 */
sealed class MVIState {
    /**
     * 空闲
     */
    object Idle : MVIState()

    /**
     * 加载
     */
    object Loading : MVIState()

    /**
     * 获取壁纸
     */
    data class Wallpapers(val wallpaper: Wallpaper) : MVIState()

    /**
     * 错误信息
     */
    data class Error(val error: String) : MVIState()
}
