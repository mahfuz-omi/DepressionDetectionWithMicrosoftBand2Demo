package com.example.omi.depressiondetectionusingmicrosoftband2demo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.GPSData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.SMSData;

import java.util.List;

@Dao
public interface SMSDataDao {

    // Read all
    @Query("SELECT * FROM SMSData")
    List<SMSData> getAll();

    @Insert
    void insertAll(List<SMSData> smsDatas);

    // Create single
    @Insert
    void insert(SMSData smsData);
}
