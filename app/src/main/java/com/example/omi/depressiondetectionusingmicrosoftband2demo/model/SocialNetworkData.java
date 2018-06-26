package com.example.omi.depressiondetectionusingmicrosoftband2demo.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SocialNetworkData extends UserCredentialsData {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // per day basis = time is a day, time format dd:MM:yyyy
    private String time;
    private int secondsSpent;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSecondsSpent() {
        return secondsSpent;
    }

    public void setSecondsSpent(int secondsSpent) {
        this.secondsSpent = secondsSpent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
