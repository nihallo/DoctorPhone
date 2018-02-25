package com.niuniusolutions.testservice20171105;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import java.util.Calendar;

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

    @Override
    public void onCreate() {
        Thread thread = new Thread();
        thread.setName("NiuniuDoctor");
        thread.start();
        //Create our Sensor Manager
        mSensorManagr = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Accelerometer Sensor
        mSensor = mSensorManagr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //Register Sensor Listener
        Log.d(TAG, "Register first listener.");
        registerListener();

        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        mReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        this.registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "On start command");
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
        if (inclination < 40) {
            Toast.makeText(this, "phone angle changed: inclination=" + inclination + " , Rotation=" + rotation, Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "----------Unregistered listener.");
        if(!listenerOff){
            mSensorManagr.unregisterListener(MyService.this, mSensor);
            listenerOff=true;
        }


        //stop for 10 seconds and call register again
        new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Log.d(TAG, "timer 1/60.");
                        Thread.sleep(60 * 1000);
                        Log.d(TAG, "timer 60/60.");
                        if(!screenOff){
                            registerListener();
                        }
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

    private void registerListener() {
        //Register Sensor Listener
        mSensorManagr.registerListener(MyService.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Listener registered.");
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