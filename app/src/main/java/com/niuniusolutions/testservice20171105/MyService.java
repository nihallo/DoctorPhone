package com.niuniusolutions.testservice20171105;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by LEO on 5/11/2017.
 */

public class MyService extends Service implements SensorEventListener {

    private static final String TAG =MyService.class.getSimpleName() ;
    private SensorManager mSensorManagr;
    private Sensor mSensor;
    private boolean isSensorStopped=false;

    private Calendar currentDate = Calendar.getInstance();
    private Calendar nextDate = Calendar.getInstance();

    int hour = currentDate.get(Calendar.HOUR);
    int minute = currentDate.get(Calendar.MINUTE);
    int currentTime=hour*100+minute;
    int futureTime=hour*100+minute+1;

    @Override
    public void onCreate() {
        Thread thread = new Thread();
        thread.setName("NiuniuDoctor");
        thread.start();
        //Create our Sensor Manager
        mSensorManagr = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Accelerometer Sensor
        mSensor = mSensorManagr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //Register Sensor Listenner
        mSensorManagr.registerListener(MyService.this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service started",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "On start command");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "OMG im destroyed",Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] g ;
        g = sensorEvent.values.clone();
        float norm_Of_g = (float)Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);
        // Normalize the accelerometer vector
        g[0] = g[0] / norm_Of_g;
        g[1] = g[1] / norm_Of_g;
        g[2] = g[2] / norm_Of_g;
        int inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));
        int rotation = (int) Math.round(Math.toDegrees(Math.atan2(g[0], g[1])));
        if(inclination<45) {
            Toast.makeText(this, "phone angle changed: inclination=" + inclination+" , Rotation="+rotation, Toast.LENGTH_LONG).show();
        }

        mSensorManagr.unregisterListener(MyService.this,mSensor);
        Log.d(TAG,"STOPPED LISTENER");

        //stop for 10 seconds and call register again
        new Thread(new Runnable(){
            public void run() {
                // TODO Auto-generated method stub
                while(true)
                {
                    try {
                        Thread.sleep(60*1000);
                        registerListener();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //not in use
    }

    private void registerListener(){
        //Register Sensor Listenner
        Log.d(TAG,"STARTED LISTENER");

        mSensorManagr.registerListener(MyService.this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
}
