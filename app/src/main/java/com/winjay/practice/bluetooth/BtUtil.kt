package com.winjay.practice.bluetooth

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

    val UUID_SERVICE = UUID.fromString("10000000-0000-0000-0000-000000000000")

    val UUID_READ_NOTIFY = UUID.fromString("11000000-0000-0000-0000-000000000000")

    val UUID_WRITE = UUID.fromString("12000000-0000-0000-0000-000000000000")

    val UUID_DESCRIBE = UUID.fromString("12000000-0000-0000-0000-000000000000")

    val bluetooth = BluetoothAdapter.getDefaultAdapter()
}