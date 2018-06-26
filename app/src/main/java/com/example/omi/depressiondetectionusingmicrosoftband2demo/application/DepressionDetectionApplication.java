package com.example.omi.depressiondetectionusingmicrosoftband2demo.application;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.database.DepressionDatabase;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.UserCredentialsData;

import java.text.SimpleDateFormat;

import io.paperdb.Paper;

public class DepressionDetectionApplication extends Application {

    private UserCredentialsData userCredentialsData;
    public static String USER_DATA_KEY = "userCredentialsData";

    public static DepressionDetectionApplication INSTANCE;
    private static final String DATABASE_NAME = "VMSDatabase";
    DepressionDatabase depressionDatabase;

    // data/time formatter

    SimpleDateFormat fullDateTimeFormatter;
    SimpleDateFormat onlyDateFormatter;

    @Override
    public void onCreate() {
        super.onCreate();

        Paper.init(this);

        this.userCredentialsData = Paper.book().read(USER_DATA_KEY,null);

        // create database
        this.depressionDatabase = Room.databaseBuilder(getApplicationContext(), DepressionDatabase.class, DATABASE_NAME)
                //.addMigrations(MyDatabase.MIGRATION_1_2)
                // this migration will re-create all of the tables
                .fallbackToDestructiveMigration()
                .build();

        INSTANCE = this;

        this.fullDateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        this.onlyDateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    }

    public UserCredentialsData getUserCredentialsData() {
        return userCredentialsData;
    }

    public void setUserCredentialsData(UserCredentialsData userCredentialsData) {
        this.userCredentialsData = userCredentialsData;
        Paper.book().write(USER_DATA_KEY,this.userCredentialsData);
    }

    public DepressionDatabase getDB()
    {
        return this.depressionDatabase;
    }

    public SimpleDateFormat getFullDateTimeFormatter() {
        return fullDateTimeFormatter;
    }

    public void setFullDateTimeFormatter(SimpleDateFormat fullDateTimeFormatter) {
        this.fullDateTimeFormatter = fullDateTimeFormatter;
    }

    public SimpleDateFormat getOnlyDateFormatter() {
        return onlyDateFormatter;
    }

    public void setOnlyDateFormatter(SimpleDateFormat onlyDateFormatter) {
        this.onlyDateFormatter = onlyDateFormatter;
    }

    public static DepressionDetectionApplication getApplication()
    {
        return INSTANCE;
    }
}
