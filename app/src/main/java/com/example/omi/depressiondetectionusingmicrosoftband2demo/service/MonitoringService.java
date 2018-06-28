package com.example.omi.depressiondetectionusingmicrosoftband2demo.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class MonitoringService extends Service implements LocationListener{

    public static boolean isServiceRunning = false;

    // Location Request Data
    private long UPDATE_INTERVAL = 1000;  /* 15 mins */
    private long UPDATE_DISTANCE = 0; /* 50 meters */

    LocationManager locationManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // start location service
        this.runServiceTask();
        return super.onStartCommand(intent, flags, startId);



    }

    @SuppressLint("MissingPermission")
    public void runServiceTask()
    {
        // start Location and band heart rate monitor service
        // start location tracking
        this.locationManager = (LocationManager)this.getSystemService((Context.LOCATION_SERVICE));
        this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,UPDATE_INTERVAL,UPDATE_DISTANCE,this);

    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Location changed");

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
