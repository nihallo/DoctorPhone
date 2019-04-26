package com.niuniusolutions.healthyniu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HowItWorks extends AppCompatActivity {
    private Button mButtonBack;
    private Button mButtonReport;

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
                Intent intent = new Intent(HowItWorks.this, MainActivity.class);
                HowItWorks.this.startActivity(intent);
            }
        });
    }
}
