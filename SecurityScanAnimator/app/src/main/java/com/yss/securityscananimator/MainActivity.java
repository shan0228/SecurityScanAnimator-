package com.yss.securityscananimator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    SecurityScanView mSecurityScanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSecurityScanView= (SecurityScanView) findViewById(R.id.securityscnview);
//        mSecurityScanView.starAnim();
    }
}
