package com.shindev.rulecalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.shindev.rulecalculator.adapters.CreateParaAdapter;
import com.shindev.rulecalculator.common.Global;

import static com.shindev.rulecalculator.common.Global.gSelIndex;

public class ParameterActivity extends AppCompatActivity {
    ListView lst_para;
    CreateParaAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.txt_black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.txt_black));
        }

        initUIView();
    }

    private void initUIView() {
        lst_para = findViewById(R.id.lst_parameter_main);
        lst_para.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                return;
            }
            Global.gSelParaItem = Global.gCreateParams.get(position - 1);
            gSelIndex = position - 1;

            Global.showOtherActivity(ParameterActivity.this, ParaSettingActivity.class, 0);
        });
        mAdapter = new CreateParaAdapter(Global.gContext, Global.gCreateParams);
        lst_para.setAdapter(mAdapter);
    }

    public void onClickBackIcon(View view) {
        Global.showOtherActivity(this, MainActivity.class, 1);
    }

    private void onShowWaringAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.setpara_alert_waring)
                .setMessage(R.string.create_alert_add)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void onClickParameterNextBtn (View view) {
        if (Global.gCreateParams.size() == 0) {
            onShowWaringAlert();
            return;
        }

        Global.showOtherActivity(this, FormulaActivity.class, 0);
    }

    public void onClickParameterAddLlt(View view) {
        Global.gSelParaItem = null;
        Global.showOtherActivity(this, ParaSettingActivity.class, 0);
    }
}
