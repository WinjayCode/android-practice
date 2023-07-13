package com.winjay.practice.architecture_mode.mvvm.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;
import com.winjay.practice.architecture_mode.mvvm.model.MVVMDataBean;
import com.winjay.practice.utils.JsonUtil;
import com.winjay.practice.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MVVMAdapter extends RecyclerView.Adapter<MVVMAdapter.ViewHolder> {
    private static final String TAG = MVVMAdapter.class.getSimpleName();
    private List<MVVMDataBean> mData;

    public MVVMAdapter(List<MVVMDataBean> data) {
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
        holder.item_tv.setText(mData.get(position).getName() + ", " + mData.get(position).getAge());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_tv)
        TextView item_tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
