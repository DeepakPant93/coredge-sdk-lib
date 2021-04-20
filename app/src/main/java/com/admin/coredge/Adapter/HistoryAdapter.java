package com.admin.coredge.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.admin.coredge.Modal.HistoryModel;
import com.admin.coredge.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context mcontext;
    private List<HistoryModel> mData;

    public HistoryAdapter (Context mcontext, List<HistoryModel> mData){
        this.mcontext = mcontext;
        this.mData = mData;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        LayoutInflater minflater = LayoutInflater.from(mcontext);
        view = minflater.inflate(R.layout.item_history, viewGroup, false);

        final MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        String date = mData.get(position).getTimestamp();
        String [] time = date.split("T");
        myViewHolder.date.setText(time[0]);
        myViewHolder.ping.setText(mData.get(position).getPing()+" ms");
        myViewHolder.download.setText(mData.get(position).getDownloading()+ " Mbps");
        myViewHolder.upload.setText(mData.get(position).getUploading()+ " Mbps");
        myViewHolder.ip.setText(mData.get(position).getIp());
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

public static class MyViewHolder extends RecyclerView.ViewHolder{

    TextView date, ping, download, upload, ip;
    LinearLayout view_container;

    public MyViewHolder(View itemView){
        super(itemView);
        view_container = itemView.findViewById(R.id.hist);
        date =(TextView) itemView.findViewById(R.id.date);
        ping = (TextView) itemView.findViewById(R.id.ping);
        download =(TextView) itemView.findViewById(R.id.download);
        upload = (TextView) itemView.findViewById(R.id.upload);
        ip = (TextView) itemView.findViewById(R.id.ip);

    }
}

}
