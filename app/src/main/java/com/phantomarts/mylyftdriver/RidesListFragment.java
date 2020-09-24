package com.phantomarts.mylyftdriver;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.phantomarts.mylyftdriver.adapters.RideListRecyclerViewAdapter;
import com.phantomarts.mylyftdriver.model.Ride;

import java.util.ArrayList;

public class RidesListFragment extends Fragment {
    private static final String TAG = "RidesListFragment";
    private ArrayList<Ride> mRides = new ArrayList<>();
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rides_list, container, false);
        Log.d(TAG, "onCreateView: created");

        mRecyclerView=view.findViewById(R.id.myrides_recycler_view);

        initRidesData();
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        Ride ride1 = new Ride();
        ride1.setDate("29 August 2019");
        ride1.setPickupLocation("Kaduwela Rd,Malabe");
        ride1.setPickupTime("10:30PM");
        ride1.setDropoffLocation("SLIIT,Malabe");
        ride1.setDropoffTime("10:40PM");
        ride1.setDiscount(50.00);
        ride1.setTotalAmount(121.12);

        Ride ride2 = new Ride();
        ride2.setDate("12 August 2019");
        ride2.setPickupLocation("Kaduwela Rd,Malabe");
        ride2.setPickupTime("10:30PM");
        ride2.setDropoffLocation("D.S.Senanayake College,Colombo07");
        ride2.setDropoffTime("11:40PM");
        ride2.setDiscount(50.00);
        ride2.setTotalAmount(314.12);

        mRides.add(ride1);
        mRides.add(ride2);
        mRides.add(ride1);
    }

    private void initRidesData() {
        RideListRecyclerViewAdapter recyclerViewAdapter=new RideListRecyclerViewAdapter(getContext(),mRides);
        mRecyclerView.setAdapter(recyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
