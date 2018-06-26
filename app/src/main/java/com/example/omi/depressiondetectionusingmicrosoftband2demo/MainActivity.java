package com.example.omi.depressiondetectionusingmicrosoftband2demo;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.support.v4.app.AppOpsManagerCompat.MODE_ALLOWED;

public class MainActivity extends AppCompatActivity {

    public static int USAGE_ACCESS_SETTINGS_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(this.checkForPermission(this))
        {
            // having permission
            Toast.makeText(this, "having permission", Toast.LENGTH_SHORT).show();

            this.grabUsageData();
        }
        else
        {
            // no permission
            Toast.makeText(this, "no permission", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),USAGE_ACCESS_SETTINGS_REQUEST_CODE);

        }
    }


    public void grabUsageData()
    {
        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        final long start = calendar.getTimeInMillis();
        final long end = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
                System.out.println("");
                for(UsageStats usageStats:stats)
                {
                    String packageName = usageStats.getPackageName();
                    if(packageName.equalsIgnoreCase("com.facebook.katana") || packageName.equalsIgnoreCase("com.facebook.orca") || packageName.equalsIgnoreCase("com.instagram.android"))
                        System.out.println(usageStats.getPackageName()+"  :  "+getTimeFromMilliseconds(usageStats.getTotalTimeInForeground()));
                }
            }
        }).start();

        System.out.println("");
    }


    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == USAGE_ACCESS_SETTINGS_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                // permission granted
                Toast.makeText(this, "u just accepted the permission", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "u just denied the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getTimeFromMilliseconds(long millis)
    {
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d : %02d : %02d : %d", hour, minute, second, millis);
        return time;
    }
}
