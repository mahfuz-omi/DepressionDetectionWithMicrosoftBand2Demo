package com.example.omi.depressiondetectionusingmicrosoftband2demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.omi.depressiondetectionusingmicrosoftband2demo.application.DepressionDetectionApplication;
import com.example.omi.depressiondetectionusingmicrosoftband2demo.model.UserCredentialsData;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText userNameEditText,ageEditText,sexEditText;
    Button loginButton;

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
        String userName = this.userNameEditText.getText().toString();
        String sex = this.sexEditText.getText().toString();
        int age = Integer.parseInt(this.ageEditText.getText().toString());

        if(userName != null && userName.isEmpty() == false && age > 0 && sex != null && sex.isEmpty() == false)
        {
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
}
