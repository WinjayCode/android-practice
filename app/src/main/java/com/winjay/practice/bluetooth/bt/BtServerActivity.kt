package com.winjay.practice.bluetooth.bt

import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.view.View
import butterknife.OnClick
import com.winjay.practice.R
import com.winjay.practice.bluetooth.BtUtil
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.databinding.A2dpActivityBinding
import com.winjay.practice.databinding.BtServerActivityBinding
import com.winjay.practice.utils.LogUtil

/**
 * 传统蓝牙服务端(流模式)
 *
 * @author Winjay
 * @date 2021-05-04
 */
class BtServerActivity : BaseActivity() {
    private val TAG = javaClass.simpleName
    private lateinit var acceptThread: AcceptThread
    private val stringBuffer = StringBuilder();
    private lateinit var handleSocket: HandleSocket

    private lateinit var binding: BtServerActivityBinding

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun viewBinding(): View {
        binding = BtServerActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        acceptThread = AcceptThread(readListener, writeListener)
        acceptThread.start()
    }

    val readListener = object : HandleSocket.BluetoothListener {
        override fun onStart() {
            runOnUiThread {
                binding.serverStatusTv.text = "服务器已就绪"
            }
        }

        override fun onReceiveData(socket: BluetoothSocket?, msg: String) {
            runOnUiThread {
                binding.logTv.text = stringBuffer.run {
                    append(socket?.remoteDevice?.name + "：" + msg).append("\n")
                    toString()
                }
            }
        }

        override fun onConnected(msg: String) {
            runOnUiThread {
                binding.serverStatusTv.text = "已连接上客户端：$msg"
            }
        }

        override fun onFail(error: String) {
            runOnUiThread {
                binding.serverStatusTv.text = error
            }
        }
    }

    val writeListener = object : HandleSocket.BaseBluetoothListener {
        override fun onSendMsg(socket: BluetoothSocket?, msg: String) {
            runOnUiThread {
                binding.logTv.text = stringBuffer.run {
                    append("我:$msg").append("\n")
                    toString()
                }
            }
        }

        override fun onFail(error: String) {
            runOnUiThread {
                binding.logTv.text = stringBuffer.run {
                    append("发送失败:$error").append("\n")
                    toString()
                }
            }
        }
    }

    private inner class AcceptThread(val readListener: HandleSocket.BluetoothListener?, val writeListener: HandleSocket.BaseBluetoothListener?) : Thread() {

        private val serverSocket: BluetoothServerSocket? by lazy {
            // 非明文匹配，不安全
            readListener?.onStart()
            BtUtil.bluetooth.listenUsingRfcommWithServiceRecord(TAG, BtUtil.BLUETOOTH_UUID)
        }

        override fun run() {
            super.run()
            var shouldLoop = true
            while (shouldLoop) {
                var socket: BluetoothSocket? =
                        try {
                            serverSocket?.accept()
                        } catch (e: Exception) {
                            LogUtil.e(TAG, "socket accept fail: ${e.message}")
                            readListener?.onFail(e.message.toString())
                            shouldLoop = false
                            null
                        }
                socket?.also {
                    // 拿到接入设备的名字
                    readListener?.onConnected(socket.remoteDevice.name)
                    // 处理读写事件
                    handleSocket = HandleSocket(socket)
                    handleSocket.start(readListener, writeListener)
                    // 关闭服务端，只连接一个
                    serverSocket?.close()
                    shouldLoop = false
                }
            }
        }

        fun cancel() {
            serverSocket?.close()
            if (::handleSocket.isInitialized) {
                handleSocket.cancel()
            }
        }
    }

    @OnClick(R.id.send_msg_btn)
    fun sendMsg() {
        if (this::handleSocket.isInitialized) {
            handleSocket.sendMsg(binding.sendEdit.text.toString())
            binding.sendEdit.setText("")
        } else {
            binding.logTv.text = stringBuffer.run {
                append("没有连接蓝牙设备...").append("\n")
                toString()
            }
            binding.sendEdit.setText("")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::acceptThread.isInitialized) {
            acceptThread.cancel()
        }
    }
}