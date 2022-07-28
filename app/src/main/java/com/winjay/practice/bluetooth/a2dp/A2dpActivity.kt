package com.winjay.practice.bluetooth.a2dp

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.OnClick
import com.winjay.practice.R
import com.winjay.practice.bluetooth.BtUtil
import com.winjay.practice.bluetooth.bt.BluetoothListAdapter
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.databinding.A2dpActivityBinding
import com.winjay.practice.utils.LogUtil

/**
 * 连接蓝牙音箱，并播放声音
 *
 * @author Winjay
 * @date 2021-05-05
 */
class A2dpActivity : BaseActivity() {
    private val TAG = javaClass.simpleName
    private lateinit var bluetoothListAdapter: BluetoothListAdapter
    private var bluetoothListData: MutableList<BluetoothDevice> = mutableListOf()
    private var bluetoothA2dp: BluetoothA2dp? = null
    private var itemStateTV: TextView? = null
    private var connectThread: ConnectThread? = null
    private var bluetoothBroadcast: BluetoothBroadcast? = null

    private lateinit var binding: A2dpActivityBinding

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun viewBinding(): View {
        binding = A2dpActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothListAdapter = BluetoothListAdapter(bluetoothListData)
        registerBluetoothBroadcast()
        initRecyclerView()

        val bondedDevices = BtUtil.bluetooth.bondedDevices
        LogUtil.d(TAG, "bondedDevices.size=${bondedDevices.size}")
        for (dev in bondedDevices) {
            if (dev !in bluetoothListData && dev.name != null) {
                bluetoothListData.add(dev)
                bluetoothListAdapter.notifyItemInserted(bluetoothListData.size)
            }
        }

        BtUtil.bluetooth.cancelDiscovery()
        BtUtil.bluetooth.startDiscovery()

        BtUtil.bluetooth.getProfileProxy(this, object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                if (profile == BluetoothProfile.A2DP) {
                    LogUtil.d(TAG)
                    bluetoothA2dp = proxy as BluetoothA2dp
                }
            }

            override fun onServiceDisconnected(profile: Int) {
                if (profile == BluetoothProfile.A2DP) {
                    LogUtil.d(TAG)
                    bluetoothA2dp = null
                }
            }
        }, BluetoothProfile.A2DP)
    }

    private fun initRecyclerView() {
        binding.deviceRv.layoutManager = LinearLayoutManager(this)
        binding.deviceRv.adapter = bluetoothListAdapter
        bluetoothListAdapter.setOnItemClickListener(object : BluetoothListAdapter.OnItemClickListener {
            override fun onItemClick(view: View) {
                itemStateTV = view.findViewById(R.id.blue_item_status_tv)
                val position = binding.deviceRv.getChildAdapterPosition(view)
                LogUtil.d(TAG, "position=$position")
                toast("开始连接...")
                connectThread = ConnectThread(bluetoothListData[position],
                        object : ConnectListener {
                            override fun onStart() {
                                LogUtil.d(TAG)
                            }

                            override fun onConnected() {
                                LogUtil.d(TAG)
                            }

                            override fun onFail(error: String) {
                                LogUtil.d(TAG, "error:$error")
                            }

                        })
                connectThread?.start()
            }
        })
    }

    inner class ConnectThread(
            private val bluetoothDevice: BluetoothDevice,
            private val listener: ConnectListener) : Thread() {
        private var socket: BluetoothSocket? = null

        override fun run() {
            super.run()
            listener.onStart()
            BtUtil.bluetooth.cancelDiscovery()
            while (true) {
                try {
                    // 先绑定
                    if (bluetoothDevice.bondState != BluetoothDevice.BOND_BONDED) {
                        val createSocket = BluetoothDevice::class.java.getMethod(
                                "createRfcommSocket",
                                Int::class.java
                        )
                        createSocket.isAccessible = true
                        // 找一个通道去连接，channel 1~30
                        socket = createSocket.invoke(bluetoothDevice, 1) as BluetoothSocket
                        // 阻塞等待
                        socket?.connect()
                        // 延时，以便于出连接
                        sleep(2000)
                    }

                    if (connectA2dp(bluetoothDevice)) {
                        listener.onConnected()
                        break
                    } else {
                        listener.onFail("connect fail!")
                    }
                } catch (e: Exception) {
                    LogUtil.d(TAG, "connect fail!")
                    listener.onFail(e.message.toString())
                    return
                }
            }
        }

        fun cancel() {
            socket?.close()
            // 取消绑定
            try {
                val disconnect = BluetoothA2dp::class.java.getMethod("disconnect", BluetoothDevice::class.java)
                disconnect.isAccessible = true
                disconnect.invoke(bluetoothA2dp, bluetoothDevice)
            } catch (e: Exception) {
                LogUtil.e(TAG, "disconnect error:${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun connectA2dp(device: BluetoothDevice): Boolean {
        val connect = BluetoothA2dp::class.java.getMethod("connect", BluetoothDevice::class.java)
        connect.isAccessible = true
        return connect.invoke(bluetoothA2dp, device) as Boolean
    }

    interface ConnectListener {
        fun onStart()
        fun onConnected()
        fun onFail(error: String)
    }

    @OnClick(R.id.scan_btn)
    fun scan() {
        bluetoothListData.clear()
        bluetoothListAdapter.notifyDataSetChanged()
        val bondedDevices = BtUtil.bluetooth.bondedDevices
        LogUtil.d(TAG, "bondedDevices.size=${bondedDevices.size}")
        for (dev in bondedDevices) {
            if (dev !in bluetoothListData && dev.name != null) {
                bluetoothListData.add(dev)
                bluetoothListAdapter.notifyItemInserted(bluetoothListData.size)
            }
        }

        BtUtil.bluetooth.cancelDiscovery()
        BtUtil.bluetooth.startDiscovery()
    }

    fun registerBluetoothBroadcast() {
        if (bluetoothBroadcast == null) {
            bluetoothBroadcast = BluetoothBroadcast()
        }
        var intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(bluetoothBroadcast, intentFilter)
    }

    fun unregisterBluetoothBroadcast() {
        if (bluetoothBroadcast != null) {
            unregisterReceiver(bluetoothBroadcast)
            bluetoothBroadcast = null
        }
    }

    inner class BluetoothBroadcast : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            LogUtil.d(TAG, "action=${intent?.action}")
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (device !in bluetoothListData && device.name != null) {
                        bluetoothListData.add(device)
                        bluetoothListAdapter.notifyItemInserted(bluetoothListData.size)
                    }
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0)
                    LogUtil.d(TAG, "state changed:$state && ${device.name}")
                }
                BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED)
                    LogUtil.d(TAG, "state=$state")
                    if (state == BluetoothA2dp.STATE_CONNECTING) {
                        itemStateTV?.text = "正在连接..."
                    } else if (state == BluetoothA2dp.STATE_CONNECTED) {
                        itemStateTV?.text = "连接成功"
                    }
                }
                BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_NOT_PLAYING)
                    LogUtil.d(TAG, "state=$state")
                    if (state == BluetoothA2dp.STATE_PLAYING) {
                        itemStateTV?.text = "正在播放"
                    } else if (state == BluetoothA2dp.STATE_NOT_PLAYING) {
                        itemStateTV?.text = "播放停止"
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterBluetoothBroadcast()
        BtUtil.bluetooth.closeProfileProxy(BluetoothProfile.A2DP, bluetoothA2dp)
        connectThread?.cancel()
    }
}