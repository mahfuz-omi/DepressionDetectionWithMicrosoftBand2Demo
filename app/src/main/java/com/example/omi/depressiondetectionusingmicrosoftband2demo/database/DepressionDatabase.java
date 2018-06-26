package com.example.omi.depressiondetectionusingmicrosoftband2demo.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.dao.GPSDataDao;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.dao.HeartRateDataDao;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.dao.PhoneCallDataDao;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.dao.SMSDataDao;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.dao.SocialNetworkDataDao;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.GPSData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.HeartRateData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.PhoneCallData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.SMSData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.SocialNetworkData;



/**
 * Created by USER on 11/27/2017.
 */

@Database(entities = {GPSData.class, HeartRateData.class, PhoneCallData.class, SMSData.class, SocialNetworkData.class}, version = 1)
public abstract class DepressionDatabase extends RoomDatabase {
    public abstract GPSDataDao gpsDataDao();
    public abstract HeartRateDataDao heartRateDataDao();
    public abstract PhoneCallDataDao phoneCallDataDao();
    public abstract SMSDataDao smsDataDao();
    public abstract SocialNetworkDataDao socialNetworkDataDao();
}
