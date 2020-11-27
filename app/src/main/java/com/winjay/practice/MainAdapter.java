package com.winjay.practice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.utils.JsonUtil;
import com.winjay.practice.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 数据适配器
 *
 * @author Winjay
 * @date 2020/9/23
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private static final String TAG = MainAdapter.class.getSimpleName();
    private List<String> mData;

    public OnItemClickListener itemClickListener;

    public MainAdapter(List<String> data) {
        mData = data;
        LogUtil.d(TAG, "mData=" + JsonUtil.getInstance().toJson(mData));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_adapter_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item_tv.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_tv)
        TextView item_tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            item_tv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String key = item_tv.getText().toString();
            LogUtil.d(TAG, "onClick():key=" + key);
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, key);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String key);
    }
}
