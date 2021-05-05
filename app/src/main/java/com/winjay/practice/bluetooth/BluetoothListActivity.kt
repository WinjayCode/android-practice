package com.winjay.practice.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.winjay.practice.MainAdapter
import com.winjay.practice.R
import com.winjay.practice.bluetooth.a2dp.A2dpActivity
import com.winjay.practice.bluetooth.bt.BtClientActivity
import com.winjay.practice.bluetooth.bt.BtServerActivity
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.utils.LogUtil
import kotlinx.android.synthetic.main.main_activity.*
import java.util.*

/**
 * 蓝牙相关
 *
 * @author Winjay
 * @date 2021-04-29
 */
class BluetoothListActivity : BaseActivity() {
    private val TAG = javaClass.simpleName

    private val mainMap: LinkedHashMap<String?, Class<*>?> = object : LinkedHashMap<String?, Class<*>?>() {
        init {
            put("传统蓝牙A2DP模式", A2dpActivity::class.java)
            put("传统蓝牙客户端(流模式)", BtClientActivity::class.java)
            put("传统蓝牙服务端(流模式)", BtServerActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.main_activity
    }

    override fun permissions(): Array<String>? {
        return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 蓝牙权限
        if (!hasPermissions()) {
            requestPermissions()
        }

        // Android 10 还需要开启GPS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                toast("请开启GPS，否则蓝牙不可用！")
            }
        }

        val bluetooth = BluetoothAdapter.getDefaultAdapter()
        if (bluetooth == null) {
            toast("设备未找到蓝牙驱动！")
            finish()
        } else {
            if (!bluetooth.isEnabled) {
                // 请求打开蓝牙
                startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1)
            }
        }

        // 让设备处于可检测模式的时间为 5 分钟 (300s)
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivityForResult(discoverableIntent, 2)

        main_rv!!.layoutManager = LinearLayoutManager(this)
        val mainAdapter = MainAdapter(ArrayList(mainMap.keys))
        main_rv!!.adapter = mainAdapter
        mainAdapter.setOnItemClickListener { view, key ->
            val intent = Intent(this@BluetoothListActivity, mainMap[key])
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                toast("请打开蓝牙!")
                finish()
            }
        }
        if (requestCode == 2) {
            LogUtil.d(TAG, "resultCode:$resultCode")
            if (resultCode == Activity.RESULT_CANCELED) {
                toast("请允许设备可被检测!")
                finish()
            }
        }
    }
}