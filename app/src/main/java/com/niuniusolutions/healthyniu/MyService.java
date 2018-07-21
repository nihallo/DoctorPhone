package com.niuniusolutions.healthyniu;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

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
                    registerListener();
                    listenerOff = false;
                    Log.i("tag1", "delayed");
                }
                handler.postDelayed(this, 60 * 1000);
            }
        }, 60 * 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "On start command");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,notificationIntent,0);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Healthy Neck")
                .setContentText("Raise ur Phone Higher & Save the Neck!")
                .setSmallIcon(R.drawable.notificationicon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(333,notification);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "OMG im destroyed", Toast.LENGTH_SHORT).show();
        unregisterReceiver(mReceiver);
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
        if (inclination < 40 & !screenOff) {
            //Toast.makeText(this, "phone angle changed: inclination=" + inclination + " , Rotation=" + rotation, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Raise e Phone Higher, Heads Up, Protect the Neck!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "event detected, make toast.");
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
        Log.d(TAG, "Listener registered.");
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


}