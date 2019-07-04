package com.shindev.rulecalculator;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.shindev.rulecalculator.common.Global;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onNextActivity();
            }
        }, 1500);
    }

    private void onNextActivity() {
        Global.showOtherActivity(this, LoginActivity.class, -1);
    }
}
