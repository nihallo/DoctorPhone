package com.niuniusolutions.healthyniuFree;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Onboarding extends AppCompatActivity implements SensorEventListener{
    private static final String TAG = Onboarding.class.getSimpleName();
    private SensorManager mSM;
    private Sensor mSensor;
    private TextView mDegreeText;
    private TextView mToastMsgText;
    private Button mReportButton;
    private Button mHowItWorks;
    private FirebaseAnalytics mFirebaseAnalytics;

    private int inclination;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private int reportBtnClickCounter; // go to the next screen when click 3 time even ads is not loaded.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        if ( !isMyServiceRunning(MyService.class)){
        Intent intent = new Intent(this,MyService.class);
        startService(intent);
        }

        //create sensor manager
        mSM = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensor =mSM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSM.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);

        mDegreeText = findViewById(R.id.mDegreeText);
        mToastMsgText = findViewById(R.id.toastMsgText);
        mReportButton =  findViewById(R.id.mReportButton);
        mHowItWorks =  findViewById(R.id.mHowItWorks);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);



        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(mReportButton.getId()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mReportButton.getText().toString());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "report button on first screen");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                // add interstitialAd
                if(mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }else if (reportBtnClickCounter<2){
                    Toast.makeText(Onboarding.this,R.string.preparingReport,Toast.LENGTH_LONG).show();
                    reportBtnClickCounter++;
                }else{
                    Intent intent = new Intent(Onboarding.this,MainActivity.class);
                    Onboarding.this.startActivity(intent);
                }
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

        //add Admob
        MobileAds.initialize(this,"ca-app-pub-6853780483343253~2516912270");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequestBannerAds = new AdRequest.Builder()
                //addTestDevice("0FD57F2D05ED2C6840BE6D79D98EB3F1")
                .build();
        mAdView.loadAd(adRequestBannerAds);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i(TAG, "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i(TAG, "onAdOpened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.i(TAG, "onAdClicked");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i(TAG, "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.i(TAG, "onAdClosed");
            }
        });

        // add interstitialAd
        reportBtnClickCounter=0;
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6853780483343253/9750859873");
        AdRequest adRequestInterstitial = new AdRequest.Builder()
                //addTestDevice("0FD57F2D05ED2C6840BE6D79D98EB3F1")
                .build()
                ;
        mInterstitialAd.loadAd(adRequestInterstitial);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
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
