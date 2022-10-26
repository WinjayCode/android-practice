package com.winjay.practice.media.media_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.winjay.practice.R;
import com.winjay.practice.media.MediaCollectionHelper.ImageBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 图片数据适配器
 *
 * @author Winjay
 * @date 2020/12/22
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private static final String TAG = ImageListAdapter.class.getSimpleName();
    private Context mContext;
    private List<ImageBean> mListData;

    public OnItemClickListener itemClickListener;

    private RequestOptions mGlideOptions;

    public ImageListAdapter(Context context) {
        mContext = context;
        mListData = new ArrayList<>();
        mGlideOptions = new RequestOptions()
                .placeholder(R.mipmap.icon)
                .centerCrop();
    }

    public void setData(List<ImageBean> listData) {
        mListData = listData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mListData.get(position).getUri())
                .apply(mGlideOptions)
                .into(holder.image_iv);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_iv)
        ImageView image_iv;

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
        void onItemClick(ImageBean imageBean);
    }
}
