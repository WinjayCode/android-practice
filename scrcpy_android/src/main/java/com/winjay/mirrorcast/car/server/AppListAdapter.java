package com.winjay.mirrorcast.car.server;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
import com.winjay.mirrorcast.R;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
    private static final String TAG = AppListAdapter.class.getSimpleName();
    private Context mContext;
    private List<AppBean> mListData;

    public OnItemClickListener itemClickListener;

//    private RequestOptions mGlideOptions;

    public AppListAdapter(Context context) {
        mContext = context;
        mListData = new ArrayList<>();
//        mGlideOptions = new RequestOptions()
//                .placeholder(R.mipmap.icon)
//                .centerCrop();
    }

    public void setData(List<AppBean> listData) {
        mListData = listData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Glide.with(mContext)
//                .load(mListData.get(position).getPath())
//                .apply(mGlideOptions)
//                .into(holder.video_iv);

        holder.app_name.setText(mListData.get(position).getAppName());

        holder.app_icon.setImageDrawable(mListData.get(position).getAppIcon());
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView app_icon;
        TextView app_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            app_icon = itemView.findViewById(R.id.app_icon_iv);
            app_name = itemView.findViewById(R.id.app_name);
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
        void onItemClick(AppBean videoBean);
    }
}
