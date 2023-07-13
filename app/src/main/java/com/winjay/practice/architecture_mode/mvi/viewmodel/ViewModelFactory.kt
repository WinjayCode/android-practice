package com.winjay.practice.architecture_mode.mvi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.winjay.practice.architecture_mode.mvi.network.ApiService
import com.winjay.practice.architecture_mode.mvi.model.MVIRepository

/**
 * ViewModel工厂
 */
class ViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 判断 MVIViewModel 是不是 modelClass 的父类或接口
        if (modelClass.isAssignableFrom(MVIViewModel::class.java)) {
            return MVIViewModel(MVIRepository(apiService)) as T
        }
        throw IllegalArgumentException("UnKnown class")
    }
}