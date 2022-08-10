package com.winjay.practice.kotlin.coroutines

import android.os.Bundle
import android.view.View
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.databinding.ActivityKotlinCoroutinesTestBinding
import com.winjay.practice.utils.LogUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

/**
 * 协程是轻量级的线程
 * ⾮阻塞的 delay(……) 与 阻塞的 Thread.sleep(……)
 * runBlocking{} 主线程会⼀直阻塞直到 runBlocking 内部的协程执⾏完毕。
 *
 * @author Winjay
 * @date 2022/02/16
 */
class KotlinCoroutinesTestActivity : BaseActivity() {
    companion object {
        private const val TAG = "KotlinCoroutinesTestActivity"
    }

    private lateinit var binding: ActivityKotlinCoroutinesTestBinding

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun viewBinding(): View {
        binding = ActivityKotlinCoroutinesTestBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.coroutinesBtn.setOnClickListener {
            // 打印hello world方法一
            GlobalScope.launch { // 在后台启动⼀个新的协程并继续
                delay(1000L) // ⾮阻塞的等待 1 秒钟（默认时间单位是毫秒）
                LogUtil.d(TAG, "World!") // 在延迟后打印输出
            }

            LogUtil.d(TAG, "Hello,") // 协程已在等待时主线程还在继续

            // 打印hello world方法二
            thread {
                Thread.sleep(1000L) // ⾮阻塞的等待 1 秒钟（默认时间单位是毫秒）
                LogUtil.d(TAG, "World!")
            }
            LogUtil.d(TAG, "Hello,")
        }
    }
}