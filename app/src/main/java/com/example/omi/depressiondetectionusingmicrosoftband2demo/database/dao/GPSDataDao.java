package com.example.omi.depressiondetectionusingmicrosoftband2demo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.GPSData;

import java.util.List;

@Dao
public interface GPSDataDao {

    // Read all
    @Query("SELECT * FROM GPSData")
    List<GPSData> getAll();

    @Insert
    void insertAll(List<GPSData> gpsData);

    // Create single
    @Insert
    void insert(GPSData gpsData);

}
