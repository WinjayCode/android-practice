package com.winjay.practice.kotlin

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.winjay.practice.MainAdapter
import com.winjay.practice.R
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.kotlin.coroutines.KotlinCoroutinesTestActivity
import kotlinx.android.synthetic.main.main_activity.*
import java.util.*

class KotlinListActivity : BaseActivity() {

    private val mainMap: LinkedHashMap<String?, Class<*>?> =
        object : LinkedHashMap<String?, Class<*>?>() {
            init {
                put("Test", KotlinTestActivity::class.java)
                put("Coroutines", KotlinCoroutinesTestActivity::class.java)
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
            val intent = Intent(this@KotlinListActivity, mainMap[key])
            startActivity(intent)
        }
    }
}