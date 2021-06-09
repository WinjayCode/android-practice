package com.winjay.practice.bluetooth.bt

import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import butterknife.OnClick
import com.winjay.practice.R
import com.winjay.practice.bluetooth.BtUtil
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.utils.LogUtil
import kotlinx.android.synthetic.main.bt_server_activity.*

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

    override fun getLayoutId(): Int {
        return R.layout.bt_server_activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        acceptThread = AcceptThread(readListener, writeListener)
        acceptThread.start()
    }

    val readListener = object : HandleSocket.BluetoothListener {
        override fun onStart() {
            runOnUiThread {
                server_status_tv.text = "服务器已就绪"
            }
        }

        override fun onReceiveData(socket: BluetoothSocket?, msg: String) {
            runOnUiThread {
                log_tv.text = stringBuffer.run {
                    append(socket?.remoteDevice?.name + "：" + msg).append("\n")
                    toString()
                }
            }
        }

        override fun onConnected(msg: String) {
            runOnUiThread {
                server_status_tv.text = "已连接上客户端：$msg"
            }
        }

        override fun onFail(error: String) {
            runOnUiThread {
                server_status_tv.text = error
            }
        }
    }

    val writeListener = object : HandleSocket.BaseBluetoothListener {
        override fun onSendMsg(socket: BluetoothSocket?, msg: String) {
            runOnUiThread {
                log_tv.text = stringBuffer.run {
                    append("我:$msg").append("\n")
                    toString()
                }
            }
        }

        override fun onFail(error: String) {
            runOnUiThread {
                log_tv.text = stringBuffer.run {
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
            handleSocket.sendMsg(send_edit.text.toString())
            send_edit.setText("")
        } else {
            log_tv.text = stringBuffer.run {
                append("没有连接蓝牙设备...").append("\n")
                toString()
            }
            send_edit.setText("")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::acceptThread.isInitialized) {
            acceptThread.cancel()
        }
    }
}