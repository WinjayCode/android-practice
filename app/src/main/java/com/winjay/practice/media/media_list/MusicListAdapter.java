package com.winjay.practice.media.media_list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Size;
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
import com.winjay.practice.media.bean.AudioBean;
import com.winjay.practice.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 音频数据适配器
 *
 * @author Winjay
 * @date 2020/12/22
 */
public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {
    private static final String TAG = "MusicListAdapter";
    private Context mContext;
    private List<AudioBean> mListData;

    public OnItemClickListener itemClickListener;

    private RequestOptions mGlideOptions;

    private Bitmap defaultBitmap;

    public MusicListAdapter(Context context) {
        mContext = context;
        mListData = new ArrayList<>();
        mGlideOptions = new RequestOptions()
                .placeholder(R.mipmap.icon)
                .centerCrop();
        defaultBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon);
    }

    public void setData(List<AudioBean> listData) {
        mListData = listData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Glide.with(mContext)
//                .load(getMusicPicture(mListData.get(position).getPath()))
//                .apply(mGlideOptions)
//                .into(holder.music_iv);

        try {
            // 显示专辑缩略图片
            holder.music_iv.setImageBitmap(mContext.getContentResolver().loadThumbnail(mListData.get(position).getUri(), new Size(50, 50), null));
        } catch (IOException e) {
//            e.printStackTrace();
            holder.music_iv.setImageBitmap(defaultBitmap);
        }
        holder.music_tv.setText(mListData.get(position).getDisplayName());
    }

    /**
     * 加载专辑图片
     *
     * @param path 资源路径
     */
    private Bitmap getMusicPicture(String path) {
        LogUtil.d(TAG, "path=" + path);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(path);
            byte[] cover = mediaMetadataRetriever.getEmbeddedPicture();
            if (cover != null && cover.length > 0) {
                return BitmapFactory.decodeByteArray(cover, 0, cover.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultBitmap;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.music_iv)
        ImageView music_iv;

        @BindView(R.id.music_tv)
        TextView music_tv;

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
        void onItemClick(AudioBean audioBean);
    }
}
