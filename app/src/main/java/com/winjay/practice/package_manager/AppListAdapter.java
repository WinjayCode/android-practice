package com.winjay.practice.package_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.MyHolder> {
    private Context mContext;
    private List<PMAppInfo> listData;

    public AppListAdapter(Context context, List<PMAppInfo> listData) {
        mContext = context;
        this.listData = listData;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.app_list_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        if (listData.get(position).getAppIcon() != null) {
            holder.app_icon_iv.setImageDrawable(listData.get(position).getAppIcon());
        }
        holder.app_name_tv.setText(listData.get(position).getAppLabel());
        holder.pkg_name_tv.setText(listData.get(position).getPkgName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView app_icon_iv;

        TextView app_name_tv;

        TextView pkg_name_tv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            app_icon_iv = itemView.findViewById(R.id.app_icon_iv);
            app_name_tv = itemView.findViewById(R.id.app_name_tv);
            pkg_name_tv = itemView.findViewById(R.id.pkg_name_tv);
        }
    }
}
