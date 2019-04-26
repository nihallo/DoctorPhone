package com.niuniusolutions.healthyniu;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.provider.FirebaseInitProvider;

public class Onboarding extends AppCompatActivity implements SensorEventListener{
    private SensorManager mSM;
    private Sensor mSensor;
    private TextView mDegreeText;
    private TextView mToastMsgText;
    private Button mStartButton;
    private Button mHowItWorks;
    private FirebaseAnalytics mFirebaseAnalytics;

    private int inclination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Intent intent = new Intent(this,MyService.class);
        startService(intent);

        //create sensor manager
        mSM = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensor =mSM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSM.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);

        mDegreeText = findViewById(R.id.mDegreeText);
        mToastMsgText = findViewById(R.id.toastMsgText);
        mStartButton =  findViewById(R.id.mStartButton);
        mHowItWorks =  findViewById(R.id.mHowItWorks);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(mStartButton.getId()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mStartButton.getText().toString());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "start button on first screen");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                Intent intent = new Intent(Onboarding.this,MainActivity.class);
                Onboarding.this.startActivity(intent);



            }
        });

        mHowItWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(mHowItWorks.getId()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mHowItWorks.getText().toString());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "How it works button on first screen");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                Intent intent = new Intent(Onboarding.this,HowItWorks.class);
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
        inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));

        mDegreeText.setText(""+inclination+"°");

        //show and hide the toast text
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
