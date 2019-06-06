package com.niuniusolutions.healthyniuFree;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = MyService.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "boot receiver signal received");
        String action = intent.getAction();
        if (action !=null) {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED))  {
                //opne report screen
                Intent myIntent = new Intent(context, MainActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(myIntent);
            }
        }


    }
}