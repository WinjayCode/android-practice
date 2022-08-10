package com.winjay.practice.design_mode

import com.winjay.practice.common.BaseListActivity
import com.winjay.practice.design_mode.mvc.controller.MVCActivity
import com.winjay.practice.design_mode.mvp.view.MVPActivity
import com.winjay.practice.design_mode.mvvm.databinding.MVVMDataBindingActivity
import com.winjay.practice.design_mode.mvvm.view.UserListActivity
import java.util.*

class DesignModeListActivity : BaseListActivity() {
    override fun getMainMap(): LinkedHashMap<String?, Class<*>?> {
        return object : LinkedHashMap<String?, Class<*>?>() {
            init {
                put("MVC", MVCActivity::class.java)
                put("MVP", MVPActivity::class.java)
                put("MVVM(DataBinding)", MVVMDataBindingActivity::class.java)
                put("MVVM", UserListActivity::class.java)
            }
        }
    }
}