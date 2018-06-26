package com.example.omi.depressiondetectionusingmicrosoftband2demo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.GPSData;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.SocialNetworkData;

import java.util.List;

@Dao
public interface SocialNetworkDataDao {

    // Read all
    @Query("SELECT * FROM SocialNetworkData")
    List<SocialNetworkData> getAll();

    @Insert
    void insertAll(List<SocialNetworkData> socialNetworkDatas);

    // Create single
    @Insert
    void insert(SocialNetworkData socialNetworkData);
}
