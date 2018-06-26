package com.example.omi.depressiondetectionusingmicrosoftband2demo.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SMSData extends UserCredentialsData{

    @PrimaryKey(autoGenerate = true)
    private int id;

    private boolean isIncoming;
    private String phoneNumber;
    private int smsLength;

    // dd:MM:yyyy hh:mm:ss
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public void setIncoming(boolean incoming) {
        isIncoming = incoming;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getSmsLength() {
        return smsLength;
    }

    public void setSmsLength(int smsLength) {
        this.smsLength = smsLength;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
