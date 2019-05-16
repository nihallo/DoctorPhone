package com.niuniusolutions.healthyniu;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG =MainActivity.class.getSimpleName();
    private Button mBackButton;
    private Button mOnOffButton;
    private FirebaseAnalytics mFirebaseAnalytics;
    private SharedPreferences mPreferences;
    private int angle_0_15;
    private int angle_16_30;
    private int angle_31_45;
    private int angle_46_60;
    private int angle_61_75;
    private int angle_76_90;
    private int angle_91_above;
    private int total_count_all_angles;
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBackButton = (Button) findViewById(R.id.backButton);
        mOnOffButton = (Button) findViewById(R.id.onOffButton);

        //start the service when the screen loads, user did not need to click on start button.
        if(!isMyServiceRunning(MyService.class)) {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(MainActivity.this,Onboarding.class);
               MainActivity.this.startActivity(intent);
            }
        });

        mOnOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isMyServiceRunning(MyService.class)){
                    Intent intent = new Intent(MainActivity.this,MyService.class);
                    stopService(intent);
                    mOnOffButton.setText(R.string.StartBtnText);
                } else //service is not running
                {
                    Intent intent = new Intent(MainActivity.this,MyService.class);
                    startService(intent);
                    mOnOffButton.setText(R.string.StopBtnText);
                }

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(mOnOffButton.getId()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mOnOffButton.getText().toString());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "on-off button on report screen");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });


        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        angle_0_15=mPreferences.getInt(getString(R.string.key_0_15_angle),0);
        Log.d(TAG, "after reading from preference: angle_0_15 "+angle_0_15);
        angle_16_30=mPreferences.getInt(getString(R.string.key_16_30_angle),0);
        angle_31_45=mPreferences.getInt(getString(R.string.key_31_45_angle),0);
        angle_46_60=mPreferences.getInt(getString(R.string.key_46_60_angle),0);
        angle_61_75=mPreferences.getInt(getString(R.string.key_61_75_angle),0);
        angle_76_90=mPreferences.getInt(getString(R.string.key_76_90_angle),0);
        angle_91_above=mPreferences.getInt(getString(R.string.key_91_above_angle),0);

        total_count_all_angles=angle_0_15+angle_16_30+angle_31_45+angle_46_60+angle_61_75+angle_76_90+angle_91_above;

        barChart = (BarChart) findViewById(R.id.barchart);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry((angle_0_15+angle_16_30),0));
        //barEntries.add(new BarEntry(angle_16_30,1));
        barEntries.add(new BarEntry(angle_31_45,1));
        barEntries.add(new BarEntry(angle_46_60,2));
        //barEntries.add(new BarEntry(angle_61_75,4));
        barEntries.add(new BarEntry((angle_76_90+angle_61_75),3));
        barEntries.add(new BarEntry(angle_91_above,4));
        BarDataSet barDataSet = new BarDataSet(barEntries, "No of times checked");

        ArrayList<String> theAngleRange = new ArrayList<>();
       // theAngleRange.add("< 15°");
        theAngleRange.add(Math.round((angle_0_15+angle_16_30)*100/total_count_all_angles)+"%<30°");
        theAngleRange.add(Math.round(angle_31_45*100/total_count_all_angles)+"%<45°");
        theAngleRange.add(Math.round(angle_46_60*100/total_count_all_angles)+"%<60°");
        //theAngleRange.add("< 75°");
        theAngleRange.add(Math.round((angle_61_75+angle_76_90)*100/total_count_all_angles)+"%<90°");
        theAngleRange.add(Math.round( angle_91_above*100/total_count_all_angles)+"%>90°");

        BarData theData = new BarData(theAngleRange, barDataSet);
        barChart.setData(theData);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);

    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

