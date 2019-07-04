package com.shindev.rulecalculator;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.shindev.rulecalculator.common.Global;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black));
        }
    }

    public void onClickBackIcon(View view) {
        Global.showOtherActivity(this, MainActivity.class, 1);
    }

    public void onClickLblSupport(View view) {
        Global.showOtherActivity(this, SupportActivity.class, 0);
    }
}
