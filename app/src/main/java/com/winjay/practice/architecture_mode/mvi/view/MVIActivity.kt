package com.winjay.practice.architecture_mode.mvi.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.winjay.practice.architecture_mode.mvi.viewmodel.MVIViewModel
import com.winjay.practice.architecture_mode.mvi.intent.MVIIntent
import com.winjay.practice.architecture_mode.mvi.network.NetworkUtils
import com.winjay.practice.architecture_mode.mvi.state.MVIState
import com.winjay.practice.architecture_mode.mvi.viewmodel.ViewModelFactory
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.databinding.MviActivityBinding
import kotlinx.coroutines.launch

/**
 * MVI的View
 *
 * @author Winjay
 * @date 2023-07-12
 */
class MVIActivity : BaseActivity() {
    private lateinit var binding: MviActivityBinding

    private lateinit var mviViewModel: MVIViewModel

    private var mviAdapter = MVIAdapter(arrayListOf())

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun viewBinding(): View {
        binding = MviActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //绑定ViewModel
        mviViewModel = ViewModelProvider(
            this, ViewModelFactory(NetworkUtils.apiService)
        )[MVIViewModel::class.java]
        //初始化
        initView()
        //观察ViewModel
        observeViewModel()
    }

    /**
     * 初始化
     */
    private fun initView() {
        //RV配置
        binding.rvWallpaper.apply {
            layoutManager = GridLayoutManager(this@MVIActivity, 2)
            adapter = mviAdapter
        }
        //按钮点击
        binding.btnGetWallpaper.setOnClickListener {
            lifecycleScope.launch {
                //发送意图
                mviViewModel.mainIntentChannel.send(MVIIntent.GetWallpaper)
            }
        }
    }

    /**
     * 观察ViewModel
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            //状态收集
            mviViewModel.state.collect {
                when (it) {
                    is MVIState.Idle -> {

                    }
                    is MVIState.Loading -> {
                        binding.btnGetWallpaper.visibility = View.GONE
                        binding.pbLoading.visibility = View.VISIBLE
                    }
                    //数据返回
                    is MVIState.Wallpapers -> {
                        binding.btnGetWallpaper.visibility = View.GONE
                        binding.pbLoading.visibility = View.GONE

                        binding.rvWallpaper.visibility = View.VISIBLE
                        it.wallpaper.let { paper ->
                            mviAdapter.addData(paper.res.vertical)
                        }
                        mviAdapter.notifyDataSetChanged()
                    }
                    is MVIState.Error -> {
                        binding.pbLoading.visibility = View.GONE
                        binding.btnGetWallpaper.visibility = View.VISIBLE
                        Log.d("TAG", "observeViewModel: $it.error")
                        Toast.makeText(this@MVIActivity, it.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}