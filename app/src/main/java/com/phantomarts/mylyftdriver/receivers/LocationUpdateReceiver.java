package com.phantomarts.mylyftdriver.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.phantomarts.mylyftdriver.MainActivity;
import com.phantomarts.mylyftdriver.R;


public class LocationUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "LocationUpdateReceiver";
    public static final String LOCATION_UPDATE_ACTION = "com.phantomarts.mylyftdriver.LOCATION_UPDATE";
    private GoogleMap mMap;

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        double latitude = intent.getDoubleExtra("lat", 0);
        double longitude = intent.getDoubleExtra("lng", 0);
        float bearing = intent.getFloatExtra("bearing", 0);
        LatLng location = new LatLng(latitude, longitude);
        Log.d(TAG, "onReceive: location updated broadcast received");
        MainActivity.mLastLocation=location;

        if (MainActivity.mCurrent != null) {
            MainActivity.mCurrent.remove();
        }
        MainActivity.mCurrent = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                .position(location)
                .flat(true)
                .rotation(bearing)
                .title("You")
                .anchor(0.5f, 0.5f));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));

        rotateMarker(MainActivity.mCurrent, -360);

    }

    private void rotateMarker(Marker mCurrent, int i) {
    }


}
