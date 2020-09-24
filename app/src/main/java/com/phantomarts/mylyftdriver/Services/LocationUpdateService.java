package com.phantomarts.mylyftdriver.Services;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.phantomarts.mylyftdriver.R;
import com.phantomarts.mylyftdriver.receivers.LocationUpdateReceiver;


public class LocationUpdateService extends IntentService {
    private static final String TAG = "LocationService";

    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    private final static long FASTEST_INTERVAL = 2000; /* 2 sec */

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;


    public static volatile boolean shouldStop=false;

    public LocationUpdateService() {
        super("LocationUpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onStartCommand: called.");
        getLocation();

    }

    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if(shouldStop){
                            Log.d(TAG, "onShouldStop: stooping location result");
                            stopSelf();
                            mFusedLocationClient.removeLocationUpdates(this);
                            return;
                        }

                        Log.d(TAG, "onLocationResult: got location result.");

                        Location location = locationResult.getLastLocation();

                        if (location != null) {
                            GeoLocation geoLocation=new GeoLocation(location.getLatitude(),location.getLongitude());

                            saveUserLocation(geoLocation,location.getBearing());
                        }
                    }
                },
                Looper.getMainLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    private void saveUserLocation(final GeoLocation userLocation,final float bearing){
        try{
            DatabaseReference locationRef = firebaseDatabase.getReference("/locations");
            final GeoFire geoFire=new GeoFire(locationRef);
            geoFire.setLocation(firebaseAuth.getCurrentUser().getUid(), userLocation, new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if(error==null){
                        Log.d(TAG," location saved sucessfully <"+firebaseAuth.getCurrentUser().getUid()+">");
                        //sending broadcast to update ui
                        Intent intent=new Intent();
                        intent.setAction(LocationUpdateReceiver.LOCATION_UPDATE_ACTION);
                        intent.putExtra("lat",userLocation.latitude);
                        intent.putExtra("lng",userLocation.longitude);
                        intent.putExtra("bearing",bearing);
                        LocalBroadcastManager.getInstance(LocationUpdateService.this).sendBroadcast(intent);
                    }else{
                        Log.d(TAG,"There was an error saving the location to GeoFire: " + error);
                    }
                }
            });
        }catch (NullPointerException e){
            Log.e(TAG, "saveUserLocation: User instance is null, stopping location service.");
            Log.e(TAG, "saveUserLocation: NullPointerException: "  + e.getMessage() );
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
