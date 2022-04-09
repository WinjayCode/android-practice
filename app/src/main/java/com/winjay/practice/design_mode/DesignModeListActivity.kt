package com.winjay.practice.design_mode

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.winjay.practice.MainAdapter
import com.winjay.practice.R
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.design_mode.mvc.controller.MVCActivity
import com.winjay.practice.design_mode.mvp.view.MVPActivity
import com.winjay.practice.design_mode.mvvm.databinding.MVVMDataBindingActivity
import com.winjay.practice.design_mode.mvvm.view.UserListActivity
import kotlinx.android.synthetic.main.main_activity.*
import java.util.*

class DesignModeListActivity : BaseActivity() {

    private val mainMap: LinkedHashMap<String?, Class<*>?> =
        object : LinkedHashMap<String?, Class<*>?>() {
            init {
                put("MVC", MVCActivity::class.java)
                put("MVP", MVPActivity::class.java)
                put("MVVM(DataBinding)", MVVMDataBindingActivity::class.java)
                put("MVVM", UserListActivity::class.java)
            }
        }

    override fun getLayoutId(): Int {
        return R.layout.main_activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        main_rv!!.layoutManager = LinearLayoutManager(this)
        val mainAdapter = MainAdapter(ArrayList(mainMap.keys))
        main_rv!!.adapter = mainAdapter
        mainAdapter.setOnItemClickListener { view, key ->
            val intent = Intent(this@DesignModeListActivity, mainMap[key])
            startActivity(intent)
        }
    }
}