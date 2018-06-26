package com.example.omi.depressiondetectionusingmicrosoftband2demo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.GPSData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.PhoneCallData;

import java.util.List;

@Dao
public interface PhoneCallDataDao {

    // Read all
    @Query("SELECT * FROM PhoneCallData")
    List<PhoneCallData> getAll();

    @Insert
    void insertAll(List<PhoneCallData> phoneCallDatas);

    // Create single
    @Insert
    void insert(PhoneCallData phoneCallData);
}
