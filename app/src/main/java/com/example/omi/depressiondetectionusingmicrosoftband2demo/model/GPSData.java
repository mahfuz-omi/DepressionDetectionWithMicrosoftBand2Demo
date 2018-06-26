package com.example.omi.depressiondetectionusingmicrosoftband2demo.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class GPSData extends UserCredentialsData{
    @PrimaryKey(autoGenerate = true)
    private int id;

    // a particular time, format dd:MM:yyyy hh:mm:ss
    private String time;
    private double latitude;
    private double longitude;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
