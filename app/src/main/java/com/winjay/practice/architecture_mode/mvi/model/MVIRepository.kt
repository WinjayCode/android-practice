package com.winjay.practice.architecture_mode.mvi.model

import com.winjay.practice.architecture_mode.mvi.network.ApiService


/**
 * MVI的Model(数据存储库)
 *
 * @author Winjay
 * @date 2023-07-13
 */
class MVIRepository(private val apiService: ApiService) {

    /**
     * 获取壁纸
     */
    suspend fun getWallPaper() = apiService.getWallPaper()
}