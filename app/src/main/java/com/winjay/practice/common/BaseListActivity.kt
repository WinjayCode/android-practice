package com.winjay.practice.common

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.winjay.practice.MainAdapter
import com.winjay.practice.databinding.MainActivityBinding

/**
 * Common List Activity
 *
 * @author Winjay
 * @date 2022-07-28
 */
abstract class BaseListActivity : BaseActivity() {
    abstract fun getMainMap(): LinkedHashMap<String?, Class<*>?>

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
        val mainAdapter = MainAdapter(ArrayList(getMainMap().keys))
        binding.mainRv.adapter = mainAdapter
        mainAdapter.setOnItemClickListener { view, key ->
            startActivity(getMainMap()[key])
        }
    }
}