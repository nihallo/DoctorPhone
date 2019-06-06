package com.niuniusolutions.healthyniuFree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class HowItWorks extends AppCompatActivity {
    private Button mButtonBack;
    private Button mButtonReport;
    private AdView mAdView;
    private static final String TAG = HowItWorks.class.getSimpleName();
    private InterstitialAd mInterstitialAd;
    private int reportBtnClickCounter;// go to the next screen when click 3 time even ads is not loaded.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_it_works);

        mButtonReport = (Button) findViewById(R.id.mButtonReport);
        mButtonBack = (Button) findViewById(R.id.mButtonBack);

        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HowItWorks.this,Onboarding .class);
                HowItWorks.this.startActivity(intent);
            }
        });

        mButtonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // add interstitialAd
                if(mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }else if (reportBtnClickCounter<2){
                  //Toast preparing report
                    Toast.makeText(HowItWorks.this,R.string.preparingReport,Toast.LENGTH_LONG).show();
                    reportBtnClickCounter++;
                }else{
                    Intent intent = new Intent(HowItWorks.this,MainActivity.class);
                    HowItWorks.this.startActivity(intent);
                }
            }
        });


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //addTestDevice("0FD57F2D05ED2C6840BE6D79D98EB3F1")
                .build();
        mAdView.loadAd(adRequest);

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

        reportBtnClickCounter = 0;
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6853780483343253/8040879303");
        AdRequest adRequestInterstitial = new AdRequest.Builder()
                //addTestDevice("0FD57F2D05ED2C6840BE6D79D98EB3F1")
                .build();
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
                Intent intent = new Intent(HowItWorks.this,MainActivity.class);
                HowItWorks.this.startActivity(intent);
            }
        });
    }
}
