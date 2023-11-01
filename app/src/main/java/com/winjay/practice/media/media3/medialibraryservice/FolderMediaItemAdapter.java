package com.winjay.practice.media.media3.medialibraryservice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FolderMediaItemAdapter extends RecyclerView.Adapter<FolderMediaItemAdapter.ViewHolder> {
    private static final String TAG = FolderMediaItemAdapter.class.getSimpleName();
    private List<MediaItem> mData;

    public OnItemClickListener itemClickListener;

    public FolderMediaItemAdapter(List<MediaItem> data) {
        mData = data;
//        LogUtil.d(TAG, "mData=" + JsonUtil.getInstance().toJson(mData));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_adapter_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item_tv.setText(mData.get(position).mediaMetadata.title);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public MediaItem getItem(int position) {
        return mData.get(position);
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
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getLayoutPosition());
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
