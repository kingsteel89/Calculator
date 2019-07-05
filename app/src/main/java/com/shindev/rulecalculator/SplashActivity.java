package com.shindev.rulecalculator;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.shindev.rulecalculator.common.Global;

import static com.shindev.rulecalculator.R.*;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(color.txt_black));
        }

        new Handler().postDelayed(this::onNextActivity, 1500);
    }

    private void onNextActivity() {
        Global.showOtherActivity(this, LoginActivity.class, -1);
    }
}
