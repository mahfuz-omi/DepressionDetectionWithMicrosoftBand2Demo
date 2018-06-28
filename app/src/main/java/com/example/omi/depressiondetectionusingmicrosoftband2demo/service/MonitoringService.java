package com.example.omi.depressiondetectionusingmicrosoftband2demo.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.application.DepressionDetectionApplication;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.Constants;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.DBAsynctask;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.DBCallbackListenerInterface;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.GPSData;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class MonitoringService extends Service implements LocationListener,DBCallbackListenerInterface{

    public static boolean isServiceRunning = false;

    // Location Request Data
    private long UPDATE_INTERVAL = 1000;  /* 15 mins */
    private long UPDATE_DISTANCE = 0; /* 50 meters */

    LocationManager locationManager;
    BandClient client = null;


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

        if(this.locationManager != null)
        {
            // service is already running
            return;
        }
        this.locationManager = (LocationManager)this.getSystemService((Context.LOCATION_SERVICE));
        this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,UPDATE_INTERVAL,UPDATE_DISTANCE,this);
        //new HeartRateSubscriptionTask().execute();


    }

    @Override
    public void onLocationChanged(Location location)
    {
        System.out.println("Location changed");

        GPSData gpsData = new GPSData();

//        gpsData.setUserName(DepressionDetectionApplication.getApplication().getUserCredentialsData().getUserName());
//        gpsData.setAge(DepressionDetectionApplication.getApplication().getUserCredentialsData().getAge());
//        gpsData.setSex(DepressionDetectionApplication.getApplication().getUserCredentialsData().getSex());

        gpsData.setLatitude(location.getLatitude());
        gpsData.setLongitude(location.getLongitude());
        gpsData.setTime(DepressionDetectionApplication.getApplication().getFullDateTimeFormatter().format(Calendar.getInstance().getTime()));

        // save this data to local database
        DBAsynctask dbAsynctask = new DBAsynctask(this);
        dbAsynctask.setDbCallbackListenerInterface(this);
        dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.INSERT_GPSDATA);
        dbAsynctask.execute(gpsData);

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


    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null)
            {
                appendToUI(String.format("Heart Rate = %d beats per minute\n"
                        + "Quality = %s\n", event.getHeartRate(), event.getQuality()));

//                HeartRateData heartRateData = new HeartRateData();
//                heartRateData.setHeartBitPerSecond(event.getHeartRate());
//                heartRateData.setTime(DepressionDetectionApplication.getApplication().getFullDateTimeFormatter().format(Calendar.getInstance().getTime()));
//
//                // save this data to local database
//                DBAsynctask dbAsynctask = new DBAsynctask(DashboardActivity.this);
//                dbAsynctask.setDbCallbackListenerInterface(DashboardActivity.this);
//                dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.INSERT_HEARTRATEDATA);
//                dbAsynctask.execute(heartRateData);
            }
        }
    };

    @Override
    public void beforeDBCall(int resourceIdentifier) {

    }

    @Override
    public void afterDBCall(int resourceIdentifier, Object response) {

    }


    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {
                if (getConnectedBandClient())
                {
                    if (params[0].get() != null) {
                        client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                                new HeartRateSubscriptionTask().execute();
                            }
                        });
                    }
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }


    private class HeartRateSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                    } else {
                        appendToUI("You have not given this application consent to access heart rate data yet."
                                + " Please press the Heart Rate Consent button.\n");
                    }
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }

    private void appendToUI(String string) {
        System.out.println(string);
    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        appendToUI("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }


}
