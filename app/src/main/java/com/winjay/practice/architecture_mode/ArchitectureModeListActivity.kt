package com.winjay.practice.architecture_mode

import com.winjay.practice.common.BaseListActivity
import com.winjay.practice.architecture_mode.mvc.controller.MVCActivity
import com.winjay.practice.architecture_mode.mvi.view.MVIActivity
import com.winjay.practice.architecture_mode.mvp.view.MVPActivity
import com.winjay.practice.architecture_mode.mvvm.databinding.MVVMDataBindingActivity
import com.winjay.practice.architecture_mode.mvvm.view.MVVMActivity
import java.util.*

class ArchitectureModeListActivity : BaseListActivity() {
    override fun getMainMap(): LinkedHashMap<String?, Class<*>?> {
        return object : LinkedHashMap<String?, Class<*>?>() {
            init {
                put("MVC", MVCActivity::class.java)
                put("MVP", MVPActivity::class.java)
                put("MVVM(DataBinding)", MVVMDataBindingActivity::class.java)
                put("MVVM", MVVMActivity::class.java)
                put("MVI", MVIActivity::class.java)
            }
        }
    }
}