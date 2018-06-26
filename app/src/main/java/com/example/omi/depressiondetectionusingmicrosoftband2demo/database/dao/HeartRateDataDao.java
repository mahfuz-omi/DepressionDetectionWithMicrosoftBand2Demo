package com.example.omi.depressiondetectionusingmicrosoftband2demo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.HeartRateData;

import java.util.List;

@Dao
public interface HeartRateDataDao {

    // Read all
    @Query("SELECT * FROM HeartRateData")
    List<HeartRateData> getAll();

    @Insert
    void insertAll(List<HeartRateData> heartRateDatas);

    // Create single
    @Insert
    void insert(HeartRateData heartRateData);
}
