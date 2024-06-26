package com.winjay.practice.bluetooth.bt

import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.winjay.practice.R
import com.winjay.practice.databinding.BleBluetoothListItemBinding
import com.winjay.practice.databinding.BluetoothListBinding

/**
 * 蓝牙列表
 *
 * @author Winjay
 * @date 2021-04-30
 */
class BluetoothListAdapter(listData: MutableList<BluetoothDevice>) :
    RecyclerView.Adapter<BluetoothListAdapter.ViewHolder>() {
    var itemClickListener: OnItemClickListener? = null
    var listData: MutableList<BluetoothDevice>? = null

    init {
        this.listData = listData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BluetoothListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.blueItemNameTv.setText(listData?.get(position)?.name)
        holder.binding.blueItemAddrTv.setText(listData?.get(position)?.address)
        if (listData?.get(position)?.bondState == BluetoothDevice.BOND_BONDED) {
            holder.binding.blueItemStatusTv.text = "(已配对)"
            holder.binding.blueItemStatusTv.setTextColor(Color.parseColor("#ff009688"))
        } else {
            holder.binding.blueItemStatusTv.text = "(未配对)"
            holder.binding.blueItemStatusTv.setTextColor(Color.parseColor("#ffFF5722"))
        }
    }

    override fun getItemCount(): Int {
        return listData?.size!!
    }

    inner class ViewHolder(binding: BluetoothListBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        var binding: BluetoothListBinding

        init {
            super.itemView
            this.binding = binding
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            this@BluetoothListAdapter.itemClickListener?.onItemClick(v!!)
        }
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View)
    }
}