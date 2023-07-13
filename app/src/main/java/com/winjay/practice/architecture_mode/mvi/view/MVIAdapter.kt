package com.winjay.practice.architecture_mode.mvi.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.winjay.practice.architecture_mode.mvi.bean.Vertical
import com.winjay.practice.databinding.MviItemBinding

/**
 * 壁纸适配器
 */
class MVIAdapter(private val verticals: ArrayList<Vertical>) :
    RecyclerView.Adapter<MVIAdapter.ViewHolder>() {

    fun addData(data: List<Vertical>) {
        verticals.addAll(data)
    }

    class ViewHolder(itemWallPaperRvBinding: MviItemBinding) :
        RecyclerView.ViewHolder(itemWallPaperRvBinding.root) {

        var binding: MviItemBinding

        init {
            binding = itemWallPaperRvBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            MviItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = verticals.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //加载图片
        verticals[position].preview.let {
            Glide.with(holder.itemView.context).load(it).into(holder.binding.ivWallPaper)
        }
    }
}