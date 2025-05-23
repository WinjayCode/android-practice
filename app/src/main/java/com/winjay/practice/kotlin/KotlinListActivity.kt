package com.winjay.practice.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.winjay.practice.MainAdapter
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.databinding.MainActivityBinding
import com.winjay.practice.kotlin.coroutines.KotlinCoroutinesTestActivity
import java.util.*

class KotlinListActivity : BaseActivity() {

    private val mainMap: LinkedHashMap<String?, Class<*>?> =
        object : LinkedHashMap<String?, Class<*>?>() {
            init {
                put("Test", KotlinTestActivity::class.java)
                put("Coroutines", KotlinCoroutinesTestActivity::class.java)
            }
        }

    private lateinit var binding: MainActivityBinding

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun viewBinding(): View {
        binding = MainActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mainRv.layoutManager = LinearLayoutManager(this)
        val mainAdapter = MainAdapter(ArrayList(mainMap.keys))
        binding.mainRv.adapter = mainAdapter
        mainAdapter.setOnItemClickListener { view, key ->
            val intent = Intent(this@KotlinListActivity, mainMap[key])
            startActivity(intent)
        }
    }
}