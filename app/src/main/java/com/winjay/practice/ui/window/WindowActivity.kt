package com.winjay.practice.ui.window

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.winjay.practice.R
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.utils.ToastUtil

/**
 * Window、WindowManager
 *
 * @author Winjay
 * @date 2022-06-12
 */
class WindowActivity : BaseActivity() {
    private lateinit var windowManager: WindowManager
    private lateinit var windowManagerParams: WindowManager.LayoutParams
    private lateinit var view: View

    override fun getLayoutId(): Int {
        return R.layout.activity_window
    }

    override fun onResume() {
        super.onResume()
        windowManager = getSystemService(WindowManager::class.java)
        findViewById<Button>(R.id.quit).setOnClickListener{
            finish()
        }
        requestPermission()
    }

    override fun onPause() {
        super.onPause()
        removeView()
    }

    private fun requestPermission() {
        // 权限判断
        if (!Settings.canDrawOverlays(this)) {
            // 启动Activity让用户授权
            val mIntent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${packageName}")
            )
            startActivityForResult(mIntent, 10)
        } else {
            // 已经有权限了,就去初始化对应的视图或者悬浮窗弹窗的初始化
            addView()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 10) {
            if (Settings.canDrawOverlays(applicationContext)) {
                addView()
            } else {
                ToastUtil.show(this, "请设置对应权限")
            }
        }
    }

    private fun addView() {
        windowManagerParams = WindowManager.LayoutParams()
        windowManagerParams.width = 300
        windowManagerParams.height = 300
        windowManagerParams.gravity = Gravity.START or Gravity.TOP
        windowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        windowManagerParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        view = LayoutInflater.from(this).inflate(R.layout.activity_window, null)
        windowManager.addView(view, windowManagerParams)
    }

    private fun removeView() {
        windowManager.removeView(view)
    }
}