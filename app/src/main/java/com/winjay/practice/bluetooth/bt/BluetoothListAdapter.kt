package com.winjay.practice.bluetooth.bt

import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.winjay.practice.R
import kotlinx.android.synthetic.main.bluetooth_list.view.*

/**
 * 蓝牙列表
 *
 * @author Winjay
 * @date 2021-04-30
 */
class BluetoothListAdapter(listData: MutableList<BluetoothDevice>) : RecyclerView.Adapter<BluetoothListAdapter.ViewHolder>() {
    var itemClickListener: OnItemClickListener? = null
    var listData: MutableList<BluetoothDevice>? = null

    init {
        this.listData = listData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.bluetooth_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.blue_item_name_tv.setText(listData?.get(position)?.name)
        holder.itemView.blue_item_addr_tv.setText(listData?.get(position)?.address)
        if (listData?.get(position)?.bondState == BluetoothDevice.BOND_BONDED) {
            holder.itemView.blue_item_status_tv.setText("(已配对)")
            holder.itemView.blue_item_status_tv.setTextColor(Color.parseColor("#ff009688"))
        } else {
            holder.itemView.blue_item_status_tv.setText("(未配对)")
            holder.itemView.blue_item_status_tv.setTextColor(Color.parseColor("#ffFF5722"))
        }
    }

    override fun getItemCount(): Int {
        return listData?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            super.itemView
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