package com.winjay.practice.usb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.winjay.practice.R;
import com.winjay.practice.media.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 视频数据适配器
 *
 * @author Winjay
 * @date 2020/12/22
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {
    private static final String TAG = VideoListAdapter.class.getSimpleName();
    private Context mContext;
    private List<VideoBean> mListData;

    public OnItemClickListener itemClickListener;

    private RequestOptions mGlideOptions;

    public VideoListAdapter(Context context) {
        mContext = context;
        mListData = new ArrayList<>();
        mGlideOptions = new RequestOptions()
                .placeholder(R.mipmap.icon)
                .centerCrop();
    }

    public void setData(List<VideoBean> listData) {
        mListData = listData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mListData.get(position).getPath())
                .apply(mGlideOptions)
                .into(holder.video_iv);
        holder.video_tv.setText(mListData.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.video_iv)
        ImageView video_iv;

        @BindView(R.id.video_tv)
        TextView video_tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(mListData.get(getAdapterPosition()));
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(VideoBean videoBean);
    }
}
