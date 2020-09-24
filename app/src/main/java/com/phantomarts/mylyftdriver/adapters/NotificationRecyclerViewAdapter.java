package com.phantomarts.mylyftdriver.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.phantomarts.mylyftdriver.R;
import com.phantomarts.mylyftdriver.model.Notification;

import java.util.ArrayList;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "NotifyRecyclerViewAdapter";

    private ArrayList<Notification> mNotificationList;
    private Context mContext;

    public NotificationRecyclerViewAdapter(Context mContext,ArrayList<Notification> mNotificationList) {
        this.mNotificationList = mNotificationList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notificationitem, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: notification called");
        Notification notification=mNotificationList.get(position);
        holder.tvSender.setText(notification.getSender());
        holder.tvDate.setText(notification.getDate());
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMsg.setText(notification.getMsg());
        if(notification.getIcon()!=null){
            //set icon
        }
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "clicked: "+position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvSender,tvDate,tvTitle,tvMsg;
        ImageView icon;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender=itemView.findViewById(R.id.tvsender);
            tvDate=itemView.findViewById(R.id.tvdate);
            tvTitle=itemView.findViewById(R.id.tvnotificationTitle);
            tvMsg=itemView.findViewById(R.id.tvnotificationmsg);
            icon=itemView.findViewById(R.id.notificationIcon);
            parentLayout=itemView.findViewById(R.id.parentlayout_notification);
        }
    }
}
