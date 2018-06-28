package com.example.omi.depressiondetectionusingmicrosoftband2demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.SMSData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.SocialNetworkData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.service.MonitoringService;
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
    private TextView txtStatus;
    private Button callLogButton;
    private Button socialNetworkButton;
    private Button smsButton,showDataButton;


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
//                                Intent service = new Intent(DashboardActivity.this,MonitoringService.class);
//                                startService(service);
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
        this.smsButton = this.findViewById(R.id.smsButton);
        this.smsButton.setOnClickListener(this);
        this.showDataButton = this.findViewById(R.id.showDataButton);
        this.showDataButton.setOnClickListener(this);

        // check if service is already running
        if(this.isServiceRunning(MonitoringService.class))
        {
            // service still running
            this.startMonitorButton.setText("Stop Monitor");
        }

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
        Intent monitorServiceIntent = new Intent(this, MonitoringService.class);

         //check if service is already running
        if(this.isServiceRunning(MonitoringService.class) )
        {
            // service still runnung
            // so, stop it
            this.stopService(monitorServiceIntent);
            this.startMonitorButton.setText("Start Monitor");
            return;
        }


        // enable the broadcastreceiver
//        ComponentName receiver = new ComponentName(this, PhoneSMSReceiver.class);
//        PackageManager pm = this.getPackageManager();
//        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);



//        // start heart rate tracking
//        // ar first, take user consent
        final WeakReference<Activity> reference = new WeakReference<Activity>(this);
        new HeartRateConsentTask().execute(reference);
//
//        this.startMonitorButton.setText("Stop Monitor");
        startService(monitorServiceIntent);
        this.startMonitorButton.setText("Stop Monitor");



    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.startMonitorButton:
            {
                this.startMonitorService();
                break;
            }
            case R.id.callLogButton:
            {
                this.saveCallLogToDB();
                break;
            }
            case R.id.socialNetworkButton:
            {
                this.saveSocialNetworkData();
                break;
            }
            case R.id.smsButton:
            {
                this.saveSMSData();
                break;
            }
            case R.id.showDataButton:
            {
                this.showAllData();
                break;
            }
        }
    }

    public void showAllData()
    {

        // get gps data
        DBAsynctask dbAsynctask = new DBAsynctask(this);
        dbAsynctask.setDbCallbackListenerInterface(this);
        dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.GET_ALL_GPSDATA);
        dbAsynctask.execute();

        // get gps data
        dbAsynctask = new DBAsynctask(this);
        dbAsynctask.setDbCallbackListenerInterface(this);
        dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.GET_ALL_PHONE_CALL_DATA);
        dbAsynctask.execute();

        // get gps data
        dbAsynctask = new DBAsynctask(this);
        dbAsynctask.setDbCallbackListenerInterface(this);
        dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.GET_ALL_HEART_RATE_DATA);
        dbAsynctask.execute();

        // get gps data
        dbAsynctask = new DBAsynctask(this);
        dbAsynctask.setDbCallbackListenerInterface(this);
        dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.GET_ALL_SMSDATA);
        dbAsynctask.execute();

        // get gps data
        dbAsynctask = new DBAsynctask(this);
        dbAsynctask.setDbCallbackListenerInterface(this);
        dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.GET_ALL_SOCIAL_NETWORK_DATA);
        dbAsynctask.execute();

    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void saveSMSData()
    {
        // first, read inbox data
        // Create Inbox box URI
        Uri inboxURI = Uri.parse("content://sms/inbox");

        // List required columns
        String[] projection = new String[] { "date", "address", "body" };

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver contentResolver = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor cursor = contentResolver.query(inboxURI, projection, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        while(cursor != null && cursor.moveToNext())
        {
            SMSData smsData = new SMSData();
            smsData.setPhoneNumber(cursor.getString(cursor.getColumnIndex("address")));
            smsData.setSmsLength(cursor.getString(cursor.getColumnIndex("body")).length());
            smsData.setType(1);
            smsData.setTime( DepressionDetectionApplication.getApplication().getFullDateTimeFormatter().format(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex("date"))))));

            // save this data to local database
            DBAsynctask dbAsynctask = new DBAsynctask(DashboardActivity.this);
            dbAsynctask.setDbCallbackListenerInterface(DashboardActivity.this);
            dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.INSERT_SMSDATA);
            dbAsynctask.execute(smsData);

        }


        // second, read sent data
        // Create Sent box URI
        Uri sentURI = Uri.parse("content://sms/sent");

        // List required columns
        projection = new String[] { "date", "address", "body" };

        // Fetch SENT SMS Message from Built-in Content Provider
        cursor = contentResolver.query(sentURI, projection, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        while(cursor != null && cursor.moveToNext())
        {
            SMSData smsData = new SMSData();
            smsData.setPhoneNumber(cursor.getString(cursor.getColumnIndex("address")));
            smsData.setSmsLength(cursor.getString(cursor.getColumnIndex("body")).length());
            smsData.setType(2);
            smsData.setTime( DepressionDetectionApplication.getApplication().getFullDateTimeFormatter().format(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex("date"))))));

            // save this data to local database
            DBAsynctask dbAsynctask = new DBAsynctask(DashboardActivity.this);
            dbAsynctask.setDbCallbackListenerInterface(DashboardActivity.this);
            dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.INSERT_SMSDATA);
            dbAsynctask.execute(smsData);

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
                        SocialNetworkData socialNetworkData = new SocialNetworkData();
                        socialNetworkData.setSecondsSpent(usageStats.getTotalTimeInForeground()/1000);
                        socialNetworkData.setSocialNetworkName(packageName);

                        // save this data to local database
                        DBAsynctask dbAsynctask = new DBAsynctask(DashboardActivity.this);
                        dbAsynctask.setDbCallbackListenerInterface(DashboardActivity.this);
                        dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.INSERT_SOCIALNETWORKDATA);
                        dbAsynctask.execute(socialNetworkData);

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
        if(resourceIdentifier == Constants.RESOURCE_IDENTIFIER.GET_ALL_GPSDATA)
        {
            List<GPSData> gpsDataList = (List<GPSData>) response;
            System.out.println("gps date read");
        }
        else if(resourceIdentifier == Constants.RESOURCE_IDENTIFIER.GET_ALL_PHONE_CALL_DATA)
        {
            List<PhoneCallData> phoneCallDataList = (List<PhoneCallData>) response;
            System.out.println("phone call date read");
        }
        else if(resourceIdentifier == Constants.RESOURCE_IDENTIFIER.GET_ALL_HEART_RATE_DATA)
        {
            List<HeartRateData> heartRateDataList = (List<HeartRateData>) response;
            System.out.println("heart rate date read");
        }
        else if(resourceIdentifier == Constants.RESOURCE_IDENTIFIER.GET_ALL_GPSDATA)
        {
            List<SMSData> smsDataList = (List<SMSData>) response;
            System.out.println("sms date read");
        }
        else if(resourceIdentifier == Constants.RESOURCE_IDENTIFIER.GET_ALL_SOCIAL_NETWORK_DATA)
        {
            List<SocialNetworkData> socialNetworkDataList = (List<SocialNetworkData>) response;
            System.out.println("social network date read");
        }

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

    public String getTimeFromMilliseconds(long millis)
    {
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d : %02d : %02d : %d", hour, minute, second, millis);
        return time;
    }


}
