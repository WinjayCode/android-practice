package com.winjay.practice.bluetooth.bt

import android.bluetooth.BluetoothAdapter
import java.util.*

/**
 *
 *
 * @author Winjay
 * @date 2021-05-04
 */
object BtUtil {
    val BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    val bluetooth = BluetoothAdapter.getDefaultAdapter()
}