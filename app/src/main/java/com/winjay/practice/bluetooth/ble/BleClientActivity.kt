package com.winjay.practice.bluetooth.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.winjay.practice.bluetooth.BtUtil
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.databinding.BleClientActivityBinding
import com.winjay.practice.utils.LogUtil
import java.util.UUID

/**
 * 低功耗蓝牙客户端(中心设备)
 *
 * 可以扫描到多个外围设备，并从外围设备获取信息
 *
 * 与传统蓝牙不同，低功耗蓝主要为了降低设备功耗，支持更低功耗(如心率检测仪，健身设备)等设备进行通信。
 *
 * Characteristic是Gatt通信最小的逻辑单元，一个 characteristic 包含一个单一 value 变量 和 0-n个用来描述
 * characteristic变量的描述符 Descriptor。与 service 相似，每个 characteristic 用 16bit或者32bit的uuid作为标识，
 * 实际的通信中，也是通过 Characteristic 进行读写通信的。
 *
 * Descriptor它的定义就是描述 GattCharacteristic 值已定义的属性，比如指定可读的属性，可接受范围等，比如为写的 特征添加描述符
 *
 * @author Winjay
 * @date 2021-06-07
 */
class BleClientActivity : BaseActivity() {
    private val TAG = javaClass.simpleName

    data class BleData(val dev: BluetoothDevice, val scanRecord: String? = null)

    private var isScanning = false
    private val handler = Handler(Looper.getMainLooper())
    private var mScanResult: MutableList<BleData> = mutableListOf()
    private lateinit var bleListAdapter: BleListAdapter
    private var blueGatt: BluetoothGatt? = null
    private var sb: StringBuilder = StringBuilder()
    private var isConnected = false

    private lateinit var binding: BleClientActivityBinding

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun viewBinding(): View {
        binding = BleClientActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bleListAdapter = BleListAdapter(mScanResult)
        isSupportBLE()
        initRecyclerView()

        binding.scanBtn.setOnClickListener { scanDevice() }
        binding.readDataBtn.setOnClickListener { readData() }
        binding.writeDataBtn.setOnClickListener { writeData() }
    }

    private fun isSupportBLE() {
        // kotlin takeIf: 满足takeIf中的条件后，走let中的逻辑
        packageManager.takeIf { !it.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }
                ?.let {
                    toast("您的设备没有低功耗蓝牙驱动！")
                    finish()
                }
    }

    private fun initRecyclerView() {
        binding.scanResultRv.layoutManager = LinearLayoutManager(this)
        binding.scanResultRv.adapter = bleListAdapter
        bleListAdapter.setOnItemClickListener(object : BleListAdapter.OnItemClickListener {
            override fun onItemClick(view: View) {
                closeConnect()
                val position = binding.scanResultRv.getChildAdapterPosition(view)
                val bleData = mScanResult[position]
                blueGatt = bleData.dev.connectGatt(this@BleClientActivity, false, bluetoothGattCallback)
                showInfo("开始与 ${bleData.dev.name} 连接...")
            }
        })
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        // 与外围设备连接后会回调该方法
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            LogUtil.d(TAG)
            val device = gatt?.device
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                isConnected = true
                handler.postDelayed({
                    gatt?.discoverServices()
                }, 300)

                device?.let {
                    showInfo("与 ${it.name} 连接成功！")
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                isConnected = false
                showInfo("无法与 ${device?.name} 连接：$status")
                closeConnect()
            }
        }

        // 与外围设备服务连接成功后回调该方法
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            LogUtil.d(TAG)
            showInfo("已连接上GATT服务，可以通信！")
        }

        // 外围设备在收到readCharacteristic请求后，会通过sendResponse发送返回数据到该方法
        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
            LogUtil.d(TAG, "value=${characteristic?.value?.let { String(it) }}")
            characteristic?.let {
                val data = String(it.value)
                showInfo("CharacteristicRead 数据：$data")
            }
        }

        // 外围设备在收到writeCharacteristic请求后，会通过sendResponse发送返回数据到该方法
        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            LogUtil.d(TAG)
            characteristic?.let {
                val data = String(it.value)
                showInfo("CharacteristicWrite 数据：$data")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            LogUtil.d(TAG)
            characteristic?.let {
                val data = String(it.value)
                showInfo("CharacteristicChanged 数据：$data")
            }
        }

        override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorRead(gatt, descriptor, status)
            LogUtil.d(TAG)
            descriptor?.let {
                val data = String(it.value)
                showInfo("DescriptorRead 数据：$data")
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
            LogUtil.d(TAG)
            descriptor?.let {
                val data = String(it.value)
                showInfo("DescriptorWrite 数据：$data")
            }
        }
    }

    private fun showInfo(msg: String) {
        runOnUiThread {
            sb.apply {
                append(msg).append("\n")
                binding.infoTv.text = toString()
            }
        }
    }

    private fun closeConnect() {
        stopScan()
        blueGatt?.let {
            it.disconnect()
            it.close()
        }
    }

    fun scanDevice() {
        if (isScanning) {
            return
        }

        mScanResult.clear()
        bleListAdapter.notifyDataSetChanged()

        val builder = ScanSettings.Builder()
                /**
                 * 三种模式
                 * - SCAN_MODE_LOW_POWER : 低功耗模式，默认此模式，如果应用不在前台，则强制此模式
                 * - SCAN_MODE_BALANCED : 平衡模式，一定频率下返回结果
                 * - SCAN_MODE_LOW_LATENCY : 高功耗模式，建议应用在前台才使用此模式
                 */
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /**
             * 三种回调模式
             * - CALLBACK_TYPE_ALL_MATCHED : 寻找符合过滤条件的广播，如果没有，则返回全部广播
             * - CALLBACK_TYPE_FIRST_MATCH : 仅筛选匹配第一个广播包出发结果回调的
             * - CALLBACK_TYPE_MATCH_LOST : 这个看英文文档吧，不满足第一个条件的时候，不好解释???
             */
            builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        }

        // ?
        if (BtUtil.bluetooth.isOffloadedFilteringSupported) {
            builder.setReportDelay(0L)
        }

        isScanning = true
        // 扫描很耗电，所以不能一直扫描
        handler.postDelayed({
            LogUtil.d(TAG, "stopScan")
            BtUtil.bluetooth.bluetoothLeScanner?.stopScan(scanCallback)
            isScanning = false
        }, 3000)
        LogUtil.d(TAG, "startScan")
        BtUtil.bluetooth.bluetoothLeScanner?.startScan(null, builder.build(), scanCallback)
    }

    private fun stopScan() {
        BtUtil.bluetooth.bluetoothLeScanner?.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result ?: return

            result.device.name ?: return

            LogUtil.d(TAG, "scan device name=" + result.device.name)
            val bean = BleData(result.device, result.scanRecord.toString())
            if (bean !in mScanResult) {
                mScanResult.add(bean)
                bleListAdapter.notifyItemInserted(mScanResult.size)
            }
        }
    }

    fun readData() {
        val service = getGattService(BtUtil.UUID_SERVICE)
        if (service != null) {
            val characteristic = service.getCharacteristic(BtUtil.UUID_READ_NOTIFY)
            blueGatt?.readCharacteristic(characteristic)
        }
    }

    // 获取Gatt服务
    private fun getGattService(uuid: UUID): BluetoothGattService? {
        if (!isConnected) {
            toast("没有连接！")
            return null
        }
        val service = blueGatt?.getService(uuid)
        if (service == null) {
            toast("没有找到服务！")
        }
        return service
    }

    fun writeData() {
        val msg = binding.dataEt.text.toString()
        binding.dataEt.setText("")
        val service = getGattService(BtUtil.UUID_SERVICE)
        if (service != null) {
            val characteristic = service.getCharacteristic(BtUtil.UUID_WRITE)
            characteristic.value = msg.toByteArray()
            blueGatt?.writeCharacteristic(characteristic)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closeConnect()
    }
}