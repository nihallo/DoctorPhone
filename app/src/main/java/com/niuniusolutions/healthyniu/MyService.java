package com.niuniusolutions.healthyniu;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import static com.niuniusolutions.healthyniu.App.CHANNEL_ID;

/**
 * Created by LEO on 5/11/2017.
 */

public class MyService extends Service implements SensorEventListener {

    private static final String TAG = MyService.class.getSimpleName();
    private SensorManager mSensorManagr;
    private Sensor mSensor;
    private BroadcastReceiver mReceiver;
    private boolean screenOff=false;
    private boolean listenerOff=false;
    private Handler handler;
    private int alertAngle;
    private int alertFrequency;
    private int angle_0_15;
    private int angle_16_30;
    private int angle_31_45;
    private int angle_46_60;
    private int angle_61_75;
    private int angle_76_90;
    private int angle_91_above;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private int failedCounter=0;
    private int flatCounter=0;

    @Override
    public void onCreate() {

        Thread thread = new Thread();
        thread.setName("HealthyNeck");
        thread.start();
        //Create our Sensor Manager
        mSensorManagr = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Accelerometer Sensor
        mSensor = mSensorManagr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Register Sensor Listener
        registerReceiver();
        createLooper();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        alertAngle = 40;//mPreferences.getInt(getString(R.string.key_alert_angle),40);
        alertFrequency = 1;//mPreferences.getInt(getString(R.string.key_alert_frequency),1);
        angle_0_15=mPreferences.getInt(getString(R.string.key_0_15_angle),0);
        angle_16_30=mPreferences.getInt(getString(R.string.key_16_30_angle),0);
        angle_31_45=mPreferences.getInt(getString(R.string.key_31_45_angle),0);
        angle_46_60=mPreferences.getInt(getString(R.string.key_46_60_angle),0);
        angle_61_75=mPreferences.getInt(getString(R.string.key_61_75_angle),0);
        angle_76_90=mPreferences.getInt(getString(R.string.key_76_90_angle),0);
        angle_91_above=mPreferences.getInt(getString(R.string.key_91_above_angle),0);

    }

    private void registerReceiver(){
        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        mReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        this.registerReceiver(mReceiver, filter);
    }


    private void createLooper() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!screenOff) {
                    Log.d(TAG, "Listener registered inside looper");
                    registerListener();
                    listenerOff = false;
                    Log.i(TAG, "Delayed in looper.");
                }
                handler.postDelayed(this, 62 * 1000*alertFrequency);
            }
        }, 62 * 1000*alertFrequency);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,R.string.toast_msg_service_started, Toast.LENGTH_LONG).show();
        Log.d(TAG, "On start command");

        Intent notificationIntent = new Intent(this, Onboarding.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,notificationIntent,0);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Healthy Neck")
                .setContentText("Good reading posture = don't bend the neck.")
                .setSmallIcon(R.drawable.notificationicon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(333,notification);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
       // Toast.makeText(this, "OMG im destroyed", Toast.LENGTH_SHORT).show();
        unregisterReceiver(mReceiver);
        unregisterListener();
        Log.d(TAG, "on destory, after unregister listener.");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] g;
        g = sensorEvent.values.clone();
        float norm_Of_g = (float) Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);
        // Normalize the accelerometer vector
        g[0] = g[0] / norm_Of_g;
        g[1] = g[1] / norm_Of_g;
        g[2] = g[2] / norm_Of_g;
        int inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));
        int rotation = (int) Math.round(Math.toDegrees(Math.atan2(g[0], g[1])));

        //record angle
        if (inclination<=15){
            angle_0_15++;
            mEditor.putInt(getString(R.string.key_0_15_angle),angle_0_15);
        } else if (inclination<=30 &inclination>15) {
            angle_16_30++;
            mEditor.putInt(getString(R.string.key_16_30_angle),angle_16_30);
        } else if (inclination<=45  &inclination>30) {
            angle_31_45++;
            mEditor.putInt(getString(R.string.key_31_45_angle),angle_31_45);
        } else if (inclination<=60 &inclination>45) {
            angle_46_60++;
            mEditor.putInt(getString(R.string.key_46_60_angle),angle_46_60);
        } else if (inclination<=75 &inclination>60) {
            angle_61_75++;
            mEditor.putInt(getString(R.string.key_61_75_angle),angle_61_75);
        } else if (inclination<=90 &inclination>75) {
            angle_76_90++;
            mEditor.putInt(getString(R.string.key_76_90_angle),angle_76_90);
        } else if(inclination>90){
            angle_91_above++;
            mEditor.putInt(getString(R.string.key_91_above_angle),angle_91_above);
        }
        mEditor.commit();



        // 0,1 is considered as lying flat, no one is using
        if(inclination!=0 & inclination!=1) {
            if (inclination < alertAngle & !screenOff) { // screen is not off and angle is less than ok
                failedCounter++;
                //alertAngle = mPreferences.getInt(getString(R.string.key_alert_angle),40);
                //alertFrequency = mPreferences.getInt(getString(R.string.key_alert_frequency),1);

                Log.d(TAG, "inside: alert angle:" + alertAngle + ", alert frequency: " + alertFrequency);

                Toast.makeText(this, "Failed "+failedCounter + " times - Healthy Neck Check :(", Toast.LENGTH_LONG).show();
                Log.d(TAG, "event detected, make toast." + " angle: " + inclination + ", Limit: " + alertAngle + " for every " + alertFrequency + " mins, " + "failed counter: " + failedCounter);

                if (failedCounter >30) { // continuously failed more than 30 times
                    firebaseEventUpdate( ""+"Failed",
                            "failed counter more than 30",
                            "failed angle is "+ inclination );
                } else// failed within 30 times
                {
                    firebaseEventUpdate( "Failed",
                        "failed counter " + failedCounter,
                        "failed angle is " + inclination);
                }

            } else if (!screenOff) { // angle is ok and screen is not off
                failedCounter = 0;
            }
            //reset to 0 if not flat anymore
            flatCounter=0;
        }  else { // angle is 0,1, lying flat, no one is using
            firebaseEventUpdate( "Flat",
                                "Flat counter " + flatCounter,
                                "angle is " + inclination);
            flatCounter++;
                }

        Log.d(TAG, "Unregistered listener.");
        if(!listenerOff){
            unregisterListener();
            listenerOff=true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //not in use
    }

    private void registerListener() {
        //Register Sensor Listener
        mSensorManagr.registerListener(MyService.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void unregisterListener() {
        mSensorManagr.unregisterListener(this,mSensor);
        Log.i(TAG, "Listener unregistered");
    }

    private class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {



            if( intent.getAction().equals(intent.ACTION_SCREEN_OFF)) {
                screenOff =true;
                Log.d(TAG, "Screen is off");

            }else if (intent.getAction().equals(intent.ACTION_SCREEN_ON)){
                screenOff =false;
                Log.d(TAG, "Screen is on");
            }
        }
    }

    private void firebaseEventUpdate(String itemIdString, String itemName, String ContentType){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemIdString);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, ContentType);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


}