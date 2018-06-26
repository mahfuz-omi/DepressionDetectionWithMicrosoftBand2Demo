package com.example.omi.depressiondetectionusingmicrosoftband2demo.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class PhoneCallData extends UserCredentialsData{

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String phoneNumber;

    // 1=incoming,2=outgoing,3=missed
    private int type;
    private int durationInSeconds;

    // dd:MM:yyyy hh:mm:ss
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
