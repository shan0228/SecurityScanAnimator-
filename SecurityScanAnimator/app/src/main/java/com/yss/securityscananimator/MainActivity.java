package com.yss.securityscananimator;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SecurityScanView.OnClickListenter{
    SecurityScanView mSecurityScanView;
    RelativeLayout mRl_bg;
    private final int[] mColors = new int[]{
            0xFFCC0F50,
            0xFFDF6C2E,
            0xFF33BE26,
            0xFF27B5D1
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSecurityScanView= (SecurityScanView) findViewById(R.id.securityscnview);
        mRl_bg = (RelativeLayout) findViewById(R.id.rl_main);
        mSecurityScanView.setmPercentage(170);
        mSecurityScanView.setOnClickListenter(this);
        startColorChangeAnim();
    }
    public void startColorChangeAnim()
    {

        ObjectAnimator animator = ObjectAnimator.ofInt(mRl_bg, "backgroundColor", mColors);
        animator.setDuration(5000);
        animator.setEvaluator(new ArgbEvaluator());
        animator.start();
    }

    @Override
    public void onClick() {
        Toast.makeText(this,"dianji",Toast.LENGTH_SHORT).show();

    }
}
