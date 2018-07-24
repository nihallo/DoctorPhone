package com.niuniusolutions.healthyniu;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Onboarding extends AppCompatActivity implements SensorEventListener{
    private SensorManager mSM;
    private Sensor mSensor;
    private TextView mDegreeText;
    private TextView mToastMsgText;
    private Button mStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        //create sensor manager
        mSM = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensor =mSM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSM.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);

        mDegreeText = (TextView) findViewById(R.id.mDegreeText);
        mToastMsgText = (TextView) findViewById(R.id.toastMsgText);
        mStartButton = (Button) findViewById(R.id.mStartButton);


            mStartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Onboarding.this,MainActivity.class);
                    Onboarding.this.startActivity(intent);
                }
            });

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] g = new float[3];
        g = sensorEvent.values.clone();
        float norm_Of_g = (float)Math.sqrt(g[0]*g[0]+g[1]*g[1]+g[2]*g[2]);
        //normalize the accelerometer vector
        g[0]=g[0]/norm_Of_g;
        g[1]=g[1]/norm_Of_g;
        g[2]=g[2]/norm_Of_g;
        int inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));
        mDegreeText.setText("Tilting Degree: "+inclination);
        if (inclination<40){
            mToastMsgText.setVisibility(View.VISIBLE);
        }else {
            mToastMsgText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // not in use
    }
}
