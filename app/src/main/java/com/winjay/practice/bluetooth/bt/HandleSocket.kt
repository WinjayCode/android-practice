package com.winjay.practice.bluetooth.bt

import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.*
import java.io.DataInputStream
import java.io.OutputStream
import java.lang.Exception

/**
 * 处理读写事件
 *
 * @author Winjay
 * @date 2021-05-04
 */
class HandleSocket(private val socket: BluetoothSocket?) {
    private val TAG = javaClass.simpleName
    private lateinit var readThread: ReadThread
    private lateinit var writeThread: WriteThread

    fun start(readListener: BluetoothListener?, writeListener: BaseBluetoothListener?) {
        readThread = ReadThread(socket, readListener)
        readThread.start()
        writeThread = WriteThread(socket, writeListener)
    }

    /**
     * 读数据
     */
    private class ReadThread(val socket: BluetoothSocket?, val bluetoothListener: BluetoothListener?) : Thread() {
        private val inputStream: DataInputStream? = DataInputStream(socket?.inputStream)
        private var isDone = false
        private val byteBuffer: ByteArray = ByteArray(1024)

        override fun run() {
            super.run()
            var size: Int? = null
            while (!isDone) {
                try {
                    size = inputStream?.read(byteBuffer)
                } catch (e: Exception) {
                    isDone = false
                    e.message?.let {
                        bluetoothListener?.onFail(it)
                        return
                    }
                }

                if (size != null && size > 0) {
                    bluetoothListener?.onReceiveData(socket, String(byteBuffer, 0, size))
                } else {
                    bluetoothListener?.onFail("断开连接")
                    isDone = false
                }
            }
        }

        fun cancel() {
            isDone = false
            socket?.close()
            inputStream?.close()
        }
    }

    private inner class WriteThread(val socket: BluetoothSocket?, val listener: BaseBluetoothListener?) {
        private var isDone = false
        private val outputStream: OutputStream? = socket?.outputStream

        fun sendMsg(msg: String) {
            if (isDone) {
                return
            }
            scope.launch(Dispatchers.Main) {
                val result = withContext(Dispatchers.IO) {
                    sendScope(msg)
                }
                when (result) {
                    null -> listener?.onSendMsg(socket, msg)
                    else -> listener?.onFail(result)
                }
            }
        }

        private fun sendScope(msg: String): String? {
            return try {
                outputStream?.write(msg.toByteArray())
                outputStream?.flush()
                null
            } catch (e: Exception) {
                e.message
            }
        }

        fun cancel() {
            isDone = true
            socket?.close()
            outputStream?.close()
        }
    }

    fun sendMsg(msg: String) {
        writeThread.sendMsg(msg)
    }

    fun cancel() {
        readThread?.cancel()
        writeThread?.cancel()
        socket?.close()
        job.cancel()
    }

    interface BaseBluetoothListener {
        fun onSendMsg(socket: BluetoothSocket?, msg: String){}
        fun onFail(error: String)
    }

    interface BluetoothListener : BaseBluetoothListener {
        fun onStart()
        fun onReceiveData(socket: BluetoothSocket?, msg: String)
        fun onConnected(msg: String){}
    }

    private val job = Job()
    private val scope = CoroutineScope(job)
}