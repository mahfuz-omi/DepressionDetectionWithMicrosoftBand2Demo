package com.example.omi.depressiondetectionusingmicrosoftband2demo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebView;

public class PhoneSMSReceiver extends BroadcastReceiver {
    TelephonyManager telephony;

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("phone call arrived");

        MyPhoneStateListener phoneListener = new MyPhoneStateListener();
        telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public class MyPhoneStateListener extends PhoneStateListener
    {
        public void onCallStateChanged(int state, String incomingNumber)
        {
            switch (state)
            {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d("DEBUG", "IDLE");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d("DEBUG", "OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d("DEBUG", "RINGING");
                    break;
            }
        }

    }
}
