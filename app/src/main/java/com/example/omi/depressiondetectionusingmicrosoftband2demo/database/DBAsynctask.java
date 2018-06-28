package com.example.omi.depressiondetectionusingmicrosoftband2demo.database;

import android.content.Context;
import android.os.AsyncTask;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.application.DepressionDetectionApplication;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.Constants;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.DBCallbackListenerInterface;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.GPSData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.HeartRateData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.PhoneCallData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.SMSData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.SocialNetworkData;



/**
 * Created by USER on 11/27/2017.
 */

public class DBAsynctask extends AsyncTask<Object, Integer, Object> {

    private int resourceIdentifier;
    private DBCallbackListenerInterface dbCallbackListenerInterface;
    Context context;


    public DBCallbackListenerInterface getDbCallbackListenerInterface() {
        return dbCallbackListenerInterface;
    }

    public void setDbCallbackListenerInterface(DBCallbackListenerInterface dbCallbackListenerInterface) {
        this.dbCallbackListenerInterface = dbCallbackListenerInterface;
    }

    public DBAsynctask(Context context) {
        this.context = context;

    }

    public int getResourceIdentifier() {
        return resourceIdentifier;
    }

    public void setResourceIdentifier(int resourceIdentifier) {

        this.resourceIdentifier = resourceIdentifier;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dbCallbackListenerInterface.beforeDBCall(this.resourceIdentifier);
    }

    @Override
    protected Object doInBackground(Object... inputDatas) {
        if(this.resourceIdentifier == Constants.RESOURCE_IDENTIFIER.INSERT_GPSDATA)
        {
            GPSData gpsData = (GPSData)inputDatas[0];
            DepressionDetectionApplication.getApplication().getDB().gpsDataDao().insert(gpsData);
        }
        else if(this.resourceIdentifier == Constants.RESOURCE_IDENTIFIER.INSERT_HEARTRATEDATA)
        {
            HeartRateData heartRateData = (HeartRateData) inputDatas[0];
            DepressionDetectionApplication.getApplication().getDB().heartRateDataDao().insert(heartRateData);

        }
        else if(this.resourceIdentifier == Constants.RESOURCE_IDENTIFIER.INSERT_PHONECALLDATA)
        {
            PhoneCallData phoneCallData = (PhoneCallData) inputDatas[0];
            DepressionDetectionApplication.getApplication().getDB().phoneCallDataDao().insert(phoneCallData);

        }

        else if(this.resourceIdentifier == Constants.RESOURCE_IDENTIFIER.INSERT_SMSDATA)
        {
            SMSData smsData = (SMSData) inputDatas[0];
            DepressionDetectionApplication.getApplication().getDB().smsDataDao().insert(smsData);

        }
        else if(this.resourceIdentifier == Constants.RESOURCE_IDENTIFIER.INSERT_SOCIALNETWORKDATA)
        {
            SocialNetworkData socialNetworkData = (SocialNetworkData) inputDatas[0];
            DepressionDetectionApplication.getApplication().getDB().socialNetworkDataDao().insert(socialNetworkData);
        }
        else if(this.resourceIdentifier == Constants.RESOURCE_IDENTIFIER.GET_ALL_GPSDATA)
        {
            return DepressionDetectionApplication.getApplication().getDB().gpsDataDao().getAll();
        }

        else if(this.resourceIdentifier == Constants.RESOURCE_IDENTIFIER.GET_ALL_PHONE_CALL_DATA)
        {
            return DepressionDetectionApplication.getApplication().getDB().phoneCallDataDao().getAll();
        }

        else if(this.resourceIdentifier == Constants.RESOURCE_IDENTIFIER.GET_ALL_HEART_RATE_DATA)
        {
            return DepressionDetectionApplication.getApplication().getDB().heartRateDataDao().getAll();
        }
        else if(this.resourceIdentifier == Constants.RESOURCE_IDENTIFIER.GET_ALL_SMSDATA)
        {
            return DepressionDetectionApplication.getApplication().getDB().smsDataDao().getAll();
        }
        else if(this.resourceIdentifier == Constants.RESOURCE_IDENTIFIER.GET_ALL_SOCIAL_NETWORK_DATA)
        {
            return DepressionDetectionApplication.getApplication().getDB().socialNetworkDataDao().getAll();
        }
        return null;

    }


    @Override
    protected void onPostExecute(Object response) {
        super.onPostExecute(response);
        this.dbCallbackListenerInterface.afterDBCall(this.resourceIdentifier, response);
    }
}


