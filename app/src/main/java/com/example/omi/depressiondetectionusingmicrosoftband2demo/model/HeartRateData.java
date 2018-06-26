package com.example.omi.depressiondetectionusingmicrosoftband2demo.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class HeartRateData extends UserCredentialsData {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // dd:MM;yyyy hh:mm:ss format
    private String time;
    private int heartBitPerSecond;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHeartBitPerSecond() {
        return heartBitPerSecond;
    }

    public void setHeartBitPerSecond(int heartBitPerSecond) {
        this.heartBitPerSecond = heartBitPerSecond;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
