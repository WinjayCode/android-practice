package com.winjay.practice.bluetooth.ble

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.winjay.practice.databinding.BleBluetoothListItemBinding

/**
 * 低功耗蓝牙扫描适配器
 *
 * @author Winjay
 * @date 2021-06-07
 */
class BleListAdapter(listData: MutableList<BleClientActivity.BleData>) :
    RecyclerView.Adapter<BleListAdapter.ViewHolder>() {
    var itemClickListener: OnItemClickListener? = null
    var listData: MutableList<BleClientActivity.BleData>? = null

    init {
        this.listData = listData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleListAdapter.ViewHolder {
        val binding =
            BleBluetoothListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BleListAdapter.ViewHolder, position: Int) {
        holder.binding.bleNameTv.text = "名称：" + listData?.get(position)?.dev?.name
        holder.binding.bleMacTv.text = "地址：" + listData?.get(position)?.dev?.address
        holder.binding.bleDeviceTv.text = listData?.get(position)?.scanRecord
    }

    override fun getItemCount(): Int {
        return listData?.size!!
    }

    inner class ViewHolder(binding: BleBluetoothListItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        var binding: BleBluetoothListItemBinding

        init {
            super.itemView
            this.binding = binding
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            this@BleListAdapter.itemClickListener?.onItemClick(v!!)
        }
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View)
    }
}