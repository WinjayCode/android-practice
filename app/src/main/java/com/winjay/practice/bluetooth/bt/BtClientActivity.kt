package com.winjay.practice.bluetooth.bt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.OnClick
import com.winjay.practice.R
import com.winjay.practice.bluetooth.BtUtil
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.databinding.BtClientActivityBinding
import com.winjay.practice.utils.LogUtil

/**
 * 传统蓝牙客户端(流模式)
 *
 * @author Winjay
 * @date 2021-04-29
 */
class BtClientActivity : BaseActivity() {
    private val TAG = javaClass.simpleName

    private lateinit var bluetoothListAdapter: BluetoothListAdapter
    private var bluetoothListData: MutableList<BluetoothDevice> = mutableListOf()
    var bluetoothBroadcast: BluetoothBroadcast? = null
    private lateinit var handleSocket: HandleSocket
    private var connectThread: ConnectThread? = null
    private var itemStateTV: TextView? = null
    private val stringBuilder = StringBuilder()

    private lateinit var binding: BtClientActivityBinding

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun viewBinding(): View {
        binding = BtClientActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothListAdapter = BluetoothListAdapter(bluetoothListData)
        initRecyclerView()
        registerBluetoothBroadcast()

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

    private fun initRecyclerView() {
        binding.btListRv.layoutManager = LinearLayoutManager(this)
        binding.btListRv.adapter = bluetoothListAdapter
        bluetoothListAdapter.setOnItemClickListener(object :
            BluetoothListAdapter.OnItemClickListener {
            override fun onItemClick(view: View) {
                itemStateTV = view.findViewById(R.id.blue_item_status_tv)
                val position = binding.btListRv.getChildAdapterPosition(view)
                LogUtil.d(TAG, "position=$position")
                toast("开始连接...")
                connectThread =
                    ConnectThread(bluetoothListData[position], readListener, writeListener)
                connectThread?.start()
            }
        })
    }

    fun registerBluetoothBroadcast() {
        if (bluetoothBroadcast == null) {
            bluetoothBroadcast = BluetoothBroadcast()
        }
        var intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        // 检测模式改变
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
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
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (device !in bluetoothListData && device.name != null) {
                        bluetoothListData.add(device)
                        bluetoothListAdapter.notifyItemInserted(bluetoothListData.size)
                    }
                }
                // 检测模式改变
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {

                }
            }
        }
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

    @OnClick(R.id.send_msg_btn)
    fun sendMsg() {
        if (this::handleSocket.isInitialized) {
            handleSocket.sendMsg(binding.sendEdit.text.toString())
            binding.sendEdit.setText("")
        } else {
            binding.tvLog.text = stringBuilder.run {
                append("没有连接蓝牙设备...").append("\n")
                toString()
            }
            binding.sendEdit.setText("")
        }
    }

    val readListener = object : HandleSocket.BluetoothListener {
        override fun onStart() {
            runOnUiThread {
                itemStateTV?.text = "正在连接..."
                itemStateTV?.setTextColor(Color.parseColor("#ff009688"))
            }
        }

        override fun onReceiveData(socket: BluetoothSocket?, msg: String) {
            runOnUiThread {
                binding.tvLog.text = stringBuilder.run {
                    append(socket?.remoteDevice?.name + "：" + msg).append("\n")
                    toString()
                }
            }
        }

        override fun onConnected(msg: String) {
            runOnUiThread {
                itemStateTV?.text = "已连接"
                itemStateTV?.setTextColor(Color.parseColor("#ff009688"))
            }
        }

        override fun onFail(error: String) {
            runOnUiThread {
                binding.tvLog.text = stringBuilder.run {
                    append(error).append("\n")
                    toString()
                }
                itemStateTV?.text = "未连接"
                itemStateTV?.setTextColor(Color.parseColor("#ffFF5722"))
            }
        }
    }

    val writeListener = object : HandleSocket.BaseBluetoothListener {
        override fun onSendMsg(socket: BluetoothSocket?, msg: String) {
            runOnUiThread {
                binding.tvLog.text = stringBuilder.run {
                    append("我：$msg").append("\n")
                    toString()
                }
            }
        }

        override fun onFail(error: String) {
            runOnUiThread {
                binding.tvLog.text = stringBuilder.run {
                    append("发送失败：$error").append("\n")
                    toString()
                }
            }
        }
    }

    /**
     * 连接类
     */
    inner class ConnectThread(
        val device: BluetoothDevice,
        val readListener: HandleSocket.BluetoothListener?,
        val writeListener: HandleSocket.BaseBluetoothListener?
    ) : Thread() {

        private val socket: BluetoothSocket? by lazy {
            readListener?.onStart()
            device.createRfcommSocketToServiceRecord(BtUtil.BLUETOOTH_UUID)
        }

        override fun run() {
            super.run()
            BtUtil.bluetooth.cancelDiscovery()
            try {
                socket.run {
                    // 阻塞等待
                    this?.connect()
                    // 连接成功，拿到服务端设备
                    this?.remoteDevice?.let {
                        LogUtil.e(TAG, "connect success!")
                        readListener?.onConnected(it.name)
                    }
                    // 处理读写事件
                    handleSocket = HandleSocket(this)
                    handleSocket.start(readListener, writeListener)
                }
            } catch (e: Exception) {
                LogUtil.e(TAG, "connect fail!")
                readListener?.onFail(e.message.toString())
            }
        }

        fun cancel() {
            socket?.close()
            if (::handleSocket.isInitialized) {
                handleSocket.cancel()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterBluetoothBroadcast()
        connectThread?.cancel()
    }
}