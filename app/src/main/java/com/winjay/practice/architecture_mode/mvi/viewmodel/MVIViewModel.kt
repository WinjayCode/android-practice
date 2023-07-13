package com.winjay.practice.architecture_mode.mvi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winjay.practice.architecture_mode.mvi.intent.MVIIntent
import com.winjay.practice.architecture_mode.mvi.model.MVIRepository
import com.winjay.practice.architecture_mode.mvi.state.MVIState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

/**
 * MVI的ViewModel
 *
 * @author Winjay
 * @date 2023-07-13
 */
class MVIViewModel(private val repository: MVIRepository) : ViewModel() {

    //创建意图管道，容量无限大
    val mainIntentChannel = Channel<MVIIntent>(Channel.UNLIMITED)

    //可变状态数据流
    private val _state = MutableStateFlow<MVIState>(MVIState.Idle)

    //可观察状态数据流
    val state: StateFlow<MVIState> get() = _state

    init {
        viewModelScope.launch {
            //收集意图
            mainIntentChannel.consumeAsFlow().collect {
                when (it) {
                    //发现意图为获取壁纸
                    is MVIIntent.GetWallpaper -> getWallpaper()
                }
            }
        }
    }

    /**
     * 获取壁纸
     */
    private fun getWallpaper() {
        viewModelScope.launch {
            //修改状态为加载中
            _state.value = MVIState.Loading
            //网络请求状态
            _state.value = try {
                //请求成功
                MVIState.Wallpapers(repository.getWallPaper())
            } catch (e: Exception) {
                //请求失败
                MVIState.Error(e.localizedMessage ?: "UnKnown Error")
            }
        }
    }
}