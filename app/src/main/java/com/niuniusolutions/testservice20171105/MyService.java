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
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by LEO on 5/11/2017.
 */

public class MyService extends Service implements SensorEventListener {

    private static final String TAG = MyService.class.getSimpleName();
    private SensorManager mSensorManagr;
    private Sensor mSensor;
    private BroadcastReceiver mReceiver;
    private boolean screenOff = false;
    private boolean listenerOff = false;
    private Handler handler;

    @Override
    public void onCreate() {
        //Create our Sensor Manager
        mSensorManagr = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Accelerometer Sensor
        mSensor = mSensorManagr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //Register Sensor Listener
        Log.d(TAG, "Register first listener.");
        registerListener();
        registerReceiver();
        createLooper();
    }

    private void registerReceiver() {
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
                listenerOff = false;
                Log.i("tag1", "delayed");
                handler.postDelayed(this, 10 * 1000);
            }
        }, 10 * 1000);
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
        handler.removeCallbacksAndMessages(null);
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
        if (!listenerOff) {
            if (inclination < 40 && !screenOff) {
                Toast.makeText(this, "phone angle changed: inclination=" + inclination + " , Rotation=" + rotation, Toast.LENGTH_LONG).show();
                Log.d(TAG, "event detected, make toast.");
            }
        }

        listenerOff = true;
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
            if (intent.getAction().equals(intent.ACTION_SCREEN_OFF)) {
                screenOff = true;
                Log.d(TAG, "Screen is off");

            } else if (intent.getAction().equals(intent.ACTION_SCREEN_ON)) {
                screenOff = false;
                Log.d(TAG, "Screen is on");
            }
        }
    }


}