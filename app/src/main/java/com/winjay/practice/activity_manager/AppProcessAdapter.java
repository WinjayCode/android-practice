package com.winjay.practice.activity_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.winjay.practice.R;

import java.util.List;

public class AppProcessAdapter extends RecyclerView.Adapter<AppProcessAdapter.MyHolder> {
    private Context mContext;
    private List<AMProcessInfo> listData;

    public AppProcessAdapter(Context context, List<AMProcessInfo> listData) {
        mContext = context;
        this.listData = listData;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.app_process_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.pid_tv.setText(listData.get(position).getPid());
        holder.uid_tv.setText(listData.get(position).getUid());
        holder.memory_size_tv.setText(listData.get(position).getMemorySize());
        holder.process_name_tv.setText(listData.get(position).getProcessName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        AppCompatTextView pid_tv;

        AppCompatTextView uid_tv;

        AppCompatTextView memory_size_tv;

        AppCompatTextView process_name_tv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            pid_tv = itemView.findViewById(R.id.pid_tv);
            uid_tv = itemView.findViewById(R.id.pid_tv);
            memory_size_tv = itemView.findViewById(R.id.pid_tv);
            process_name_tv = itemView.findViewById(R.id.pid_tv);
        }
    }
}
