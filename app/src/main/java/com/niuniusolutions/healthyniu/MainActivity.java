package com.niuniusolutions.healthyniu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG =MainActivity.class.getSimpleName();
    private Button mBtnStart;
    private Button mBtnStop;
    private Button mBtnSetting;
    private FirebaseAnalytics mFirebaseAnalytics;
    private SharedPreferences mPreferences;
    private int angle_0_15;
    private int angle_16_30;
    private int angle_31_45;
    private int angle_46_60;
    private int angle_61_75;
    private int angle_76_90;
    private int angle_91_above;
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnStart = (Button) findViewById(R.id.btnStartService);
        mBtnStop = (Button) findViewById(R.id.btnStopService);

        //start the service when the screen loads, even if user did not click on start button.


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"On Start click");
               Intent intent = new Intent(MainActivity.this,MyService.class);
               startService(intent);
               mBtnStart.setText("Service Running!");

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(mBtnStart.getId()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mBtnStart.getText().toString());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "start button on second screen");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });

        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MyService.class);
                stopService(intent);
                mBtnStart.setText("Start Caring!");

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(mBtnStop.getId()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mBtnStop.getText().toString());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "stop button on second screen");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });


        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        angle_0_15=mPreferences.getInt(getString(R.string.key_0_15_angle),0);
        angle_16_30=mPreferences.getInt(getString(R.string.key_16_30_angle),0);
        angle_31_45=mPreferences.getInt(getString(R.string.key_31_45_angle),0);
        angle_46_60=mPreferences.getInt(getString(R.string.key_46_60_angle),0);
        angle_61_75=mPreferences.getInt(getString(R.string.key_61_75_angle),0);
        angle_76_90=mPreferences.getInt(getString(R.string.key_76_90_angle),0);
        angle_91_above=mPreferences.getInt(getString(R.string.key_91_above_angle),0);

        barChart = (BarChart) findViewById(R.id.barchart);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(angle_0_15,0));
        barEntries.add(new BarEntry(angle_16_30,1));
        barEntries.add(new BarEntry(angle_31_45,2));
        barEntries.add(new BarEntry(angle_46_60,3));
        barEntries.add(new BarEntry(angle_61_75,4));
        barEntries.add(new BarEntry(angle_76_90,5));
        barEntries.add(new BarEntry(angle_91_above,6));
        BarDataSet barDataSet = new BarDataSet(barEntries, "No of times appeared in that angle range");

        ArrayList<String> theAngleRange = new ArrayList<>();
        theAngleRange.add("< 15°");
        theAngleRange.add("< 30°");
        theAngleRange.add("< 45°");
        theAngleRange.add("< 60°");
        theAngleRange.add("< 75°");
        theAngleRange.add("< 90°");
        theAngleRange.add("> 90°");

        BarData theData = new BarData(theAngleRange, barDataSet);
        barChart.setData(theData);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);

    }
}
