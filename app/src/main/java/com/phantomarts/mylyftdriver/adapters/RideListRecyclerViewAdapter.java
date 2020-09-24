package com.phantomarts.mylyftdriver.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.phantomarts.mylyftdriver.R;
import com.phantomarts.mylyftdriver.model.Ride;

import java.util.ArrayList;

public class RideListRecyclerViewAdapter extends RecyclerView.Adapter<RideListRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RideListRecyViewAdapter";

    private ArrayList<Ride> mrideList = new ArrayList<>();
    private Context mContext;

    public RideListRecyclerViewAdapter(Context mContext, ArrayList<Ride> rideList) {
        this.mrideList = rideList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rides_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        final Ride ride = mrideList.get(position);
        holder.tvRideDate.setText(ride.getDate());
        holder.tvPickupLocation.setText(ride.getPickupLocation());
        holder.tvDropOffLocation.setText(ride.getDropoffLocation());
        holder.tvPickupTime.setText(ride.getPickupTime());
        holder.tvDropOffTime.setText(ride.getDropoffTime());
        holder.tvRideFare.setText(ride.getRideFare() + "");

        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("data", ride.toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on " + mrideList.get(position));
                Toast.makeText(mContext, "clicked on: " + mrideList.get(position), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mrideList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRideDate, tvPickupLocation, tvDropOffLocation, tvPickupTime, tvDropOffTime, tvRideFare;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRideDate = itemView.findViewById(R.id.rideDate);
            tvPickupLocation = itemView.findViewById(R.id.tvpickuplocation);
            tvDropOffLocation = itemView.findViewById(R.id.tvdropofflocation);
            tvPickupTime = itemView.findViewById(R.id.tvpickuptime);
            tvDropOffTime = itemView.findViewById(R.id.tvdropofftime);
            tvRideFare = itemView.findViewById(R.id.tvridefare);
            parentLayout = itemView.findViewById(R.id.ridelist_parentlayout);
        }
    }
}
