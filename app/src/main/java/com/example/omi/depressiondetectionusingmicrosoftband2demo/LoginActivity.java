package com.example.omi.depressiondetectionusingmicrosoftband2demo;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.application.DepressionDetectionApplication;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.UserCredentialsData;

import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.support.v4.app.AppOpsManagerCompat.MODE_ALLOWED;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText userNameEditText,ageEditText,sexEditText;
    Button loginButton;

    public static int USAGE_ACCESS_SETTINGS_REQUEST_CODE = 2;
    public static int PERMISSION_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.userNameEditText = this.findViewById(R.id.userNameEditText);
        this.ageEditText = this.findViewById(R.id.ageEditText);
        this.sexEditText = this.findViewById(R.id.sexEditText);

        this.loginButton = this.findViewById(R.id.loginButton);
        this.loginButton.setOnClickListener(this);


        // check login status
        if(DepressionDetectionApplication.getApplication().getUserCredentialsData() != null)
        {
            // user already logged in
            // move to DashboardActivity
            Intent intent = new Intent(this,DashboardActivity.class);
            startActivity(intent);
            this.finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // at first, check permissions
        this.checkPermissions();
    }

    public void checkPermissions()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            // request permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_CALL_LOG}, PERMISSION_REQUEST_CODE);
        }
        else
        {
            // permissions granted
            // now check settings
            if(this.checkSettings(this) == false)
            {
                // go to settings screen
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),USAGE_ACCESS_SETTINGS_REQUEST_CODE);
            }
            else
            {
                // permissions+settings ok
                // enable login button
                loginButton.setEnabled(true);
            }


        }
    }

    private boolean checkSettings(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.loginButton:
            {
                this.login();
                break;
            }
        }
    }

    public void login()
    {
        if(TextUtils.isEmpty(this.userNameEditText.getText().toString()) || TextUtils.isEmpty(this.ageEditText.getText().toString()) || TextUtils.isEmpty(this.sexEditText.getText().toString()))
        {
            Toast.makeText(this, "Input Data properly", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String userName = this.userNameEditText.getText().toString();
            String sex = this.sexEditText.getText().toString();
            int age = Integer.parseInt(this.ageEditText.getText().toString());

            UserCredentialsData userCredentialsData = new UserCredentialsData();
            userCredentialsData.setAge(age);
            userCredentialsData.setSex(sex);
            userCredentialsData.setUserName(userName);

            // save data
            DepressionDetectionApplication.getApplication().setUserCredentialsData(userCredentialsData);

            // move to Dashboard Activity
            Intent intent = new Intent(this,DashboardActivity.class);
            startActivity(intent);
            this.finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE)
        {
            for(int i=0;i<grantResults.length;i++)
            {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                {
                    // permission denied
                    Toast.makeText(this, "You have just denied permissions", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            // permission granted
            // check settings
            if(this.checkSettings(this) == false)
            {
                // go to settings screen
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),USAGE_ACCESS_SETTINGS_REQUEST_CODE);
            }
            else
            {
                loginButton.setEnabled(true);
            }
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == USAGE_ACCESS_SETTINGS_REQUEST_CODE)
//        {
//            if(resultCode == RESULT_OK)
//            {
//                // permission granted
//                Toast.makeText(this, "u just accepted the permission", Toast.LENGTH_SHORT).show();
//                loginButton.setEnabled(true);
//            }
//            else
//            {
//                Toast.makeText(this, "u just denied the permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
