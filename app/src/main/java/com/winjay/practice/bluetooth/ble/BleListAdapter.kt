package com.winjay.practice.bluetooth.ble

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.winjay.practice.R
import kotlinx.android.synthetic.main.ble_bluetooth_list_item.view.*

/**
 * 低功耗蓝牙扫描适配器
 *
 * @author Winjay
 * @date 2021-06-07
 */
class BleListAdapter(listData: MutableList<BleClientActivity.BleData>) : RecyclerView.Adapter<BleListAdapter.ViewHolder>() {
    var itemClickListener: OnItemClickListener? = null
    var listData: MutableList<BleClientActivity.BleData>? = null

    init {
        this.listData = listData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleListAdapter.ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.ble_bluetooth_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: BleListAdapter.ViewHolder, position: Int) {
        holder.itemView.ble_name_tv.text = "名称：" + listData?.get(position)?.dev?.name
        holder.itemView.ble_mac_tv.text = "地址：" + listData?.get(position)?.dev?.address
        holder.itemView.ble_device_tv.text = listData?.get(position)?.scanRecord
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