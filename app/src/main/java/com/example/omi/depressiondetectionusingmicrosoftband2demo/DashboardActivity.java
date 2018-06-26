package com.example.omi.depressiondetectionusingmicrosoftband2demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.lang.ref.WeakReference;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.HeartRateData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.PhoneCallData;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.application.DepressionDetectionApplication;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.Constants;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.DBAsynctask;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.DBCallbackListenerInterface;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.GPSData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.receiver.PhoneSMSReceiver;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener,LocationListener, DBCallbackListenerInterface {
    Button startMonitorButton;

    private BandClient client = null;

    // Location Request Data
    private long UPDATE_INTERVAL = 5*60*1000;  /* 15 mins */
    private long UPDATE_DISTANCE = 50; /* 50 meters */

    LocationManager locationManager;
    private TextView txtStatus;
    private Button callLogButton;
    private Button socialNetworkButton;


    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null)
            {
                appendToUI(String.format("Heart Rate = %d beats per minute\n"
                        + "Quality = %s\n", event.getHeartRate(), event.getQuality()));

                HeartRateData heartRateData = new HeartRateData();
                heartRateData.setHeartBitPerSecond(event.getHeartRate());
                heartRateData.setTime(DepressionDetectionApplication.getApplication().getFullDateTimeFormatter().format(Calendar.getInstance().getTime()));

                // save this data to local database
                DBAsynctask dbAsynctask = new DBAsynctask(DashboardActivity.this);
                dbAsynctask.setDbCallbackListenerInterface(DashboardActivity.this);
                dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.INSERT_HEARTRATEDATA);
                dbAsynctask.execute(heartRateData);
            }
        }
    };



    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {
                if (getConnectedBandClient()) {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        this.startMonitorButton = (Button)this.findViewById(R.id.startMonitorButton);
        this.startMonitorButton.setOnClickListener(this);
        this.txtStatus = (TextView) findViewById(R.id.txtStatus);
        this.callLogButton = (Button)this.findViewById(R.id.callLogButton);
        this.callLogButton.setOnClickListener(this);
        this.socialNetworkButton = (Button)this.findViewById(R.id.socialNetworkButton);
        this.socialNetworkButton.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            // request permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_CALL_LOG}, 1);
        }


    }

    @SuppressLint("MissingPermission")
    public void startMonitorService()
    {
        // enable the broadcastreceiver
        ComponentName receiver = new ComponentName(this, PhoneSMSReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);


        // start location tracking
        this.locationManager = (LocationManager)this.getSystemService((Context.LOCATION_SERVICE));
        this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,UPDATE_INTERVAL,UPDATE_DISTANCE,this);



        // start heart rate tracking
        // ar first, take user consent
        final WeakReference<Activity> reference = new WeakReference<Activity>(this);
        new HeartRateConsentTask().execute(reference);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.startMonitorButton:
            {
                this.startMonitorService();
            }
            case R.id.callLogButton:
            {
                this.saveCallLogToDB();
            }
            case R.id.socialNetworkButton:
            {
                this.saveSocialNetworkData();
            }
        }
    }

    public void saveSocialNetworkData()
    {
        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        final long start = calendar.getTimeInMillis();
        final long end = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
                System.out.println("");
                for(UsageStats usageStats:stats)
                {
                    String packageName = usageStats.getPackageName();
                    if(packageName.equalsIgnoreCase("com.facebook.katana") || packageName.equalsIgnoreCase("com.facebook.orca") || packageName.equalsIgnoreCase("com.instagram.android"))
                    {
                        System.out.println(usageStats.getPackageName()+"  :  "+getTimeFromMilliseconds(usageStats.getTotalTimeInForeground()));
                    }

                }
            }
        }).start();

        System.out.println("");
    }

    public void saveCallLogToDB()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-7);
        Date sinceDate = calendar.getTime();
        @SuppressLint("MissingPermission")
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
                new String[] { CallLog.Calls.DATE, CallLog.Calls.DURATION,
                        CallLog.Calls.NUMBER, CallLog.Calls.TYPE },
                CallLog.Calls.DATE + ">?",
                new String[] { String.valueOf(sinceDate.getTime())},
                CallLog.Calls.DATE + " desc");
        if(cursor != null)
            cursor.moveToFirst();

        while(cursor != null && cursor.moveToNext())
        {
            PhoneCallData phoneCallData = new PhoneCallData();

            phoneCallData.setPhoneNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
            phoneCallData.setDurationInSeconds(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION)));
            phoneCallData.setType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));
            phoneCallData.setTime( DepressionDetectionApplication.getApplication().getFullDateTimeFormatter().format(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)))))     );

            if(phoneCallData.getDurationInSeconds() > 0)
            {
                // save this data to local database
                DBAsynctask dbAsynctask = new DBAsynctask(this);
                dbAsynctask.setDbCallbackListenerInterface(this);
                dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.INSERT_PHONECALLDATA);
                dbAsynctask.execute(phoneCallData);
            }




        }

        System.out.println("");

    }

    @Override
    public void onLocationChanged(Location location) {

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

    @Override
    public void beforeDBCall(int resourceIdentifier) {

    }

    @Override
    public void afterDBCall(int resourceIdentifier, Object response) {

    }

    private void appendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(string);
            }
        });
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
