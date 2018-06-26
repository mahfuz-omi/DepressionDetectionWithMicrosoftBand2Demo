package com.example.omi.depressiondetectionusingmicrosoftband2demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.Constants;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.DBAsynctask;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.DBCallbackListenerInterface;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.*;

public class DataSaveService extends Service implements DBCallbackListenerInterface {

    public static String TASK_ID = "task_id";
    public static String TASK_DATA = "task_data";

    public static int GPS_DATA = 0;
    public static int HEART_RATE_DATA = 1;
    public static int PHONE_CALL_DATA = 2;
    public static int SMS_DATA = 3;
    public static int SOCIAL_NETWORK_DATA = 4;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        int task_id = intent.getIntExtra(TASK_ID,-1);

        if(task_id == GPS_DATA)
        {
            GPSData gpsData = (GPSData)intent.getSerializableExtra(TASK_DATA);
            this.saveGPSData(gpsData);

        }
        else if(task_id == HEART_RATE_DATA)
        {
            HeartRateData heartRateData = (HeartRateData)intent.getSerializableExtra(TASK_DATA);
            this.saveHeartRateData(heartRateData);

        }

        else if(task_id == PHONE_CALL_DATA)
        {
            PhoneCallData phoneCallData = (PhoneCallData) intent.getSerializableExtra(TASK_DATA);
            this.savePhoneCallData(phoneCallData);

        }

        else if(task_id == SMS_DATA)
        {
            SMSData smsData = (SMSData) intent.getSerializableExtra(TASK_DATA);
            this.saveSMSData(smsData);

        }

        else if(task_id == SOCIAL_NETWORK_DATA)
        {
            SocialNetworkData socialNetworkData = (SocialNetworkData) intent.getSerializableExtra(TASK_DATA);
            this.saveSocialNetworkData(socialNetworkData);

        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void saveGPSData(GPSData gpsData)
    {
        DBAsynctask dbAsynctask = new DBAsynctask(this);
        dbAsynctask.setDbCallbackListenerInterface(this);
        dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.INSERT_GPSDATA);
        dbAsynctask.execute(gpsData);

    }

    public void saveHeartRateData(HeartRateData heartRateData)
    {
        DBAsynctask dbAsynctask = new DBAsynctask(this);
        dbAsynctask.setDbCallbackListenerInterface(this);
        dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.INSERT_HEARTRATEDATA);
        dbAsynctask.execute(heartRateData);
    }

    public void savePhoneCallData(PhoneCallData phoneCallData)
    {
        DBAsynctask dbAsynctask = new DBAsynctask(this);
        dbAsynctask.setDbCallbackListenerInterface(this);
        dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.INSERT_PHONECALLDATA);
        dbAsynctask.execute(phoneCallData);
    }

    public void saveSMSData(SMSData smsData)
    {
        DBAsynctask dbAsynctask = new DBAsynctask(this);
        dbAsynctask.setDbCallbackListenerInterface(this);
        dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.INSERT_SMSDATA);
        dbAsynctask.execute(smsData);
    }

    public void saveSocialNetworkData(SocialNetworkData socialNetworkData)
    {
        DBAsynctask dbAsynctask = new DBAsynctask(this);
        dbAsynctask.setDbCallbackListenerInterface(this);
        dbAsynctask.setResourceIdentifier(Constants.RESOURCE_IDENTIFIER.INSERT_SOCIALNETWORKDATA);
        dbAsynctask.execute(socialNetworkData);
    }

    @Override
    public void beforeDBCall(int resourceIdentifier) {

    }

    @Override
    public void afterDBCall(int resourceIdentifier, Object response) {

    }
}
