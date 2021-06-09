package com.winjay.practice.bluetooth.ble

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.Bundle
import android.os.ParcelUuid
import com.winjay.practice.R
import com.winjay.practice.bluetooth.BluetoothUuid
import com.winjay.practice.bluetooth.BtUtil
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.utils.LogUtil
import kotlinx.android.synthetic.main.ble_client_activity.*

/**
 * 低功耗蓝牙服务端（外围设备）
 *
 * 会不断的发出广播，让中心设备知道，一旦连接上中心设备，就会停止发出广播
 *
 * @author Winjay
 * @date 2021-06-07
 */
class BleServerActivity : BaseActivity() {
    private val TAG = javaClass.simpleName

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var sb: StringBuilder = StringBuilder()
    private var bluetoothGattServer: BluetoothGattServer? = null

    override fun getLayoutId(): Int {
        return R.layout.ble_server_activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBle()
    }

    private fun initBle() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothAdapter?.name = "BLE"

        // 广播设置
        val advertiseSettings = AdvertiseSettings.Builder()
                /**
                 * SCAN_MODE_LOW_POWER : 低功耗模式，默认此模式，如果应用不在前台，则强制此模式
                 * SCAN_MODE_BALANCED ： 平衡模式，一定频率下返回结果
                 * SCAN_MODE_LOW_LATENCY 高功耗模式，建议应用在前台才使用此模式
                 */
                // 低延时，高功率，不使用后台
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                /**
                 * 使用高TX功率级别进行广播：AdvertiseSettings#ADVERTISE_TX_POWER_HIGH
                 * 使用低TX功率级别进行广播：AdvertiseSettings#ADVERTISE_TX_POWER_LOW
                 * 使用中等TX功率级别进行广播：AdvertiseSettings#ADVERTISE_TX_POWER_MEDIUM
                 * 使用最低传输（TX）功率级别进行广播：AdvertiseSettings#ADVERTISE_TX_POWER_ULTRA_LOW
                 */
                // 高的发送功率
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                // 广播时限。最多180000毫秒。值为0将禁用时间限制。（不设置则为无限广播时长）
                .setTimeout(0)
                .build()

        // 设置广播包，这个是必须要设置的
        val advertiseData = AdvertiseData.Builder()
                // 显示名字
                .setIncludeDeviceName(true)
                // 设置功率
                .setIncludeTxPowerLevel(true)
                // 设置 UUID 服务的 uuid
                .addServiceUuid(ParcelUuid(BtUtil.UUID_SERVICE))
                .build()

        // 测试 31bit
        val byteData = byteArrayOf(-65, 2, 3, 6, 4, 23, 23, 9, 9,
                9, 1, 2, 3, 6, 4, 23, 23, 9, 9, 8, 23, 23, 23)

        // 扫描广播数据（可不写，客户端扫描才发送）
        val scanResponse = AdvertiseData.Builder()
                //设置厂商数据
                .addManufacturerData(0x19, byteData)
                .build()

        /**
         * GATT 使用了 ATT 协议，ATT 把 service 和 characteristic 对应的数据保存在一个查询表中，
         * 依次查找每一项的索引
         * BLE 设备通过 Service 和 Characteristic 进行通信
         * 外设只能被一个中心设备连接，一旦连接，就会停止广播，断开又会重新发送
         * 但中心设备同时可以和多个外设连接
         * 他们之间需要双向通信的话，唯一的方式就是建立 GATT 连接
         * 外设作为 GATT(server)，它维持了 ATT 的查找表以及service 和 charateristic 的定义
         */

        // 判断广播数据是否超过31个字节
        val advertiseDataTotalSize = totalBytes(advertiseData, advertiseSettings.isConnectable)
        LogUtil.d(TAG, "advertiseDataTotalSize=$advertiseDataTotalSize")
        val scanResponseTotalSize = totalBytes(scanResponse, false)
        LogUtil.d(TAG, "scanResponseTotalSize=$scanResponseTotalSize")


        // 开启广播,这个外设就开始发送广播了
        val bluetoothLeAdvertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        bluetoothLeAdvertiser?.startAdvertising(
                advertiseSettings,
                advertiseData,
                scanResponse,
                advertiseCallback
        )

        // 添加读+通知的 GattCharacteristic
        val readCharacteristic = BluetoothGattCharacteristic(
                BtUtil.UUID_READ_NOTIFY,
                BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ
        )

        // 添加写的 GattCharacteristic
        val writeCharacteristic = BluetoothGattCharacteristic(
                BtUtil.UUID_WRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE
        )

        // 添加 Descriptor 描述符
        val bluetoothGattDescriptor = BluetoothGattDescriptor(
                BtUtil.UUID_DESCRIBE,
                BluetoothGattDescriptor.PERMISSION_WRITE
        )
        // 为特征值添加描述
        writeCharacteristic.addDescriptor(bluetoothGattDescriptor)

        /**
         * 添加 Gatt service 用来通信
         */

        // 开启广播service，这样才能通信，包含一个或多个 characteristic ，每个service 都有一个 uuid
        val bluetoothGattService = BluetoothGattService(
                BtUtil.UUID_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY
        )
        bluetoothGattService.addCharacteristic(readCharacteristic)
        bluetoothGattService.addCharacteristic(writeCharacteristic)

        // 打开 GATT 服务，方便客户端连接
        bluetoothGattServer = bluetoothManager.openGattServer(this, bluetoothGattCallback)
        bluetoothGattServer?.addService(bluetoothGattService)
    }

    private val bluetoothGattCallback = object : BluetoothGattServerCallback() {
        // 中心设备connectGatt外围设备的时候回调该方法
        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            device ?: return
            LogUtil.d(TAG, "status=$status, newState=$newState")
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                showInfo("连接到中心设备：${device.name}")
            } else {
                showInfo("与：${device.name} 断开连接失败！")
            }
        }

        // 中心设备BluetoothGatt.readCharacteristic的时候回调该方法
        override fun onCharacteristicReadRequest(device: BluetoothDevice?, requestId: Int, offset: Int,
                                                 characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
            LogUtil.d(TAG, "requestId=$requestId, offset=$offset")

            val data = "this is a test from ble server"
            bluetoothGattServer?.sendResponse(
                    device, requestId, BluetoothGatt.GATT_SUCCESS, offset, data.toByteArray()
            )
            showInfo("中心设备读取 [characteristic ${characteristic?.uuid}] $data")
        }

        // 中心设备BluetoothGatt.writeCharacteristic的时候回调该方法
        override fun onCharacteristicWriteRequest(device: BluetoothDevice?, requestId: Int,
                                                  characteristic: BluetoothGattCharacteristic?,
                                                  preparedWrite: Boolean, responseNeeded: Boolean,
                                                  offset: Int, value: ByteArray?) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
            LogUtil.d(TAG, "requestId=$requestId, preparedWrite=$preparedWrite, responseNeeded=$responseNeeded, offset=$offset")
            bluetoothGattServer?.sendResponse(
                    device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value
            )
            value?.let {
                showInfo("中心设备写入 [characteristic ${characteristic?.uuid}] ${String(it)}")
            }
        }

        override fun onDescriptorReadRequest(device: BluetoothDevice?, requestId: Int, offset: Int,
                                             descriptor: BluetoothGattDescriptor?) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor)
            LogUtil.d(TAG, "requestId=$requestId, offset=$offset")
            val data = "this is a test"
            bluetoothGattServer?.sendResponse(
                    device, requestId, BluetoothGatt.GATT_SUCCESS, offset, data.toByteArray()
            )
            showInfo("中心设备读取 [descriptor ${descriptor?.uuid}] $data")
        }

        override fun onDescriptorWriteRequest(device: BluetoothDevice?, requestId: Int,
                                              descriptor: BluetoothGattDescriptor?, preparedWrite: Boolean,
                                              responseNeeded: Boolean, offset: Int, value: ByteArray?) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value)
            LogUtil.d(TAG, "requestId=$requestId, preparedWrite=$preparedWrite, responseNeeded=$responseNeeded, offset=$offset")
            value?.let {
                showInfo("中心设备写入 [descriptor ${descriptor?.uuid}] ${String(it)}")
            }
        }

        override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
            super.onExecuteWrite(device, requestId, execute)
            LogUtil.d(TAG, "requestId=$requestId, execute=$execute")
        }

        override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
            super.onNotificationSent(device, status)
            LogUtil.d(TAG, "status=$status")
        }

        override fun onMtuChanged(device: BluetoothDevice?, mtu: Int) {
            super.onMtuChanged(device, mtu)
            LogUtil.d(TAG, "mtu=$mtu")
        }
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            LogUtil.d(TAG)
            showInfo("服务准备就绪，请搜索广播！")
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            LogUtil.d(TAG, "errorCode=$errorCode")
            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                showInfo("广播数据超过31个字节了！")
            } else {
                showInfo("服务启动失败：$errorCode")
            }
        }
    }

    private fun showInfo(msg: String) {
        runOnUiThread {
            sb.apply {
                append(msg).append("\n")
                info_tv.text = toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothAdapter?.bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback)
        bluetoothGattServer?.close()
    }

    /**
     * 16bitUuid: settings.isConnectable():3/0 + 2 + 2 + includeTxPowerLevel:2+1 + includeDeviceName:2+name.length
     * 32bitUuid: settings.isConnectable():3/0 + 2 + 4 + includeTxPowerLevel:2+1 + includeDeviceName:2+name.length
     * 128bitUuid: settings.isConnectable():3/0 + 2 + 16 + includeTxPowerLevel:2+1 + includeDeviceName:2+name.length
     */
    private fun totalBytes(data: AdvertiseData?, isFlagsIncluded: Boolean): Int {
        if (data == null) return 0
        var sb = StringBuilder()
        // Flags field is omitted if the advertising is not connectable.
        var size = if (isFlagsIncluded) 3 else 0
        sb.append("1size=$size").append("\n")
        if (data.serviceUuids != null) {
            var num16BitUuids = 0
            var num32BitUuids = 0
            var num128BitUuids = 0
            for (uuid in data.serviceUuids) {
                if (BluetoothUuid.is16BitUuid(uuid)) {
                    ++num16BitUuids
                } else if (BluetoothUuid.is32BitUuid(uuid)) {
                    ++num32BitUuids
                } else {
                    ++num128BitUuids
                }
            }
            // 16 bit service uuids are grouped into one field when doing advertising.
            if (num16BitUuids != 0) {
                size += 2 + num16BitUuids * 2
                sb.append("2size=$size").append("\n")
            }
            // 32 bit service uuids are grouped into one field when doing advertising.
            if (num32BitUuids != 0) {
                size += 2 + num32BitUuids * 4
                sb.append("3size=$size").append("\n")
            }
            // 128 bit service uuids are grouped into one field when doing advertising.
            if (num128BitUuids != 0) {
                size += (2 + num128BitUuids * 16)
                sb.append("4size=$size").append("\n")
            }
        }
        for (uuid in data.serviceData.keys) {
            val uuidLen: Int = BluetoothUuid.uuidToBytes(uuid).size
            size += (2 + uuidLen + byteLength(data.serviceData[uuid]))
            sb.append("5size=$size").append("\n")
        }
        for (i in 0 until data.manufacturerSpecificData.size()) {
            size += (2 + 2 + byteLength(data.manufacturerSpecificData.valueAt(i)))
            sb.append("6size=$size").append("\n")
        }
        if (data.includeTxPowerLevel) {
            size += 2 + 1 // tx power level value is one byte.
            sb.append("7size=$size").append("\n")
        }
        if (data.includeDeviceName && bluetoothAdapter?.name != null) {
            size += 2 + bluetoothAdapter?.name?.length!!
            sb.append("8size=$size").append("\n")
        }
        sb.append("size=$size").append("\n")
        LogUtil.d(TAG, sb.toString())
        return size
    }

    private fun byteLength(array: ByteArray?): Int {
        return array?.size ?: 0
    }
}